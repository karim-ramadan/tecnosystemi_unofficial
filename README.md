# tecnosystemy-unofficial

Unofficial Python library for controlling **Tecnosystemi** IoT devices (Pico ventilation units and more) over UDP.

> [!WARNING]
> This is a community reverse-engineering effort based on the decompiled Android app. It is not affiliated with or endorsed by Tecnosistemi S.r.l.

## Features

- Separate UDP send / receive sockets with a persistent listener thread
- Thread-safe incremental `idp` counter (wraps at 500) — storable in **memory** or **file** (survives restarts)
- Per-request state machine: correctly handles `res:99` ACK → `res:1` data flow
- **Jinja2 template commands** — define new commands in `.json.j2` files without writing Python
- High-level `PicoDevice` API with full type hints
- Context-manager lifecycle (`with TecnoClient(...) as client`)

## Requirements

- Python ≥ 3.10
- jinja2 ≥ 3.1
- Device must be on the same network (default hotspot: `192.168.4.1`)

## Installation

```bash
uv add tecnosystemy-unofficial
# or
pip install tecnosystemy-unofficial
```

## Quick Start

```python
from tecnosystemy_unofficial import TecnoClient
from tecnosystemy_unofficial.devices import PicoDevice

with TecnoClient(ip="192.168.4.1") as client:
    pico = PicoDevice(client, pin="1234")

    # Read
    info  = pico.get_info()    # serial, firmware, name
    state = pico.get_state()   # temperatures, speed, mode, humidity ...

    # Control
    pico.turn_on()
    pico.set_speed(3)
    pico.set_mode(2)            # 2 = Estrazione (Extraction)
    pico.set_humidity(55)
    pico.set_night_mode(True)
    pico.turn_off()
```

## Persistent IDP (survives restarts)

By default the IDP counter resets to 1 on each run. Use the `file` backend to
persist it across restarts:

```python
from pathlib import Path
from tecnosystemy_unofficial import TecnoClient, IDPManager

idp = IDPManager(backend="file", path=Path("~/.tecno/idp.json").expanduser())

with TecnoClient(ip="192.168.4.1", idp_manager=idp) as client:
    ...
```

## Template-based commands

Commands can be driven from Jinja2 templates so you can add new device
interactions without writing Python.

```
my_templates/
  pico/
    set_speed_auto.json.j2
```

```jinja2
{# my_templates/pico/set_speed_auto.json.j2 #}
{
  "cmd": "upd_pico",
  "pin": "{{ pin }}"
  {%- if speed is defined and speed is not none %}, "speed": {{ speed }}{% endif %}
  {%- if mod is defined and mod is not none %}, "mod": {{ mod }}{% endif %}
}
```

```python
from pathlib import Path
from tecnosystemy_unofficial import TecnoClient
from tecnosystemy_unofficial.devices import PicoDevice

with TecnoClient(
    ip="192.168.4.1",
    template_dirs=[Path("my_templates")],
) as client:
    pico = PicoDevice(client, pin="1234")
    pico.send_template("pico/set_speed_auto.json.j2", speed=5, mod=4)
```

Bundled templates are in `src/tecnosystemy_unofficial/templates/pico/`.

## UDP Protocol Reference

| Field | Description |
|-------|-------------|
| `cmd` | Command name (e.g. `pico_info`, `stato_sync`, `upd_pico`) |
| `pin` | Device PIN — use `"-1"` if none is configured |
| `idp` | Incremental request ID (1–500, wraps) |
| `frm` | Frame source — always `"app"` from the client |

**Response codes:**

| `res` | Meaning |
|-------|---------|
| `99`  | ACK / processing; terminal for control commands (`upd_pico` etc.) |
| `1`   | Success + data; client must send ACK back |
| `0`   | Error |

**Ports:**

| Port  | Direction | Purpose |
|-------|-----------|---------|
| 40070 | Client → Device | Commands |
| 40069 | Device → Client | Responses |

## Architecture

```
TecnoClient
  ├── UDPTransport          — sockets + listener thread
  ├── IDPManager            — thread-safe IDP allocation (memory or file)
  ├── RequestSession        — per-request state machine (WAITING_ACK -> WAITING_FINAL -> COMPLETED)
  └── TemplateLoader        — Jinja2 renderer for .json.j2 templates

devices/
  └── PicoDevice            — high-level Pico API
```

## Running Tests

```bash
uv run pytest
```
