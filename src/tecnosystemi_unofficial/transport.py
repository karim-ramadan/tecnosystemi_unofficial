"""
UDP transport layer: manages the per-device send socket and delegates
receiving to the process-level SharedUDPListener.

Tecnosystemi devices use two separate UDP channels:
  - Send:    local → device at port 40070
  - Receive: device → local at port 40069

All TecnoClient instances share one socket bound to the receive port (via
SharedUDPListener).  Incoming packets are dispatched to each transport by the
source IP address of the sending device.
"""

import errno
import logging
import socket
import time
from typing import Callable

from .shared_listener import SharedUDPListener

logger = logging.getLogger(__name__)


class UDPTransport:
    """
    Per-device UDP transport.

    Owns the (unbound) send socket for one device.  Receiving is handled by
    the process-level :class:`SharedUDPListener` so multiple transports can
    coexist without conflicting on the receive port.

    Usage::

        transport = UDPTransport("192.168.4.1", 40070, 40069)
        transport.add_packet_handler(my_handler)
        with transport:
            transport.send(b'...')
    """

    def __init__(
        self,
        ip: str,
        send_port: int,
        receive_port: int,
    ):
        # Normalize to a canonical IPv4 string so it matches addr[0] from recvfrom.
        self.ip = socket.gethostbyname(ip)
        self.send_port = send_port
        self.receive_port = receive_port

        self._send_sock: socket.socket | None = None
        self._packet_handlers: list[Callable[[dict], None]] = []
        self._started = False

    # ------------------------------------------------------------------
    # Public API
    # ------------------------------------------------------------------

    def add_packet_handler(self, handler: Callable[[dict], None]) -> None:
        """Register a callback that is invoked for every received JSON packet."""
        self._packet_handlers.append(handler)

    def start(self) -> None:
        """Open the send socket and register with the shared listener."""
        if self._started:
            return

        self._send_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        SharedUDPListener.get(self.receive_port).register(self.ip, self._dispatch_packet)
        self._started = True
        logger.debug(
            "UDPTransport started: listening on :%d (shared), sending to %s:%d",
            self.receive_port,
            self.ip,
            self.send_port,
        )

    def stop(self) -> None:
        """Unregister from the shared listener and close the send socket."""
        if not self._started:
            return
        SharedUDPListener.get(self.receive_port).unregister(self.ip, self._dispatch_packet)
        if self._send_sock:
            try:
                self._send_sock.close()
            except Exception:
                pass
            self._send_sock = None
        self._started = False
        logger.debug("UDPTransport stopped for %s", self.ip)

    def send(self, data: bytes) -> None:
        """Send raw bytes to the device, retrying once on transient routing errors."""
        if not self._send_sock:
            raise RuntimeError("Transport is not started. Call start() first.")
        for attempt in range(3):
            try:
                self._send_sock.sendto(data, (self.ip, self.send_port))
                return
            except OSError as exc:
                # EHOSTUNREACH / ENETUNREACH: transient on macOS when the ARP cache for
                # the target IP is stale right after a discovery scan; 0.5 s lets the
                # kernel refresh its neighbour table before the next attempt.
                # EPERM: Linux propagates a stored ICMP error (e.g. ICMP type 3/code 13
                # from Docker's iptables REJECT rule) from the socket's error queue on
                # the next sendto(); recreating the socket flushes the queue immediately,
                # so no sleep is needed.
                if exc.errno in (errno.EHOSTUNREACH, errno.ENETUNREACH, errno.EPERM) and attempt < 2:
                    if exc.errno == errno.EPERM:
                        self._send_sock.close()
                        self._send_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
                    else:
                        time.sleep(0.5)
                    logger.warning(
                        "send attempt %d failed (%s), retrying …",
                        attempt + 1,
                        exc,
                    )
                    continue
                raise

    def __enter__(self):
        self.start()
        return self

    def __exit__(self, *_):
        self.stop()

    # ------------------------------------------------------------------
    # Internal
    # ------------------------------------------------------------------

    def _dispatch_packet(self, packet: dict) -> None:
        """Forward a received packet to all registered handlers."""
        for handler in self._packet_handlers:
            try:
                handler(packet)
            except Exception:
                logger.exception("Packet handler raised an exception")
