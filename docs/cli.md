# tecnosystemi-unofficial — CLI

Interactive terminal tool to discover, inspect and control Tecnosystemi devices (Pico and Polaris 5X).

← [Back to project root](../README.md)

## Installation

```bash
pipx install tecnosystemi-unofficial   # recommended (isolated environment)
# or
pip install tecnosystemi-unofficial
```

## Device Types

The CLI supports two device types, selected with `--type`:

| `--type` | Device |
|----------|--------|
| `pico` *(default)* | Pico / Pico Pro heat-recovery ventilation units |
| `polaris5x` | Polaris 5X multi-zone Wi-Fi HVAC control unit |

The selected type is persisted in `~/.tecno/config.json` and remembered between sessions.

## Launch

```bash
# Pico (default)
tecno                             # start interactive REPL (remembers last device)
tecno --ip 192.168.1.16           # open REPL pre-connected to a specific device
tecno discover                    # one-shot discovery, then exit
tecno info                        # show device info (uses remembered device)
tecno state                       # show device state
tecno on / tecno off              # power on/off

# Polaris 5X
tecno --type polaris5x                              # start REPL as Polaris 5X
tecno --type polaris5x --ip 192.168.1.100 state    # fetch state
tecno --type polaris5x --ip 192.168.1.100 on       # turn on
tecno --type polaris5x --ip 192.168.1.100 mode 1   # set cooling mode
```

## Interactive REPL

Running `tecno` opens a persistent REPL session. The prompt shows the active device type and IP:

```
  (tecno/pico 192.168.1.16) >
  (tecno/polaris5x 192.168.1.100) >
```

### Commands

The `help` command (or just `help <command>`) shows only the commands relevant to the active device type.

| Command | Pico | Polaris 5X | Description |
|---------|:----:|:----------:|-------------|
| `discover` | ✓ | ✓ | Broadcast discovery; auto-connects if one device found |
| `select <ip>` | ✓ | ✓ | Connect to a specific IP and remember it |
| `type [pico\|polaris5x]` | ✓ | ✓ | Show or switch device type |
| `info` | ✓ | — | Fetch device info (serial, firmware, name) |
| `state` | ✓ | ✓ | Fetch and display current device state |
| `on` | ✓ | ✓ | Power the device on |
| `off` | ✓ | ✓ | Power the device off |
| `set key=value …` | ✓ | ✓ | Update arbitrary device fields |
| `speed <1-3>` | ✓ | — | Set fan speed |
| `mode [value]` | ✓ | ✓ | Set operating mode (different values per device type) |
| `humidity <0-100>` | ✓ | — | Set target humidity (%) |
| `night [on\|off]` | ✓ | — | Toggle night mode |
| `temp <°C>` | — | ✓ | Set CU canal temperature setpoint |
| `zone …` | — | ✓ | Per-zone control (see below) |
| `pin` | ✓ | ✓ | Show / set / forget stored PIN |
| `check_pin` | ✓ | ✓ | Validate stored PIN against device |
| `debug [on\|off]` | ✓ | ✓ | Toggle raw packet logging |
| `quit` / `exit` | ✓ | ✓ | Exit the REPL |

### Switching device type in the REPL

```
  (tecno/pico) > type polaris5x
  ✓ Device type → polaris5x
  ✓ Reconnected to 192.168.1.100 as polaris5x
  (tecno/polaris5x 192.168.1.100) >
```

### Mode menu

Running `mode` without an argument shows an interactive numbered menu tailored to the active device type.

**Pico:**
```
   [ 1]  Recupero              – Heat-recovery: simultaneous supply + exhaust
   [ 2]  Estrazione            – Extraction only: exhaust air out
   [ 3]  Immissione            – Supply only: fresh air in
   ...
  Select mode (1-12, or Enter to cancel):
```

**Polaris 5X:**
```
   [ 0]  Riscaldamento         – Heating
   [ 1]  Raffrescamento        – Cooling
   [ 2]  Deumidificazione      – Dehumidification
   [ 3]  Ventilazione          – Ventilation only
  Select mode (0-3, or Enter to cancel):
```

## Pico Mode Reference

| # | Name | Description |
|---|------|-------------|
| 1 | Recupero | Balanced supply + extraction with heat recovery |
| 2 | Estrazione | Extraction only — removes stale/humid air |
| 3 | Immissione | Supply only — brings in fresh outdoor air |
| 4 | Auto Umidità ☀ | Automatically adjusts speed based on humidity (summer) |
| 5 | Auto Umidità ❄ | Automatically adjusts speed based on humidity (winter) |
| 6 | Comfort Estate | Comfort-optimised profile for summer |
| 7 | Comfort Inverno | Comfort-optimised profile for winter |
| 8 | CO₂ Recupero | Activates heat-recovery when CO₂ level rises |
| 9 | CO₂ Estrazione | Activates extraction when CO₂ level rises |
| 10 | Auto Umidità 2 ☀ | Secondary humidity auto mode (summer variant) |
| 11 | Auto Umidità 2 ❄ | Secondary humidity auto mode (winter variant) |
| 12 | Ricambio Naturale | Minimal mechanical ventilation; relies on natural exchange |

## Polaris 5X Mode Reference

| # | Name | Description |
|---|------|-------------|
| 0 | Riscaldamento | Heating mode |
| 1 | Raffrescamento | Cooling mode |
| 2 | Deumidificazione | Dehumidification mode |
| 3 | Ventilazione | Ventilation only (no heating or cooling) |

## Polaris 5X Canal Temperature

The `temp` command sets the central unit (CU) canal temperature setpoint — the target temperature for the whole system, independent of individual zone setpoints.

### REPL

```
(tecno/polaris5x 192.168.1.100) > temp 21.5
✓ Canal setpoint → 21.5 °C
```

### Non-interactive

```bash
tecno --type polaris5x --ip 192.168.1.100 temp 21.5
```

## Polaris 5X Zone Control

The `zone` command lets you inspect and update individual zones.

### REPL

```
  (tecno/polaris5x 192.168.1.100) > zone
  [1] Soggiorno            [ON ]  T=21.2°C  SP=21.0°C  fan=2
  [2] Camera               [OFF]  T=19.5°C  SP=20.0°C

  (tecno/polaris5x 192.168.1.100) > zone 1
  Zone 1 — Soggiorno
  Power                ON
  Temperature          21.2 °C
  Setpoint             21.0 °C
  Fan coil             2  (setpoint: 2)
  Schedule             off

  (tecno/polaris5x 192.168.1.100) > zone 1 temp 22
  ✓ Zone 1 (Soggiorno) setpoint → 22.0 °C

  (tecno/polaris5x 192.168.1.100) > zone 2 on
  ✓ Zone 2 (Camera) → ON

  (tecno/polaris5x 192.168.1.100) > zone 1 crono on
  ✓ Zone 1 (Soggiorno) schedule → on

  (tecno/polaris5x 192.168.1.100) > zone 1 fan 3
  ✓ Zone 1 (Soggiorno) fan → 3
```

### Non-interactive

```bash
tecno --type polaris5x --ip 192.168.1.100 zone 1 on
tecno --type polaris5x --ip 192.168.1.100 zone 1 off
tecno --type polaris5x --ip 192.168.1.100 zone 1 temp 21.5
tecno --type polaris5x --ip 192.168.1.100 zone 2 crono on
tecno --type polaris5x --ip 192.168.1.100 zone 1 fan 2
```

## Per-Device PINs

Each device PIN is stored separately in `~/.tecno/config.json`.

- **Default PIN**: `1234`
- On first connection the CLI validates the PIN against the device and saves it only on success.
- You can manage PINs manually with the `pin` command (see table above).

## Debug Mode

Enable with `--debug` flag or the `debug on` REPL command:

```bash
tecno --debug
tecno --type polaris5x --debug
```

In debug mode every raw UDP packet sent and received is printed to stderr.

## Session Persistence

The CLI saves state to `~/.tecno/config.json`:

- Last connected device IP
- Active device type (`pico` or `polaris5x`)
- Per-device PINs
- Debug flag

State is automatically restored on the next run, so `tecno` reconnects to the last device without any arguments.

## Non-Interactive One-Shot Commands

All REPL commands are also available as one-shot CLI subcommands:

```bash
# Pico
tecno --ip 192.168.1.16 info
tecno --ip 192.168.1.16 state
tecno --ip 192.168.1.16 on
tecno --ip 192.168.1.16 off
tecno --ip 192.168.1.16 speed 3
tecno --ip 192.168.1.16 mode 1
tecno --ip 192.168.1.16 humidity 55
tecno --ip 192.168.1.16 night on
tecno --ip 192.168.1.16 --pin 1234 on

# Polaris 5X
tecno --type polaris5x --ip 192.168.1.100 state
tecno --type polaris5x --ip 192.168.1.100 on
tecno --type polaris5x --ip 192.168.1.100 off
tecno --type polaris5x --ip 192.168.1.100 mode 1        # cooling
tecno --type polaris5x --ip 192.168.1.100 temp 21.5     # CU canal setpoint
tecno --type polaris5x --ip 192.168.1.100 zone 1 on
tecno --type polaris5x --ip 192.168.1.100 zone 1 temp 21.5
tecno --type polaris5x --ip 192.168.1.100 zone 2 crono on
```

If `--ip` is omitted, the last remembered device is used.
