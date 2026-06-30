"""
tecnosystemi_unofficial — Unofficial Python library for Tecnosystemi devices.

Quickstart (Pico — UDP)::

    from tecnosystemi_unofficial import TecnoClient
    from tecnosystemi_unofficial.devices import PicoDevice

    with TecnoClient(ip="192.168.4.1") as client:
        pico = PicoDevice(client, pin="1234")
        info = pico.get_info()
        pico.turn_on()
        state = pico.get_state()

Quickstart (Polaris 5X — TCP port 1235)::

    import asyncio
    from tecnosystemi_unofficial import PolarisClient
    from tecnosystemi_unofficial.devices import Polaris5XDevice

    async def main():
        async with PolarisClient(ip="192.168.1.100", pin="1234") as client:
            polaris = Polaris5XDevice(client)
            state = await polaris.get_state()
            await polaris.turn_on()

    asyncio.run(main())
"""

from .client import TecnoClient
from .idp import IDPManager, FileIDPStore, MemoryIDPStore
from .polaris_client import PolarisClient
from .transport import UDPTransport

__version__ = "0.1.0"
__all__ = [
    "TecnoClient",
    "PolarisClient",
    "IDPManager",
    "FileIDPStore",
    "MemoryIDPStore",
    "UDPTransport",
]
