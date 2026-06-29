"""
TecnoClient: orchestrates transport, IDP management, and request sessions.

Typical usage (async)::

    from tecnosystemi_unofficial import TecnoClient
    from tecnosystemi_unofficial.devices.pico import PicoDevice

    async with TecnoClient(ip="192.168.4.1") as client:
        pico = PicoDevice(client, pin="1234")
        state = await pico.get_state()
        await pico.turn_on()

The client maintains a rolling circular buffer of every packet received from
the device.  Call :meth:`get_recent_packets` to inspect that history at any
time (e.g. for debugging or replay).
"""

import asyncio
import collections
import json
import logging
import threading
import time
from pathlib import Path
from typing import Optional

from .idp import IDPManager
from .session import RequestSession
from .templates_loader import TemplateLoader
from .transport import UDPTransport

# Commands that complete on res:99 alone (no data packet follows).
_CONTROL_COMMANDS: frozenset[str] = frozenset(
    {
        "upd_pico",
        "upd_P6X",
        "check_led",
        "upd_cu",
        "upd_fasce",
        "upd_zona",
        "upd_date",
        "config",
    }
)

logger = logging.getLogger(__name__)


class TecnoClient:
    """
    High-level async UDP client for Tecnosystemi devices.

    The listener always runs in the background.  Every received packet is
    stored in a circular buffer (keyed by ``idp``) and used to resolve the
    corresponding pending :class:`asyncio.Future`.  No caller thread is ever
    blocked waiting for a response.

    Args:
        ip:            Device IP address.
        send_port:     UDP port the *device* listens on (commands go here).
        recv_port:     UDP port the *client* listens on (responses come here).
        template_dirs: Extra Jinja2 template directories (searched first).
        timeout:       Default timeout in seconds for waiting on responses.
        buffer_size:   Number of received packets to keep in the rolling buffer.
    """

    DEFAULT_SEND_PORT = 40070
    DEFAULT_RECV_PORT = 40069
    BUFFER_SIZE = 200

    def __init__(
        self,
        ip: str,
        send_port: int = DEFAULT_SEND_PORT,
        recv_port: int = DEFAULT_RECV_PORT,
        idp_manager: Optional[IDPManager] = None,
        template_dirs: Optional[list[Path]] = None,
        timeout: float = 10.0,
        buffer_size: int = BUFFER_SIZE,
    ):
        self.ip = ip
        self.timeout = timeout

        self.transport = UDPTransport(ip, send_port, recv_port)
        self.idp_manager = idp_manager or IDPManager()
        self.template_loader = TemplateLoader(template_dirs)

        self._sessions: dict[int, RequestSession] = {}
        # Protects _sessions and _pending for access from the listener thread.
        # Never held across an await.
        self._sessions_lock = threading.Lock()
        # Maps idp → (asyncio.Future, event-loop) so _route_packet can resolve
        # the future from the listener thread via call_soon_threadsafe.
        self._pending: dict[int, tuple[asyncio.Future, asyncio.AbstractEventLoop]] = {}
        # Rolling buffer: every received packet, regardless of idp state.
        self._received: collections.deque[dict] = collections.deque(maxlen=buffer_size)

        self.transport.add_packet_handler(self._route_packet)

    # ------------------------------------------------------------------
    # Lifecycle
    # ------------------------------------------------------------------

    def start(self) -> None:
        """Open sockets and start the listener thread."""
        self.transport.start()

    def stop(self) -> None:
        """Stop the listener and close sockets."""
        self.transport.stop()

    def __enter__(self) -> "TecnoClient":
        self.start()
        return self

    def __exit__(self, *_) -> None:
        self.stop()

    async def __aenter__(self) -> "TecnoClient":
        self.start()
        return self

    async def __aexit__(self, *_) -> None:
        self.stop()

    # ------------------------------------------------------------------
    # Command API
    # ------------------------------------------------------------------

    async def send_command(
        self,
        payload: dict,
        timeout: Optional[float] = None,
    ) -> Optional[dict]:
        """
        Send a command dict and asynchronously await the device response.

        ``idp`` and ``frm`` are injected automatically; any values in
        *payload* for those keys will be overwritten.

        Args:
            payload: Command fields, e.g. ``{"cmd": "pico_info", "pin": "-1"}``.
            timeout: Override the client's default timeout.

        Returns:
            The response dict, or ``None`` on timeout.
        """
        effective_timeout = timeout if timeout is not None else self.timeout
        payload = dict(payload)  # never mutate the caller's dict

        idp = await self.idp_manager.acquire()
        payload["idp"] = idp
        payload["frm"] = "app"

        cmd = payload.get("cmd", "")
        session = RequestSession(idp, cmd, expects_data=cmd not in _CONTROL_COMMANDS)

        loop = asyncio.get_running_loop()
        future: asyncio.Future = loop.create_future()

        with self._sessions_lock:
            self._sessions[idp] = session
            self._pending[idp] = (future, loop)

        try:
            raw = json.dumps(payload).encode("utf-8")
            logger.debug("→ [idp=%d] %s", idp, payload)
            # Send without blocking the event loop (transport may retry with sleep).
            await asyncio.to_thread(self.transport.send, raw)

            try:
                # Shield the future so that a timeout cancels only the wait,
                # not the underlying future (avoids InvalidStateError in
                # _route_packet if a late packet arrives after timeout).
                result = await asyncio.wait_for(
                    asyncio.shield(future), timeout=effective_timeout
                )
            except asyncio.TimeoutError:
                logger.warning(
                    "Timeout (%.1fs) waiting for response to idp=%d cmd=%s",
                    effective_timeout,
                    idp,
                    cmd,
                )
                return None

            # Protocol: client must ACK the device when it receives res != 99.
            if result.get("res") != 99:
                await asyncio.to_thread(self._send_ack, idp, cmd)

            return result
        finally:
            with self._sessions_lock:
                self._sessions.pop(idp, None)
                self._pending.pop(idp, None)
            self.idp_manager.release(idp)

    async def send_template(
        self,
        template_name: str,
        timeout: Optional[float] = None,
        **context,
    ) -> Optional[dict]:
        """
        Render a Jinja2 template and send the resulting command.

        ``idp`` and ``frm`` are injected by the client even if the template
        includes them (template values are stripped and replaced).

        Args:
            template_name: Path relative to any template dir,
                           e.g. ``"pico/upd_pico.json.j2"``.
            timeout:       Override the client's default timeout.
            context:       Variables passed to the template.

        Returns:
            The response dict, or ``None`` on timeout.
        """
        rendered = self.template_loader.render(template_name, **context)
        payload = json.loads(rendered)
        payload.pop("idp", None)
        payload.pop("frm", None)
        return await self.send_command(payload, timeout=timeout)

    # ------------------------------------------------------------------
    # Buffer query
    # ------------------------------------------------------------------

    def get_recent_packets(
        self,
        idp: Optional[int] = None,
        limit: Optional[int] = None,
    ) -> list[dict]:
        """
        Return a snapshot of the circular receive buffer.

        Args:
            idp:   If given, return only entries matching this IDP.
            limit: If given, return only the last *limit* entries.

        Each entry is a dict with keys ``ts`` (monotonic timestamp),
        ``ip`` (device IP), ``idp``, and ``packet``.
        """
        entries: list[dict] = list(self._received)
        if idp is not None:
            entries = [e for e in entries if e["idp"] == idp]
        if limit is not None:
            entries = entries[-limit:]
        return entries

    # ------------------------------------------------------------------
    # Internal helpers
    # ------------------------------------------------------------------

    def _send_ack(self, idp: int, cmd: str = "") -> None:
        ack: dict = {"idp": idp, "frm": "app", "res": 99}
        if cmd:
            ack["cmd"] = cmd
        try:
            self.transport.send(json.dumps(ack).encode("utf-8"))
            logger.debug("← ACK [idp=%d]", idp)
        except Exception:
            logger.warning("Failed to send ACK for idp=%d", idp)

    def _route_packet(self, packet: dict) -> None:
        """
        Called by the transport layer (listener thread) for every received packet.

        Always appends the packet to the circular buffer.  If there is a
        pending command for this ``idp``, advances its session state and
        resolves the associated Future when the session is complete.
        """
        idp = packet.get("idp")
        if idp is None:
            return

        self._received.append(
            {"ts": time.monotonic(), "ip": self.ip, "idp": idp, "packet": packet}
        )

        with self._sessions_lock:
            session = self._sessions.get(idp)

        if session is None:
            logger.debug("Received packet for unknown/expired idp=%d and ip: %s, ignoring", idp, self.ip)
            return

        logger.debug("← [idp=%d] %s", idp, packet)
        complete = session.handle_packet(packet)

        if complete:
            with self._sessions_lock:
                pending = self._pending.get(session.idp)
            if pending is not None:
                future, loop = pending
                if not future.done():
                    try:
                        loop.call_soon_threadsafe(future.set_result, session.response)
                    except RuntimeError:
                        pass  # loop already closed
