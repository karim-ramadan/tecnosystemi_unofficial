# tecnosystemi-unofficial — Library / API

Python library for controlling **Tecnosystemi** IoT devices over the local network.

Supported devices:
- **Pico / Pico Pro** — decentralised heat-recovery ventilation units (UDP)
- **Polaris 5X** — multi-zone Wi-Fi HVAC control unit (TCP)

← [Back to project root](../README.md)

## Features

### Pico (UDP)
- Shared singleton UDP listener — no port conflicts when controlling multiple devices
- Thread-safe incremental `idp` counter (wraps at 500) — storable in **memory** or **file** (survives restarts)
- Per-request state machine: correctly handles `res:99` ACK → `res:1` data flow
- **Jinja2 template commands** — add new commands via `.json.j2` files without writing Python
- Context-manager lifecycle (`with TecnoClient(...) as client`)

### Polaris 5X (TCP)
- Stateless per-command TCP connections on port 1235 (mirrors official app)
- `stato_r` compact polling with automatic fallback to full `stato`
- PIN error detection via `err_cu` bitmask
- Context-manager lifecycle (`async with PolarisClient(...) as client`)

## Requirements

- Python ≥ 3.10
- `jinja2 ≥ 3.1` (Pico only)
- Device must be reachable on the local network

## Installation

```bash
pip install tecnosystemi-unofficial
# or with uv
uv add tecnosystemi-unofficial
```

## Quick Start

### Pico ventilation unit (UDP)

```python
from tecnosystemi_unofficial import TecnoClient
from tecnosystemi_unofficial.devices import PicoDevice

with TecnoClient(ip="192.168.1.16") as client:
    pico = PicoDevice(client, pin="1234")

    # Read
    info = pico.get_info()    # serial, firmware, name
    state = pico.get_state()  # temperatures, speed, mode, humidity …

    # Control
    pico.turn_on()
    pico.set_speed(3)         # 1=Min, 2=Medium, 3=Max
    pico.set_mode(2)          # 2 = Estrazione (Extraction)
    pico.set_humidity(55)
    pico.set_night_mode(True)
    pico.turn_off()
```

### Polaris 5X multi-zone HVAC (TCP)

`PolarisClient` uses **TCP port 1235** and a different command schema from `TecnoClient`.
All device methods are coroutines.

```python
import asyncio
from tecnosystemi_unofficial import PolarisClient
from tecnosystemi_unofficial.devices import (
    Polaris5XDevice,
    OPERATING_MODE_HEATING,
    OPERATING_MODE_COOLING,
    OPERATING_MODE_DEHUMIDIFICATION,
    OPERATING_MODE_VENTILATION,
)

async def main():
    async with PolarisClient(ip="192.168.1.100", pin="1234") as client:
        polaris = Polaris5XDevice(client)

        # Read
        state = await polaris.get_state()
        # state["is_off"]   → 0=running, 1=off
        # state["is_cool"]  → 0=heating, 1=cooling
        # state["cool_mod"] → 1=Raffrescamento, 2=Deumidificazione, 3=Ventilazione
        # state["zone"]     → list of zone dicts

        # Control
        await polaris.turn_on()
        await polaris.set_mode(OPERATING_MODE_COOLING)
        await polaris.turn_off()

asyncio.run(main())
```

## Controlling Multiple Pico Devices

All `TecnoClient` instances in the same process share a single UDP socket on
port 40069. No extra configuration needed.

```python
from tecnosystemi_unofficial import TecnoClient
from tecnosystemi_unofficial.devices import PicoDevice

with TecnoClient(ip="192.168.1.16") as c1, TecnoClient(ip="192.168.1.40") as c2:
    p1 = PicoDevice(c1, pin="1234")
    p2 = PicoDevice(c2, pin="5678")
    p1.turn_on()
    p2.turn_on()
```

## Persistent IDP Counter (Pico only)

By default the `idp` counter resets to 1 on each run. Use the `file` backend to
persist it across restarts:

```python
from pathlib import Path
from tecnosystemi_unofficial import TecnoClient, IDPManager

idp = IDPManager(backend="file", path=Path("~/.tecno/idp.json").expanduser())

with TecnoClient(ip="192.168.1.16", idp_manager=idp) as client:
    ...
```

## Template-Based Commands (Pico only)

You can drive Pico commands from Jinja2 templates — useful for adding new device
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
from tecnosystemi_unofficial import TecnoClient
from tecnosystemi_unofficial.devices import PicoDevice

with TecnoClient(ip="192.168.1.16", template_dirs=[Path("my_templates")]) as client:
    pico = PicoDevice(client, pin="1234")
    pico.send_template("pico/set_speed_auto.json.j2", speed=3, mod=4)
```

Bundled templates live in `src/tecnosystemi_unofficial/templates/pico/`.

---

## PicoDevice API Reference

```python
pico = PicoDevice(client, pin="1234")  # client: TecnoClient

pico.get_info()                        # → dict  serial, firmware version, device name
pico.get_state()                       # → dict  temps, speed, mode, humidity, on/off …
pico.check_pin()                       # → bool  validate PIN against device
pico.turn_on()                         # → bool  (sends on_off=1)
pico.turn_off()                        # → bool  (sends on_off=2)
pico.set_speed(n)                      # n: 1–3
pico.set_mode(n)                       # n: 1–12, see mode table below
pico.set_humidity(rh)                  # rh: 0–100 %
pico.set_night_mode(enabled)           # True / False
pico.set_crono_mode(mode)              # int
pico.reset_manual(man_reset)           # list[int]
pico.update(on_off=1, speed=3, ...)    # low-level: send arbitrary upd_pico fields
pico.check_led(led_color=2)            # → dict
pico.send_template(name, **ctx)        # → dict  Jinja2 template interface
```

### on_off values (Pico)

| Value | Meaning |
|-------|---------|
| `1` | ON |
| `2` | OFF |

### Pico Mode Table

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

### Pico Speed Table

| # | Name |
|---|------|
| 1 | Min |
| 2 | Medium |
| 3 | Max |

---

## Polaris5XDevice API Reference

```python
client  = PolarisClient(ip="192.168.1.100", pin="1234")  # TCP port 1235
polaris = Polaris5XDevice(client)

await polaris.get_state()                   # → dict  full CU + zone state
await polaris.check_pin()                   # → bool  True if PIN accepted
await polaris.turn_on()                     # → bool  (sends is_off=0)
await polaris.turn_off()                    # → bool  (sends is_off=1)
await polaris.set_mode(n)                   # n: 0–3, see mode table below
await polaris.update(is_off=0, is_cool=1)   # low-level: send arbitrary upd_cu fields
await polaris.update_zone(                  # update a single zone
    zone_id, name,
    is_off=0,
    set_temp=21.0,          # °C (sent as int×10 internally)
    is_crono=0,
    fan_set=2,              # optional
)

Polaris5XDevice.parse_zone_temperature(raw) # static: raw int → °C float
Polaris5XDevice.get_mode_name(n)            # static: 0 → "Riscaldamento", etc.
```

### on_off / is_off encoding (Polaris 5X)

| Value | Meaning |
|-------|---------|
| `0` | ON (active) |
| `1` | OFF |

> **Note:** Polaris 5X uses `is_off` with `0`=ON / `1`=OFF — different field name
> *and* reversed meaning compared to Pico's `on_off=1`=ON / `2`=OFF.

### Polaris 5X Mode Table

Use the `OPERATING_MODE_*` constants or raw integers:

```python
from tecnosystemi_unofficial.devices import (
    OPERATING_MODE_HEATING,          # 0
    OPERATING_MODE_COOLING,          # 1
    OPERATING_MODE_DEHUMIDIFICATION, # 2
    OPERATING_MODE_VENTILATION,      # 3
)
```

| # | Constant | Name | Description |
|---|----------|------|-------------|
| 0 | `OPERATING_MODE_HEATING` | Riscaldamento | Heating (`is_cool=0`) |
| 1 | `OPERATING_MODE_COOLING` | Raffrescamento | Cooling (`is_cool=1, cool_mod=1`) |
| 2 | `OPERATING_MODE_DEHUMIDIFICATION` | Deumidificazione | Dehumidification (`is_cool=1, cool_mod=2`) |
| 3 | `OPERATING_MODE_VENTILATION` | Ventilazione | Ventilation only (`is_cool=1, cool_mod=3`) |

### Polaris 5X State Fields

Top-level fields returned by `get_state()`:

| Field (full) | Field (ridotto) | Type | Description |
|---|---|---|---|
| `is_off` | `off` | int | `0`=running, `1`=off |
| `is_cool` | `cl` | int | `0`=heating, `1`=cooling |
| `cool_mod` | `cl_m` | int | Cooling sub-mode (1/2/3) |
| `zone` | `zone` | list | Per-zone state objects |
| `name` | — | str | Device name |
| `fw_ver` | — | str | Firmware version |
| `t_can` | `tc` | int | Canal temp setpoint (×10 int, °C) |
| `f_inv` | `fi` | int | Winter fan speed |
| `f_est` | `fe` | int | Summer fan speed |
| `err_cu` | `err_cu` | int | CU error bitmask (bit 4 = PIN error) |

> `get_state()` tries `stato_r` (compact/ridotto keys) first and falls back
> to full `stato` on older firmware — your code receives whichever format the
> device returns.

### Zone State Fields (`state["zone"]` entries)

| Field | Type | Description |
|-------|------|-------------|
| `id_zona` | int | Zone ID |
| `name` / `n` | str | Zone name |
| `is_off` / `off` | int | `0`=on, `1`=off |
| `t` | float/int | Current temperature (raw, use `parse_zone_temperature()`) |
| `t_set` / `ts` | str/int | Temperature setpoint (raw ×10) |
| `fan` | int | Fan coil current speed (-1 = not installed) |
| `fan_set` | int | Fan coil setpoint |
| `shu` | int | Shutter position (-1 = not installed) |
| `shu_set` | int | Shutter setpoint |
| `is_crono` | int | Schedule mode active |
| `err` | int | Zone error bitmask |

```python
# Convert raw temperature to °C
temp_c = Polaris5XDevice.parse_zone_temperature(state["zone"][0]["t"])
```

---

## Protocol Reference

### Pico — UDP

| Field | Description |
|-------|-------------|
| `cmd` | Command name (`pico_info`, `stato_sync`, `upd_pico`, …) |
| `pin` | Device PIN — use `"-1"` if none configured |
| `idp` | Incremental request ID (1–500, wraps) — injected by `TecnoClient` |
| `frm` | Frame source — always `"app"` — injected by `TecnoClient` |

**Response codes:**

| `res` | Meaning |
|-------|---------|
| `99` | ACK / processing; terminal for control commands |
| `1` | Success + data; client sends ACK back |
| `0` | Error |

**Ports:**

| Port | Direction | Purpose |
|------|-----------|---------|
| 40070 | Client → Device | Commands |
| 40069 | Device → Client | Responses |

**Commands:**

| Command | Description |
|---------|-------------|
| `pico_info` | Fetch serial, firmware, name (no PIN required) |
| `stato_sync` | Fetch full device state |
| `check_pin` | Validate PIN |
| `upd_pico` | Update device fields (on_off, speed, mod, …) |

### Polaris 5X — TCP

| Field | Description |
|-------|-------------|
| `c` | Command name (`stato_r`, `stato`, `upd_cu`, `upd_zona`) |
| `pin` | Device PIN sent with every command |

No `idp`, `frm`, or ACK exchange — each command opens its own connection.

**Port:** TCP 1235 (device listens; client connects per command)

**Commands:**

| Command | Description |
|---------|-------------|
| `stato_r` | Compact state (ridotto); device returns `res=4` if unsupported |
| `stato` | Full state (all fields) |
| `upd_cu` | Update CU settings (`is_off`, `is_cool`, `cool_mod`, `t_can`, `f_inv`, `f_est`) |
| `upd_zona` | Update a zone (`id_zona`, `name`, `is_off`, `t_set`, `is_crono`, `fan_set`, `shu_set`) |

---

## Architecture

```
Pico (UDP)
──────────
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
templates/
  └── pico/                 — bundled Jinja2 templates for Pico

Polaris 5X (TCP)
─────────────────
PolarisClient               — async TCP, short-lived connection per command
                              port 1235; command key "c"; no IDP/frm/ACK
devices/
  └── Polaris5XDevice       — high-level Polaris 5X multi-zone HVAC API
```

## Running Tests

```bash
uv run pytest
```
