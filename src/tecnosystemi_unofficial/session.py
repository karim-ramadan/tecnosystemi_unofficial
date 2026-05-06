"""
Per-request state machine for tracking command/response pairs.

Protocol behaviour (from decompiled firmware):
  - Device always echoes `idp` in the response.
  - `res: 99`   → ACK / processing. For *control* commands this is terminal success.
  - `res: 1`    → Data/success response (info & state commands).
  - `otherwise` → Error.
  - Client must send an ACK back to the device whenever it receives `res != 99`.
"""

from enum import Enum
from typing import Optional


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

        if self.state == RequestState.COMPLETED:
            return True

        res = packet.get("res")
        if res == 99:
            self.state = RequestState.WAITING_FINAL
        elif res == 1:
            # Data response (res:1) or error (res:0)
            self.response = packet
            self.state = RequestState.COMPLETED
            return True
        else:
            self.state = RequestState.COMPLETED
        return False

    @property
    def is_complete(self) -> bool:
        return self.state == RequestState.COMPLETED
