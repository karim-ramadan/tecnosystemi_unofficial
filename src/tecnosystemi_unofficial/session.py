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

    def __init__(self, idp: int, command: str, expects_data: bool = True):
        self.idp = idp
        self.command = command
        self.expects_data = expects_data
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
            if not self.expects_data:
                self.response = packet
                self.state = RequestState.COMPLETED
                return True
            self.state = RequestState.WAITING_FINAL
            return False
        else:
            self.response = packet
            self.state = RequestState.COMPLETED
            return True

    @property
    def is_complete(self) -> bool:
        return self.state == RequestState.COMPLETED
