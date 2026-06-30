# tecnosystemi-unofficial

Unofficial Python library + CLI for controlling **Tecnosystemi** IoT devices over the local network.

Supported devices:
- **Pico / Pico Pro** — decentralised heat-recovery ventilation units
- **Polaris 5X** — multi-zone Wi-Fi HVAC control unit (heating, cooling, dehumidification, ventilation)

> [!WARNING]
> This is a community reverse-engineering effort based on the decompiled Android app. It is not affiliated with or endorsed by Tecnosistemi S.r.l.

## Documentation

| | |
|---|---|
| 📦 [**Library / API**](docs/library.md) | Use `TecnoClient`/`PolarisClient`, `PicoDevice` and `Polaris5XDevice` in your own Python code |
| 🖥️ [**CLI**](docs/cli.md) | Interactive terminal tool to discover, inspect and control devices |

## Installation

```bash
pip install tecnosystemi-unofficial
```

For the CLI:

```bash
pipx install tecnosystemi-unofficial
tecno --help
```

## Quick examples

**Pico ventilation unit:**

```python
from tecnosystemi_unofficial import TecnoClient
from tecnosystemi_unofficial.devices import PicoDevice

with TecnoClient(ip="192.168.1.16") as client:
    pico = PicoDevice(client, pin="1234")
    print(pico.get_state())
    pico.turn_on()
    pico.set_speed(3)
    pico.set_mode(1)  # 1 = Recupero (heat-recovery)
```

**Polaris 5X multi-zone HVAC (TCP port 1235):**

```python
import asyncio
from tecnosystemi_unofficial import PolarisClient
from tecnosystemi_unofficial.devices import Polaris5XDevice, OPERATING_MODE_COOLING

async def main():
    async with PolarisClient(ip="192.168.1.100", pin="1234") as client:
        polaris = Polaris5XDevice(client)
        state = await polaris.get_state()
        print(state)
        await polaris.turn_on()
        await polaris.set_mode(OPERATING_MODE_COOLING)

asyncio.run(main())
```

## Running Tests

```bash
uv run pytest
```
