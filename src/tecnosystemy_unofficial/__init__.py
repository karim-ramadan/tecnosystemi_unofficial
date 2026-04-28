"""
tecnosystemy_unofficial — Unofficial Python library for Tecnosystemi devices.

Quickstart::

    from tecnosystemy_unofficial import TecnoClient
    from tecnosystemy_unofficial.devices import PicoDevice

    with TecnoClient(ip="192.168.4.1") as client:
        pico = PicoDevice(client, pin="1234")
        info = pico.get_info()
        pico.turn_on()
        state = pico.get_state()
"""

from .client import TecnoClient
from .idp import IDPManager, FileIDPStore, MemoryIDPStore
from .transport import UDPTransport

__version__ = "0.1.0"
__all__ = [
    "TecnoClient",
    "IDPManager",
    "FileIDPStore",
    "MemoryIDPStore",
    "UDPTransport",
]
