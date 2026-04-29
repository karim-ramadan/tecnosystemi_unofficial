"""
Per-request state machine for tracking command/response pairs.

Protocol behaviour (from decompiled firmware):
  - Device always echoes `idp` in the response.
  - `res: 99`  → ACK / processing. For *control* commands this is terminal success.
  - `res: 1`   → Data/success response (info & state commands).
  - `res: 0`   → Error.
  - Client must send an ACK back to the device whenever it receives `res != 99`.
"""

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


class RequestSession:
    """
    Tracks the lifecycle of a single outgoing command.

    ``handle_packet`` is called by the listener thread; it returns ``True``
    when the session has received its final response so the caller can resolve
    the associated asyncio Future.
    """

    def __init__(self, idp: int, command: str):
        self.idp = idp
        self.command = command
        self.expects_data: bool = command not in CONTROL_COMMANDS
        self.state: RequestState = RequestState.WAITING_ACK
        self.response: Optional[dict] = None
        self.all_packets: list[dict] = []

    def handle_packet(self, packet: dict) -> bool:
        """
        Process an incoming packet.

        Returns ``True`` when the session is complete (final response received),
        ``False`` when still waiting for more packets.
        """
        self.all_packets.append(packet)
        res = packet.get("res")

        if res == 99:
            # ACK from device
            if not self.expects_data:
                # Control command: ACK is the final answer
                self.response = packet
                self.state = RequestState.COMPLETED
                return True
            else:
                # Info/state command: keep waiting for the real data
                self.state = RequestState.WAITING_FINAL
                return False
        else:
            # Data response (res:1) or error (res:0)
            self.response = packet
            self.state = RequestState.COMPLETED
            return True

    @property
    def is_complete(self) -> bool:
        return self.state == RequestState.COMPLETED
