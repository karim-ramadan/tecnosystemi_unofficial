"""
Tecnosystemi CLI  —  ``tecno`` command entry point.

Non-interactive usage::

    tecno discover
    tecno register 192.168.4.1 1234
    tecno --ip 192.168.4.1 --pin 1234 info
    tecno --ip 192.168.4.1 --pin 1234 state
    tecno --ip 192.168.4.1 --pin 1234 on
    tecno --ip 192.168.4.1 --pin 1234 off
    tecno --ip 192.168.4.1 --pin 1234 set speed=3 mod=2
    tecno --ip 192.168.4.1 --pin 1234 speed 3
    tecno --ip 192.168.4.1 --pin 1234 mode 4
    tecno --debug --ip 192.168.4.1 --pin 1234 state

    # Polaris 5X
    tecno --type polaris5x --ip 192.168.1.100 --pin 1234 state
    tecno --type polaris5x --ip 192.168.1.100 --pin 1234 on
    tecno --type polaris5x --ip 192.168.1.100 --pin 1234 mode 1

Interactive REPL (no sub-command)::

    tecno
    tecno --debug
    tecno --type polaris5x
"""

from __future__ import annotations

import argparse
import asyncio
import sys
from typing import Optional, Union

from ..idp import IDPManager
from ._colors import C
from ._repl import (
    DEVICE_TYPES,
    TecnoREPL,
    discover,
    enable_debug,
    disable_debug,
    parse_kv,
    print_info,
    print_state,
    print_state_polaris5x,
    register_device,
)
from ._session import IDP_FILE, SessionState


def _build_client(ip: str, pin: str, device_type: str, debug: bool):
    """Return a started (client, device) pair, or exit on failure."""
    from ..client import TecnoClient
    from ..devices import PicoDevice, Polaris5XDevice
    from ..polaris_client import PolarisClient

    handler = None
    if debug:
        handler = enable_debug()

    try:
        if device_type == "polaris5x":
            client = PolarisClient(ip=ip, pin=pin)
            client.start()
            device = Polaris5XDevice(client)
        else:
            idp_mgr = IDPManager(backend="memory")
            client = TecnoClient(ip=ip, idp_manager=idp_mgr, timeout=12.0)
            client.start()
            device = PicoDevice(client, pin=pin)
        return client, device, handler
    except Exception as exc:
        if handler:
            disable_debug(handler)
        print(f"  {C.red('✗')} Could not connect to {ip}: {exc}", file=sys.stderr)
        sys.exit(1)


def _need_ip(session: SessionState, args: argparse.Namespace) -> str:
    ip = args.ip or session.ip
    if not ip:
        print(f"  {C.red('✗')} No device IP given.  Use --ip <address> or run 'tecno discover' first.", file=sys.stderr)
        sys.exit(1)
    return ip


def _need_pin(session: SessionState, ip: str, args: argparse.Namespace) -> str:
    if args.pin:
        session.set_pin(ip, args.pin)
        return args.pin

    stored = session.get_pin(ip)
    if stored != "-1":
        return stored

    if sys.stdin.isatty():
        try:
            candidate = input(f"PIN for {ip}: ").strip()
        except (KeyboardInterrupt, EOFError):
            print()
            return "-1"
        if candidate:
            session.set_pin(ip, candidate)
            return candidate
    return "-1"


def main(argv: Optional[list[str]] = None) -> None:
    parser = argparse.ArgumentParser(
        prog="tecno",
        description="Unofficial CLI for Tecnosystemi HVAC devices",
    )
    parser.add_argument("--ip", default="", metavar="ADDRESS", help="Device IP address")
    parser.add_argument("--pin", default="", metavar="PIN", help="Device PIN")
    parser.add_argument(
        "--type",
        default="",
        dest="device_type",
        metavar="TYPE",
        choices=list(DEVICE_TYPES) + [""],
        help=f"Device type: {', '.join(DEVICE_TYPES)} (default: pico)",
    )
    parser.add_argument(
        "--debug",
        action="store_true",
        help="Enable verbose TX/RX packet logging to stderr",
    )

    sub = parser.add_subparsers(dest="cmd", metavar="COMMAND")

    sub.add_parser("discover", help="Scan the local network for devices")

    p_register = sub.add_parser(
        "register",
        help="Manually register a device by IP (useful when discovery is unavailable)",
    )
    p_register.add_argument("reg_ip", metavar="IP", help="Device IP address")
    p_register.add_argument("reg_pin", metavar="PIN", nargs="?", default=None, help="Device PIN (optional, validated if given)")

    sub.add_parser("info", help="Show device information (Pico only)")

    sub.add_parser("state", help="Show full device state (requires PIN)")

    sub.add_parser("on", help="Turn device ON")

    sub.add_parser("off", help="Turn device OFF")

    p_set = sub.add_parser("set", help="Update device fields: set key=value ...")
    p_set.add_argument("fields", nargs="+", metavar="key=value")

    p_speed = sub.add_parser("speed", help="Set fan speed 1-3 (Pico only)")
    p_speed.add_argument("value", type=int)
    p_speed.add_argument("raw", nargs="?", type=int, default=None, metavar="RAW")

    p_mode = sub.add_parser("mode", help="Set operating mode (Pico: 1-12 / Polaris 5X: 0-3)")
    p_mode.add_argument("value", type=int)

    p_humidity = sub.add_parser("humidity", help="Set target humidity 0-100 (Pico only)")
    p_humidity.add_argument("value", type=int)

    p_night = sub.add_parser("night", help="Set night mode on|off (Pico only)")
    p_night.add_argument("state", choices=["on", "off"])

    p_zone = sub.add_parser(
        "zone",
        help="Control a single zone (Polaris 5X only)",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        description=(
            "Per-zone control for Polaris 5X devices.\n\n"
            "Actions:\n"
            "  on|off              Turn zone on or off\n"
            "  temp <°C>           Set temperature setpoint (e.g. zone 1 temp 21.5)\n"
            "  crono on|off        Enable / disable schedule mode\n"
            "  fan <n>             Set fan coil speed"
        ),
    )
    p_zone.add_argument("zone_id", metavar="ZONE_ID", help="Zone numeric ID or name (e.g. 1 or CUCINA)")
    p_zone.add_argument(
        "action",
        choices=["on", "off", "temp", "crono", "fan"],
        metavar="ACTION",
        help="on | off | temp | crono | fan",
    )
    p_zone.add_argument("value", nargs="?", metavar="VALUE")

    args = parser.parse_args(argv)
    session = SessionState.load()

    # Resolve device type: CLI flag > session > default
    device_type = args.device_type or session.device_type or "pico"

    if args.cmd is None:
        repl = TecnoREPL(
            initial_ip=args.ip,
            initial_pin=args.pin,
            initial_device_type=args.device_type,
            debug=args.debug,
        )
        repl.cmdloop()
        return

    # ------------------------------------------------------------------
    # Non-interactive sub-commands
    # ------------------------------------------------------------------

    if args.cmd == "discover":
        handler = enable_debug() if args.debug else None
        print("Scanning (2s) …")
        found = discover(2.0)
        if not found:
            print(f"{C.red('✗')} No devices found.")
        else:
            print(f"\nFound {C.bold(str(len(found)))} device(s):\n")
            for ip in found:
                print(f"  {C.bold(ip)}")
        if handler:
            disable_debug(handler)
        return

    if args.cmd == "register":
        handler = enable_debug() if args.debug else None
        session = SessionState.load()
        ok = register_device(args.reg_ip, args.reg_pin, session)
        if handler:
            disable_debug(handler)
        sys.exit(0 if ok else 1)

    ip = _need_ip(session, args)
    pin = _need_pin(session, ip, args)
    client, device, debug_handler = _build_client(ip, pin, device_type, args.debug)

    is_polaris5x = device_type == "polaris5x"

    try:
        if args.cmd == "info":
            if is_polaris5x:
                print(f"  {C.yellow('!')} 'info' is not supported for Polaris 5X.", file=sys.stderr)
                sys.exit(1)
            print("Fetching info …")
            print_info(asyncio.run(device.get_info(timeout=12.0)))  # type: ignore[union-attr]

        elif args.cmd == "state":
            print("Fetching state …")
            state = asyncio.run(device.get_state(timeout=15.0))
            if is_polaris5x:
                print_state_polaris5x(state)
            else:
                print_state(state)

        elif args.cmd == "on":
            if asyncio.run(device.turn_on()):
                print(f"{C.green('✓')} Device {C.green('ON')}")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "off":
            if asyncio.run(device.turn_off()):
                print(f"{C.green('✓')} Device {C.red('OFF')}")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "set":
            fields = parse_kv(" ".join(args.fields))
            if not fields:
                print(f"{C.red('✗')} No valid key=value pairs found.", file=sys.stderr)
                sys.exit(1)
            if asyncio.run(device.update(**fields)):
                print(f"{C.green('✓')} " + " ".join(f"{k}={v}" for k, v in fields.items()))
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "speed":
            if is_polaris5x:
                print(f"  {C.yellow('!')} 'speed' is not supported for Polaris 5X.", file=sys.stderr)
                sys.exit(1)
            if asyncio.run(device.set_speed(args.value, speed_raw=args.raw)):  # type: ignore[union-attr]
                print(f"{C.green('✓')} Speed → {args.value}")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "mode":
            if asyncio.run(device.set_mode(args.value)):
                print(f"{C.green('✓')} Mode → {args.value}")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "humidity":
            if is_polaris5x:
                print(f"  {C.yellow('!')} 'humidity' is not supported for Polaris 5X.", file=sys.stderr)
                sys.exit(1)
            if asyncio.run(device.set_humidity(args.value)):  # type: ignore[union-attr]
                print(f"{C.green('✓')} Humidity → {args.value}%")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "night":
            if is_polaris5x:
                print(f"  {C.yellow('!')} 'night' is not supported for Polaris 5X.", file=sys.stderr)
                sys.exit(1)
            enabled = args.state == "on"
            if asyncio.run(device.set_night_mode(enabled)):  # type: ignore[union-attr]
                print(f"{C.green('✓')} Night mode → {args.state}")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "zone":
            if not is_polaris5x:
                print(f"  {C.yellow('!')} 'zone' is only supported for Polaris 5X.", file=sys.stderr)
                sys.exit(1)
            from ._repl import _parse_setpoint, _find_zone, _zones_available, _zone_nr, _zone_name

            print("Fetching state …")
            state = asyncio.run(device.get_state(timeout=10.0))
            if state is None:
                print(f"{C.red('✗')} No response from device.", file=sys.stderr)
                sys.exit(2)

            zones = state.get("zone") or state.get("z") or []
            zone = _find_zone(zones, args.zone_id)
            if zone is None:
                print(f"{C.red('✗')} Zone {args.zone_id!r} not found. Available: {_zones_available(zones)}", file=sys.stderr)
                sys.exit(1)

            zone_nr = _zone_nr(zone)
            zone_name = _zone_name(zone)
            cur_setpoint = _parse_setpoint(zone.get("t_set", zone.get("ts"))) or 20.0
            cur_is_off = zone.get("is_off", zone.get("off", 0))
            cur_crono = zone.get("is_crono", 0)
            action = args.action

            if action in ("on", "off"):
                is_off = 0 if action == "on" else 1
                ok = asyncio.run(device.update_zone(  # type: ignore[union-attr]
                    zone_nr, zone_name,
                    is_off=is_off, set_temp=cur_setpoint, is_crono=cur_crono,
                ))
                if ok:
                    print(f"{C.green('✓')} Zone {zone_nr} ({zone_name}) → {'ON' if action == 'on' else 'OFF'}")
                else:
                    print(f"{C.red('✗')} Timed out.", file=sys.stderr)
                    sys.exit(2)

            elif action == "temp":
                if not args.value:
                    print(f"{C.red('✗')} Usage: zone <nr> temp <°C>", file=sys.stderr)
                    sys.exit(1)
                try:
                    temp = float(args.value)
                except ValueError:
                    print(f"{C.red('✗')} Temperature must be a number.", file=sys.stderr)
                    sys.exit(1)
                ok = asyncio.run(device.update_zone(  # type: ignore[union-attr]
                    zone_nr, zone_name,
                    is_off=cur_is_off, set_temp=temp, is_crono=cur_crono,
                ))
                if ok:
                    print(f"{C.green('✓')} Zone {zone_nr} ({zone_name}) setpoint → {temp:.1f} °C")
                else:
                    print(f"{C.red('✗')} Timed out.", file=sys.stderr)
                    sys.exit(2)

            elif action == "crono":
                if not args.value or args.value.lower() not in ("on", "off"):
                    print(f"{C.red('✗')} Usage: zone <nr> crono on|off", file=sys.stderr)
                    sys.exit(1)
                is_crono = 1 if args.value.lower() == "on" else 0
                ok = asyncio.run(device.update_zone(  # type: ignore[union-attr]
                    zone_nr, zone_name,
                    is_off=cur_is_off, set_temp=cur_setpoint, is_crono=is_crono,
                ))
                if ok:
                    print(f"{C.green('✓')} Zone {zone_nr} ({zone_name}) schedule → {args.value}")
                else:
                    print(f"{C.red('✗')} Timed out.", file=sys.stderr)
                    sys.exit(2)

            elif action == "fan":
                if not args.value or not args.value.lstrip("-").isdigit():
                    print(f"{C.red('✗')} Usage: zone <nr> fan <n>", file=sys.stderr)
                    sys.exit(1)
                fan_speed = int(args.value)
                ok = asyncio.run(device.update_zone(  # type: ignore[union-attr]
                    zone_nr, zone_name,
                    is_off=cur_is_off, set_temp=cur_setpoint, is_crono=cur_crono,
                    fan_set=fan_speed,
                ))
                if ok:
                    print(f"{C.green('✓')} Zone {zone_nr} ({zone_name}) fan → {fan_speed}")
                else:
                    print(f"{C.red('✗')} Timed out.", file=sys.stderr)
                    sys.exit(2)

    finally:
        client.stop()
        if debug_handler:
            disable_debug(debug_handler)
