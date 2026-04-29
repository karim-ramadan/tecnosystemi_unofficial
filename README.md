# tecnosystemy-unofficial

Unofficial Python library + CLI for controlling **Tecnosystemi** IoT devices (Pico ventilation units and more) over UDP.

> [!WARNING]
> This is a community reverse-engineering effort based on the decompiled Android app. It is not affiliated with or endorsed by Tecnosistemi S.r.l.

## Documentation

| | |
|---|---|
| 📦 [**Library / API**](docs/library.md) | Use `TecnoClient` and `PicoDevice` in your own Python code |
| 🖥️ [**CLI**](docs/cli.md) | Interactive terminal tool to discover, inspect and control devices |

## Installation

```bash
pip install tecnosystemy-unofficial
```

For the CLI:

```bash
pipx install tecnosystemy-unofficial
tecno --help
```

## Quick example

```python
from tecnosystemy_unofficial import TecnoClient
from tecnosystemy_unofficial.devices import PicoDevice

with TecnoClient(ip="192.168.1.16") as client:
    pico = PicoDevice(client, pin="1234")
    print(pico.get_state())
    pico.turn_on()
    pico.set_speed(3)
    pico.set_mode(1)   # 1 = Recupero (heat-recovery)
```

## Running Tests

```bash
uv run pytest
```
