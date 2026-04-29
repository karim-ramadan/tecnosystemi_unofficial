# tecnosystemi-unofficial — CLI

Interactive terminal tool to discover, inspect and control Tecnosystemi Pico devices.

← [Back to project root](../README.md)

## Installation

```bash
pipx install tecnosystemi-unofficial   # recommended (isolated environment)
# or
pip install tecnosystemi-unofficial
```

## Launch

```bash
tecno                  # start interactive REPL (remembers last device)
tecno --ip 192.168.1.16   # open REPL pre-connected to a specific device
tecno discover         # one-shot discovery, then exit
tecno info             # show device info (uses remembered device)
tecno state            # show device state
tecno on / tecno off   # power on/off
```

## Interactive REPL

Running `tecno` (or `tecno --ip <ip>`) opens a persistent REPL session:

```
tecno> help
```

### Commands

| Command | Description |
|---------|-------------|
| `discover` | Broadcast discovery; auto-connects if one device found, prompts otherwise |
| `select <ip>` | Connect to a specific IP and remember it |
| `info` | Fetch and display device info (serial, firmware, name) |
| `state` | Fetch and display current device state |
| `on` | Power the device on |
| `off` | Power the device off |
| `speed <1-5>` | Set fan speed (1=Min … 5=Max) |
| `mode [1-12]` | Set operating mode; shows numbered menu if no argument given |
| `humidity <0-100>` | Set target humidity (%) |
| `night [on\|off]` | Toggle night mode |
| `pin` | Show stored PIN for current device |
| `pin <value>` | Validate PIN against device and save if correct |
| `pin forget` | Remove stored PIN for current device |
| `pin list` | List all stored PINs |
| `debug [on\|off]` | Toggle debug mode (shows raw UDP packets) |
| `quit` / `exit` | Exit the REPL |

### Mode Menu

Running `mode` without an argument shows an interactive numbered menu:

```
tecno> mode
  1  Recupero          Heat-recovery ventilation
  2  Estrazione        Extraction only
  3  Immissione        Supply only
  4  Auto Umidità ☀   Humidity-controlled auto (summer)
  5  Auto Umidità ❄   Humidity-controlled auto (winter)
  6  Comfort Estate    Comfort – summer profile
  7  Comfort Inverno   Comfort – winter profile
  8  CO₂ Recupero      CO₂-triggered heat-recovery
  9  CO₂ Estrazione    CO₂-triggered extraction
 10  Auto Umidità 2 ☀  Humidity auto mode 2 (summer)
 11  Auto Umidità 2 ❄  Humidity auto mode 2 (winter)
 12  Ricambio Naturale Natural air exchange
Select mode [1-12]:
```

## Mode Reference

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

## Per-Device PINs

Each device PIN is stored separately in `~/.tecno/config.json`.

- **Default PIN**: `1234`
- On first connection the CLI validates the PIN against the device and saves it only on success.
- You can manage PINs manually with the `pin` command (see table above).

## Debug Mode

Enable with `--debug` flag or the `debug on` REPL command:

```bash
tecno --debug
```

In debug mode every raw UDP packet sent and received is printed to the terminal.

## Session Persistence

The CLI saves state to `~/.tecno/config.json`:

- Last connected device IP
- Per-device PINs
- Debug flag

State is automatically restored on the next run, so `tecno` reconnects to the
last device without any arguments.

## Non-Interactive One-Shot Commands

All REPL commands are also available as one-shot CLI subcommands:

```bash
tecno --ip 192.168.1.16 info
tecno --ip 192.168.1.16 state
tecno --ip 192.168.1.16 on
tecno --ip 192.168.1.16 off
tecno --ip 192.168.1.16 speed 3
tecno --ip 192.168.1.16 mode 1
tecno --ip 192.168.1.16 humidity 55
tecno --ip 192.168.1.16 night on
tecno --ip 192.168.1.16 --pin 1234 on   # supply PIN inline
```

If `--ip` is omitted, the last remembered device is used.
