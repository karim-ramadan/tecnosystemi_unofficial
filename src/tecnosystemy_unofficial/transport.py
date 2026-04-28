"""
UDP transport layer: manages the send socket and persistent listener thread.

Tecnosystemi devices use two separate UDP channels:
  - Send:    local → device at port 40070
  - Receive: device → local at port 40069

The listener runs in a dedicated thread and dispatches decoded JSON packets to
all registered handlers.
"""

import json
import logging
import socket
import threading
from typing import Callable

logger = logging.getLogger(__name__)


class UDPTransport:
    """
    Low-level UDP transport with a persistent background listener.

    Usage::

        transport = UDPTransport("192.168.4.1", 40070, 40069)
        transport.add_packet_handler(my_handler)
        with transport:
            transport.send(b'...')
    """

    BUFFER_SIZE = 4096
    RECV_THREAD_TIMEOUT = 1.0  # seconds; controls how quickly stop() returns

    def __init__(
        self,
        ip: str,
        send_port: int,
        receive_port: int,
    ):
        self.ip = ip
        self.send_port = send_port
        self.receive_port = receive_port

        self._send_sock: socket.socket | None = None
        self._recv_sock: socket.socket | None = None
        self._recv_thread: threading.Thread | None = None
        self._stop_event = threading.Event()
        self._packet_handlers: list[Callable[[dict], None]] = []
        self._started = False

    # ------------------------------------------------------------------
    # Public API
    # ------------------------------------------------------------------

    def add_packet_handler(self, handler: Callable[[dict], None]):
        """Register a callback that is invoked for every received JSON packet."""
        self._packet_handlers.append(handler)

    def start(self):
        """Open sockets and start the listener thread."""
        if self._started:
            return

        self._send_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

        self._recv_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self._recv_sock.settimeout(self.RECV_THREAD_TIMEOUT)
        self._recv_sock.bind(("", self.receive_port))

        self._stop_event.clear()
        self._recv_thread = threading.Thread(
            target=self._recv_loop,
            name="tecno-udp-listener",
            daemon=False,
        )
        self._recv_thread.start()
        self._started = True
        logger.debug(
            "UDPTransport started: listening on :%d, sending to %s:%d",
            self.receive_port,
            self.ip,
            self.send_port,
        )

    def stop(self):
        """Signal the listener to stop and close all sockets."""
        if not self._started:
            return
        self._stop_event.set()
        if self._recv_thread:
            self._recv_thread.join(timeout=self.RECV_THREAD_TIMEOUT + 1.0)
        for sock in (self._send_sock, self._recv_sock):
            if sock:
                try:
                    sock.close()
                except Exception:
                    pass
        self._send_sock = None
        self._recv_sock = None
        self._started = False
        logger.debug("UDPTransport stopped")

    def send(self, data: bytes):
        """Send raw bytes to the device."""
        if not self._send_sock:
            raise RuntimeError("Transport is not started. Call start() first.")
        self._send_sock.sendto(data, (self.ip, self.send_port))

    def __enter__(self):
        self.start()
        return self

    def __exit__(self, *_):
        self.stop()

    # ------------------------------------------------------------------
    # Internal
    # ------------------------------------------------------------------

    def _recv_loop(self):
        assert self._recv_sock is not None
        while not self._stop_event.is_set():
            try:
                data, addr = self._recv_sock.recvfrom(self.BUFFER_SIZE)
            except socket.timeout:
                continue
            except OSError:
                if not self._stop_event.is_set():
                    logger.exception("Socket error in receive loop")
                break

            try:
                packet = json.loads(data.decode("utf-8"))
            except (json.JSONDecodeError, UnicodeDecodeError):
                logger.debug("Ignoring non-JSON packet from %s", addr)
                continue

            logger.debug("Received from %s: %s", addr, packet)
            for handler in self._packet_handlers:
                try:
                    handler(packet)
                except Exception:
                    logger.exception("Packet handler raised an exception")

        logger.debug("Listener thread exiting")
