"""Non-interactive one-shot command execution for the ``tecno`` CLI."""
from __future__ import annotations

import argparse
import asyncio
import sys

from ._colors import C
from ._display_pico import print_info, print_state
from ._display_polaris import (
    _find_zone,
    _parse_setpoint,
    _zone_name,
    _zone_nr,
    _zones_available,
    print_state_polaris5x,
)
from ._helpers import disable_debug, discover, enable_debug, parse_kv, register_device
from ._session import SessionState


def _build_client(ip: str, pin: str, device_type: str, debug: bool):
    """Return a started (client, device, debug_handler) triple, or sys.exit(1)."""
    from ..client import TecnoClient
    from ..devices import PicoDevice, Polaris5XDevice
    from ..idp import IDPManager
    from ..polaris_client import PolarisClient

    handler = enable_debug() if debug else None
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
        print(
            f"  {C.red('✗')} No device IP given.  Use --ip <address> or run 'tecno discover' first.",
            file=sys.stderr,
        )
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


def run(args: argparse.Namespace, session: SessionState, device_type: str) -> None:
    """Dispatch a single non-interactive sub-command and exit."""

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

        elif args.cmd == "temp":
            if not is_polaris5x:
                print(f"  {C.yellow('!')} 'temp' is only supported for Polaris 5X.", file=sys.stderr)
                sys.exit(1)
            if asyncio.run(device.set_canal_temperature(args.value)):  # type: ignore[union-attr]
                print(f"{C.green('✓')} Canal setpoint → {args.value:.1f} °C")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "zone":
            _run_zone(args, device, is_polaris5x)  # type: ignore[arg-type]

    finally:
        client.stop()
        if debug_handler:
            disable_debug(debug_handler)


def _run_zone(args: argparse.Namespace, device, is_polaris5x: bool) -> None:
    if not is_polaris5x:
        print(f"  {C.yellow('!')} 'zone' is only supported for Polaris 5X.", file=sys.stderr)
        sys.exit(1)
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
        ok = asyncio.run(device.update_zone(zone_nr, zone_name, is_off=is_off, set_temp=cur_setpoint, is_crono=cur_crono))
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
        ok = asyncio.run(device.update_zone(zone_nr, zone_name, is_off=cur_is_off, set_temp=temp, is_crono=cur_crono))
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
        ok = asyncio.run(device.update_zone(zone_nr, zone_name, is_off=cur_is_off, set_temp=cur_setpoint, is_crono=is_crono))
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
        ok = asyncio.run(device.update_zone(zone_nr, zone_name, is_off=cur_is_off, set_temp=cur_setpoint, is_crono=cur_crono, fan_set=fan_speed))
        if ok:
            print(f"{C.green('✓')} Zone {zone_nr} ({zone_name}) fan → {fan_speed}")
        else:
            print(f"{C.red('✗')} Timed out.", file=sys.stderr)
            sys.exit(2)
