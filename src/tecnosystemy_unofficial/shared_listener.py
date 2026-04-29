"""
Process-level shared UDP listener.

A single socket is bound to the receive port per process, shared across all
UDPTransport instances listening on that port.  Incoming packets are dispatched
to the handler registered for the packet's source IP.

Lifecycle
---------
The socket is opened on the first ``register`` call and closed when the last
handler is removed via ``unregister``.  This means other code (e.g. a discovery
scan) can rebind the port after all clients have stopped.

Same-device multi-client
------------------------
If two TecnoClient instances target the **same** device IP they will both
receive every packet from that device.  Because TecnoClient already ignores
packets with an unknown ``idp``, this is safe as long as each client has its
own IDPManager — in-flight IDPs from different clients will never collide
because each IDPManager tracks only its own allocations.  Using a shared
IDPManager for same-IP clients is not supported.
"""

import json
import logging
import socket
import threading
from typing import Callable

logger = logging.getLogger(__name__)

_HandlerFn = Callable[[dict], None]
_RawHandlerFn = Callable[[dict, tuple], None]


class SharedUDPListener:
    """
    Singleton UDP listener for a given local port.

    Register per-device-IP handlers; every UDP packet arriving from that IP
    is forwarded to the matching handler(s).
    """

    _RECV_TIMEOUT = 1.0  # controls how quickly the thread notices a closed socket
    BUFFER_SIZE = 4096

    # Class-level registry: port → instance
    _instances: dict[int, "SharedUDPListener"] = {}
    _instances_lock = threading.Lock()

    @classmethod
    def get(cls, port: int) -> "SharedUDPListener":
        """Return (or create) the singleton listener for *port*."""
        with cls._instances_lock:
            if port not in cls._instances:
                cls._instances[port] = cls(port)
            return cls._instances[port]

    def __init__(self, port: int) -> None:
        self._port = port
        self._sock: socket.socket | None = None
        self._thread: threading.Thread | None = None
        self._handlers: dict[str, list[_HandlerFn]] = {}
        self._raw_handlers: list[_RawHandlerFn] = []  # receive (packet, addr) from any IP
        self._lock = threading.Lock()

    # ------------------------------------------------------------------
    # Public API
    # ------------------------------------------------------------------

    def register(self, ip: str, handler: _HandlerFn) -> None:
        """
        Register *handler* to receive packets arriving from *ip*.

        The socket is opened (and the listener thread started) on the first
        registration.  Raises ``OSError`` if the port cannot be bound.
        """
        with self._lock:
            if self._sock is None:
                self._open()  # must succeed before we add the handler
            self._handlers.setdefault(ip, []).append(handler)
        logger.debug("SharedUDPListener registered handler for %s on :%d", ip, self._port)

    def unregister(self, ip: str, handler: _HandlerFn) -> None:
        """
        Remove *handler*.  Closes the socket when no handlers remain.
        """
        with self._lock:
            bucket = self._handlers.get(ip, [])
            try:
                bucket.remove(handler)
            except ValueError:
                return  # handler was never registered or already removed
            if not bucket:
                self._handlers.pop(ip, None)
            if not self._handlers and not self._raw_handlers and self._sock is not None:
                self._close()
        logger.debug("SharedUDPListener unregistered handler for %s on :%d", ip, self._port)

    def register_raw(self, handler: _RawHandlerFn) -> None:
        """
        Register *handler* to receive ``(packet, addr)`` for **every** incoming
        packet, regardless of source IP.  Useful for discovery where source
        addresses are not known in advance.
        """
        with self._lock:
            if self._sock is None:
                self._open()
            self._raw_handlers.append(handler)
        logger.debug("SharedUDPListener registered raw handler on :%d", self._port)

    def unregister_raw(self, handler: _RawHandlerFn) -> None:
        """Remove a previously registered raw handler."""
        with self._lock:
            try:
                self._raw_handlers.remove(handler)
            except ValueError:
                return
            if not self._handlers and not self._raw_handlers and self._sock is not None:
                self._close()
        logger.debug("SharedUDPListener unregistered raw handler on :%d", self._port)

    # ------------------------------------------------------------------
    # Internal helpers
    # ------------------------------------------------------------------

    def _open(self) -> None:
        """Open the socket and start the listener thread.  Called with ``_lock`` held."""
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.settimeout(self._RECV_TIMEOUT)
        sock.bind(("", self._port))
        self._sock = sock
        self._thread = threading.Thread(
            target=self._recv_loop,
            name=f"tecno-udp-listener:{self._port}",
            daemon=True,
        )
        self._thread.start()
        logger.debug("SharedUDPListener socket opened on :%d", self._port)

    def _close(self) -> None:
        """Close the socket (triggers OSError in the recv thread).  Called with ``_lock`` held."""
        try:
            self._sock.close()  # type: ignore[union-attr]
        except Exception:
            pass
        self._sock = None
        # The daemon thread will exit on its own when it gets the OSError from recvfrom.
        # We don't join it here to avoid blocking the caller.
        self._thread = None
        logger.debug("SharedUDPListener socket closed on :%d", self._port)

    def _recv_loop(self) -> None:
        # Keep a local reference so we still work even if self._sock is replaced.
        sock = self._sock
        assert sock is not None
        logger.debug("SharedUDPListener thread started on :%d", self._port)
        while True:
            try:
                data, addr = sock.recvfrom(self.BUFFER_SIZE)
            except socket.timeout:
                # Check whether the socket was replaced (closed + reopened).
                # If so, exit this thread — the new one owns the new socket.
                with self._lock:
                    if self._sock is not sock:
                        break
                continue
            except OSError:
                # Socket was closed by _close(); exit cleanly.
                break

            try:
                packet = json.loads(data.decode("utf-8"))
            except (json.JSONDecodeError, UnicodeDecodeError):
                logger.debug("Ignoring non-JSON UDP packet from %s", addr)
                continue

            src_ip = addr[0]
            logger.debug("SharedUDPListener received from %s: %s", addr, packet)

            with self._lock:
                handlers = list(self._handlers.get(src_ip, []))
                raw_handlers = list(self._raw_handlers)

            for fn in handlers:
                try:
                    fn(packet)
                except Exception:
                    logger.exception("Packet handler raised an exception (src=%s)", src_ip)

            for fn in raw_handlers:
                try:
                    fn(packet, addr)
                except Exception:
                    logger.exception("Raw packet handler raised an exception (src=%s)", src_ip)

        logger.debug("SharedUDPListener thread exiting on :%d", self._port)
