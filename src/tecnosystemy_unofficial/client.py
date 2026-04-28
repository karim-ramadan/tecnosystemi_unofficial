"""
TecnoClient: orchestrates transport, IDP management, and request sessions.

Typical usage::

    from tecnosystemy_unofficial import TecnoClient
    from tecnosystemy_unofficial.devices.pico import PicoDevice

    with TecnoClient(ip="192.168.4.1") as client:
        pico = PicoDevice(client, pin="1234")
        state = pico.get_state()
        pico.turn_on()
"""

import json
import logging
import threading
from pathlib import Path
from typing import Optional

from .idp import IDPManager
from .session import RequestSession
from .templates_loader import TemplateLoader
from .transport import UDPTransport

logger = logging.getLogger(__name__)


class TecnoClient:
    """
    High-level UDP client for Tecnosystemi devices.

    Manages the transport, IDP allocation, and per-request sessions.  Supports
    both direct dict-based commands and Jinja2 template-based commands.

    Args:
        ip:          Device IP address.
        send_port:   UDP port the *device* listens on (commands go here).
        recv_port:   UDP port the *client* listens on (responses come here).
        idp_manager: Custom IDPManager.  Defaults to in-memory allocation.
        template_dirs: Extra Jinja2 template directories (searched first).
        timeout:     Default timeout in seconds for waiting on responses.
    """

    DEFAULT_SEND_PORT = 40070
    DEFAULT_RECV_PORT = 40069

    def __init__(
        self,
        ip: str,
        send_port: int = DEFAULT_SEND_PORT,
        recv_port: int = DEFAULT_RECV_PORT,
        idp_manager: Optional[IDPManager] = None,
        template_dirs: Optional[list[Path]] = None,
        timeout: float = 10.0,
    ):
        self.ip = ip
        self.timeout = timeout

        self.transport = UDPTransport(ip, send_port, recv_port)
        self.idp_manager = idp_manager or IDPManager()
        self.template_loader = TemplateLoader(template_dirs)

        self._sessions: dict[int, RequestSession] = {}
        self._sessions_lock = threading.Lock()

        self.transport.add_packet_handler(self._route_packet)

    # ------------------------------------------------------------------
    # Lifecycle
    # ------------------------------------------------------------------

    def start(self):
        """Open sockets and start the listener thread."""
        self.transport.start()

    def stop(self):
        """Stop the listener and close sockets."""
        self.transport.stop()

    def __enter__(self):
        self.start()
        return self

    def __exit__(self, *_):
        self.stop()

    # ------------------------------------------------------------------
    # Command API
    # ------------------------------------------------------------------

    def send_command(
        self,
        payload: dict,
        timeout: Optional[float] = None,
    ) -> Optional[dict]:
        """
        Send a command dict and wait for the device response.

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

        idp = self.idp_manager.acquire()
        payload["idp"] = idp
        payload["frm"] = "app"

        cmd = payload.get("cmd", "")
        session = RequestSession(idp, cmd)

        with self._sessions_lock:
            self._sessions[idp] = session

        try:
            raw = json.dumps(payload).encode("utf-8")
            logger.debug("→ [idp=%d] %s", idp, payload)
            self.transport.send(raw)

            result = session.wait(effective_timeout)

            if result is None:
                logger.warning(
                    "Timeout (%.1fs) waiting for response to idp=%d cmd=%s",
                    effective_timeout,
                    idp,
                    cmd,
                )
                return None

            # Protocol: client must ACK the device when it receives res != 99
            if result.get("res") != 99:
                self._send_ack(idp, cmd)

            return result
        finally:
            with self._sessions_lock:
                self._sessions.pop(idp, None)
            self.idp_manager.release(idp)

    def send_template(
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
        # Strip fields managed by send_command
        payload.pop("idp", None)
        payload.pop("frm", None)
        return self.send_command(payload, timeout=timeout)

    # ------------------------------------------------------------------
    # Internal helpers
    # ------------------------------------------------------------------

    def _send_ack(self, idp: int, cmd: str = ""):
        ack: dict = {"idp": idp, "frm": "app", "res": 99}
        if cmd:
            ack["cmd"] = cmd
        try:
            self.transport.send(json.dumps(ack).encode("utf-8"))
            logger.debug("← ACK [idp=%d]", idp)
        except Exception:
            logger.warning("Failed to send ACK for idp=%d", idp)

    def _route_packet(self, packet: dict):
        idp = packet.get("idp")
        if idp is None:
            return
        with self._sessions_lock:
            session = self._sessions.get(idp)
        if session:
            logger.debug("← [idp=%d] %s", idp, packet)
            session.handle_packet(packet)
        else:
            logger.debug("Received packet for unknown/expired idp=%d, ignoring", idp)
