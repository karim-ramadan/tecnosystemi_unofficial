"""
PolarisClient: async TCP client for Tecnosystemi Polaris 5X (CU) devices.

Protocol (from APK MySocket class):
- Transport: TCP port 1235
- Each command opens its own short-lived connection (stateless)
- Command key is "c" (not "cmd")
- No IDP, frm, or ACK envelope — plain JSON in, JSON out
- PIN is sent as a field with every command
"""

from __future__ import annotations

import asyncio
import json
import logging
from typing import Optional

logger = logging.getLogger(__name__)

_BUFFER_SIZE = 4096


class PolarisClient:
    """
    Async TCP client for Polaris 5X CU devices.

    Each command opens and closes its own TCP connection (mirrors the
    ``MySocket.sendAndReceive`` pattern in the official Android app).
    ``start()`` and ``stop()`` are no-ops — no persistent socket is kept open.

    Args:
        ip:      Device IP address.
        pin:     Device PIN (sent with every command).
        port:    TCP port on the device (default 1235).
        timeout: Per-command timeout in seconds.
    """

    DEFAULT_PORT = 1235

    def __init__(
        self,
        ip: str,
        pin: str,
        port: int = DEFAULT_PORT,
        timeout: float = 5.0,
    ) -> None:
        self.ip = ip
        self.pin = pin
        self.port = port
        self.timeout = timeout

    # ------------------------------------------------------------------
    # Lifecycle (no-ops — TCP is stateless per command)
    # ------------------------------------------------------------------

    def start(self) -> None:
        pass

    def stop(self) -> None:
        pass

    def __enter__(self) -> "PolarisClient":
        return self

    def __exit__(self, *_) -> None:
        pass

    async def __aenter__(self) -> "PolarisClient":
        return self

    async def __aexit__(self, *_) -> None:
        pass

    # ------------------------------------------------------------------
    # Command API
    # ------------------------------------------------------------------

    async def send_command(
        self,
        cmd: dict,
        timeout: Optional[float] = None,
    ) -> Optional[dict]:
        """
        Send *cmd* over TCP and return the parsed JSON response.

        Returns ``None`` on timeout or connection error.
        The ``"c"`` key must already be set in *cmd*.
        """
        effective_timeout = timeout if timeout is not None else self.timeout
        payload = json.dumps(cmd).encode("utf-8")
        logger.debug("→ [%s:%d] %s", self.ip, self.port, cmd)
        try:
            result = await asyncio.wait_for(
                self._send_and_receive(payload),
                timeout=effective_timeout,
            )
            if result is not None:
                logger.debug("← [%s:%d] %s", self.ip, self.port, result)
            return result
        except asyncio.TimeoutError:
            logger.warning(
                "Timeout (%.1fs) waiting for response to '%s' from %s",
                effective_timeout, cmd.get("c"), self.ip,
            )
            return None
        except (OSError, json.JSONDecodeError) as exc:
            logger.warning("Error communicating with %s: %s", self.ip, exc)
            return None

    # ------------------------------------------------------------------
    # Internal
    # ------------------------------------------------------------------

    async def _send_and_receive(self, payload: bytes) -> Optional[dict]:
        """Open a TCP connection, send *payload*, read and return the response."""
        reader, writer = await asyncio.open_connection(self.ip, self.port)
        try:
            writer.write(payload)
            await writer.drain()

            data = b""
            while True:
                chunk = await reader.read(_BUFFER_SIZE)
                if not chunk:
                    break
                data += chunk
                if len(chunk) < _BUFFER_SIZE:
                    break

            if not data:
                return None
            return json.loads(data.decode("utf-8"))
        finally:
            writer.close()
            try:
                await writer.wait_closed()
            except OSError:
                pass
