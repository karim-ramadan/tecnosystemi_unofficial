"""
Per-request state machine for tracking command/response pairs.

Protocol behaviour (from decompiled firmware):
  - Device always echoes `idp` in the response.
  - `res: 99`  → ACK / processing. For *control* commands this is terminal success.
  - `res: 1`   → Data/success response (info & state commands).
  - `res: 0`   → Error.
  - Client must send an ACK back to the device whenever it receives `res != 99`.
"""

import threading
import time
from enum import Enum
from typing import Optional

# Commands that consider res:99 as terminal success (no data follows).
CONTROL_COMMANDS: frozenset[str] = frozenset(
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


class RequestState(Enum):
    WAITING_ACK = "waiting_ack"
    WAITING_FINAL = "waiting_final"
    COMPLETED = "completed"
    TIMED_OUT = "timed_out"


class RequestSession:
    """
    Tracks the lifecycle of a single outgoing command.

    Thread-safe: `handle_packet` may be called from the listener thread while
    `wait` is blocking in the caller thread.
    """

    def __init__(self, idp: int, command: str):
        self.idp = idp
        self.command = command
        self.expects_data: bool = command not in CONTROL_COMMANDS
        self.state: RequestState = RequestState.WAITING_ACK
        self.response: Optional[dict] = None
        self.all_packets: list[dict] = []
        self._event = threading.Event()
        self.issued_at: float = time.monotonic()

    def handle_packet(self, packet: dict):
        """Called by the transport layer when a matching packet arrives."""
        self.all_packets.append(packet)
        res = packet.get("res")

        if res == 99:
            # ACK from device
            if not self.expects_data:
                # Control command: ACK is the final answer
                self.response = packet
                self.state = RequestState.COMPLETED
                self._event.set()
            else:
                # Info/state command: keep waiting for the real data
                self.state = RequestState.WAITING_FINAL
        else:
            # Data response (res:1) or error (res:0)
            self.response = packet
            self.state = RequestState.COMPLETED
            self._event.set()

    def wait(self, timeout: float) -> Optional[dict]:
        """Block until response arrives or timeout expires. Returns the response or None."""
        if self._event.wait(timeout):
            return self.response
        self.state = RequestState.TIMED_OUT
        return None

    @property
    def is_complete(self) -> bool:
        return self.state == RequestState.COMPLETED

    @property
    def elapsed(self) -> float:
        return time.monotonic() - self.issued_at
