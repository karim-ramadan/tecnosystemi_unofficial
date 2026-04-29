# tecnosystemy-unofficial — Library / API

Python library for controlling **Tecnosystemi** IoT devices (Pico ventilation units) over UDP.

← [Back to project root](../README.md)

## Features

- Shared singleton UDP listener — no port conflicts when controlling multiple devices
- Thread-safe incremental `idp` counter (wraps at 500) — storable in **memory** or **file** (survives restarts)
- Per-request state machine: correctly handles `res:99` ACK → `res:1` data flow
- **Jinja2 template commands** — add new commands via `.json.j2` files without writing Python
- High-level `PicoDevice` API with full type hints
- Context-manager lifecycle (`with TecnoClient(...) as client`)

## Requirements

- Python ≥ 3.10
- `jinja2 ≥ 3.1`
- Device must be reachable on the network (default hotspot: `192.168.4.1`)

## Installation

```bash
pip install tecnosystemy-unofficial
# or with uv
uv add tecnosystemy-unofficial
```

## Quick Start

```python
from tecnosystemy_unofficial import TecnoClient
from tecnosystemy_unofficial.devices import PicoDevice

with TecnoClient(ip="192.168.1.16") as client:
    pico = PicoDevice(client, pin="1234")

    # Read
    info  = pico.get_info()    # serial, firmware, name
    state = pico.get_state()   # temperatures, speed, mode, humidity …

    # Control
    pico.turn_on()
    pico.set_speed(3)          # 1=Min … 5=Max
    pico.set_mode(2)           # 2 = Estrazione (Extraction)
    pico.set_humidity(55)
    pico.set_night_mode(True)
    pico.turn_off()
```

## Controlling Multiple Devices

All `TecnoClient` instances in the same process share a single UDP socket on
port 40069 (devices always reply to that port). No extra configuration needed.

```python
from tecnosystemy_unofficial import TecnoClient
from tecnosystemy_unofficial.devices import PicoDevice

with TecnoClient(ip="192.168.1.16") as c1, TecnoClient(ip="192.168.1.40") as c2:
    p1 = PicoDevice(c1, pin="1234")
    p2 = PicoDevice(c2, pin="5678")
    p1.turn_on()
    p2.turn_on()
```

## Persistent IDP Counter

By default the `idp` counter resets to 1 on each run. Use the `file` backend to
persist it across restarts (required if you care about idp continuity):

```python
from pathlib import Path
from tecnosystemy_unofficial import TecnoClient, IDPManager

idp = IDPManager(backend="file", path=Path("~/.tecno/idp.json").expanduser())

with TecnoClient(ip="192.168.1.16", idp_manager=idp) as client:
    ...
```

## Template-Based Commands

You can drive commands from Jinja2 templates — useful for adding new device
interactions without touching Python.

**Directory layout:**

```
my_templates/
  pico/
    set_speed_auto.json.j2
```

**Template example:**

```jinja2
{# my_templates/pico/set_speed_auto.json.j2 #}
{
  "cmd": "upd_pico",
  "pin": "{{ pin }}"
  {%- if speed is defined and speed is not none %}, "speed": {{ speed }}{% endif %}
  {%- if mod is defined and mod is not none %}, "mod": {{ mod }}{% endif %}
}
```

**Usage:**

```python
from pathlib import Path
from tecnosystemy_unofficial import TecnoClient
from tecnosystemy_unofficial.devices import PicoDevice

with TecnoClient(ip="192.168.1.16", template_dirs=[Path("my_templates")]) as client:
    pico = PicoDevice(client, pin="1234")
    pico.send_template("pico/set_speed_auto.json.j2", speed=5, mod=4)
```

Bundled templates live in `src/tecnosystemy_unofficial/templates/pico/`.

## PicoDevice API Reference

```python
pico.get_info()               # → dict  serial, firmware version, device name
pico.get_state()              # → dict  temps, speed, mode, humidity, on/off …
pico.check_pin(pin)           # → bool  validate a PIN against the device
pico.turn_on()
pico.turn_off()
pico.set_speed(n)             # n: 1–5
pico.set_mode(n)              # n: 1–12, see mode table below
pico.set_humidity(rh)         # rh: 0–100 %
pico.set_night_mode(enabled)  # True / False
```

### Mode Table

| # | Name | Description |
|---|------|-------------|
| 1 | Recupero | Heat-recovery ventilation — balanced supply + extraction |
| 2 | Estrazione | Extraction only — removes stale air |
| 3 | Immissione | Supply only — brings in fresh air |
| 4 | Auto Umidità ☀ | Humidity-controlled auto mode (summer) |
| 5 | Auto Umidità ❄ | Humidity-controlled auto mode (winter) |
| 6 | Comfort Estate | Comfort mode — summer profile |
| 7 | Comfort Inverno | Comfort mode — winter profile |
| 8 | CO₂ Recupero | CO₂-triggered heat-recovery |
| 9 | CO₂ Estrazione | CO₂-triggered extraction |
| 10 | Auto Umidità 2 ☀ | Humidity auto mode 2 (summer) |
| 11 | Auto Umidità 2 ❄ | Humidity auto mode 2 (winter) |
| 12 | Ricambio Naturale | Natural air exchange — minimal mechanical ventilation |

### Speed Table

| # | Name |
|---|------|
| 1 | Min |
| 2 | Low |
| 3 | Medium |
| 4 | High |
| 5 | Max |

## UDP Protocol Reference

| Field | Description |
|-------|-------------|
| `cmd` | Command name (`pico_info`, `stato_sync`, `upd_pico`, …) |
| `pin` | Device PIN — use `"-1"` if none is configured |
| `idp` | Incremental request ID (1–500, wraps) |
| `frm` | Frame source — always `"app"` from the client |

**Response codes:**

| `res` | Meaning |
|-------|---------|
| `99` | ACK / processing; terminal for control commands (`upd_pico`, etc.) |
| `1` | Success + data; client must send ACK back |
| `0` | Error |

**Ports:**

| Port | Direction | Purpose |
|------|-----------|---------|
| 40070 | Client → Device | Commands |
| 40069 | Device → Client | Responses (all devices reply to this port) |

## Architecture

```
TecnoClient
  ├── UDPTransport          — thin send wrapper; registers with SharedUDPListener
  ├── SharedUDPListener     — process-level singleton recv socket (port 40069)
  │                           routes packets to the correct TecnoClient by source IP
  ├── IDPManager            — thread-safe IDP counter (memory or file backend)
  ├── RequestSession        — per-request state machine
  │                           WAITING_ACK → WAITING_FINAL → COMPLETED
  └── TemplateLoader        — Jinja2 renderer for .json.j2 templates

devices/
  └── PicoDevice            — high-level Pico ventilation unit API
```

## Running Tests

```bash
uv run pytest
```
