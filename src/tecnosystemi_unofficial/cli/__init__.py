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

Interactive REPL (no sub-command)::

    tecno
    tecno --debug
"""

from __future__ import annotations

import argparse
import sys
from typing import Optional

from ..idp import IDPManager
from ._colors import C
from ._repl import (
    TecnoREPL,
    discover,
    enable_debug,
    disable_debug,
    parse_kv,
    print_info,
    print_state,
    register_device,
)
from ._session import IDP_FILE, SessionState


def _build_client(ip: str, pin: str, debug: bool):
    """Return a started (client, pico) pair, or exit on failure."""
    from ..client import TecnoClient
    from ..devices import PicoDevice

    handler = None
    if debug:
        handler = enable_debug()

    try:
        idp_mgr = IDPManager(backend="memory")
        client = TecnoClient(ip=ip, idp_manager=idp_mgr, timeout=12.0)
        client.start()
        pico = PicoDevice(client, pin=pin)
        return client, pico, handler
    except Exception as exc:
        if handler:
            disable_debug(handler)
        print(f"  {C.red('✗')} Could not connect to {ip}: {exc}", file=sys.stderr)
        sys.exit(1)


def _need_ip(session: SessionState, args: argparse.Namespace) -> str:
    """Return the resolved IP or exit with a helpful message."""
    ip = args.ip or session.ip
    if not ip:
        print(f"  {C.red('✗')} No device IP given.  Use --ip <address> or run 'tecno discover' first.", file=sys.stderr)
        sys.exit(1)
    return ip


def _need_pin(session: SessionState, ip: str, args: argparse.Namespace) -> str:
    """
    Return the PIN to use for *ip*.

    Priority: --pin flag > stored per-device PIN > prompt if tty available > "-1"
    If --pin is given it is also saved for future use.
    """
    if args.pin:
        session.set_pin(ip, args.pin)
        return args.pin

    stored = session.get_pin(ip)
    if stored != "-1":
        return stored

    # No PIN stored — prompt if we have a tty.
    if sys.stdin.isatty():
        try:
            candidate = input(f"PIN for {ip}: ").strip()
        except (KeyboardInterrupt, EOFError):
            print()
            return "-1"
        if candidate:
            # We can't validate here without opening a client first — just save it
            # and let the command fail with a clear error if wrong.
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

    sub.add_parser("info", help="Show device information")

    sub.add_parser("state", help="Show full device state (requires PIN)")

    sub.add_parser("on", help="Turn device ON")

    sub.add_parser("off", help="Turn device OFF")

    p_set = sub.add_parser("set", help="Update device fields: set key=value ...")
    p_set.add_argument("fields", nargs="+", metavar="key=value")

    p_speed = sub.add_parser("speed", help="Set fan speed (1-3)")
    p_speed.add_argument("value", type=int)
    p_speed.add_argument("raw", nargs="?", type=int, default=None, metavar="RAW")

    p_mode = sub.add_parser("mode", help="Set operating mode (0-12)")
    p_mode.add_argument("value", type=int)

    p_humidity = sub.add_parser("humidity", help="Set target humidity (0-100)")
    p_humidity.add_argument("value", type=int)

    p_night = sub.add_parser("night", help="Set night mode: on|off")
    p_night.add_argument("state", choices=["on", "off"])

    args = parser.parse_args(argv)
    session = SessionState.load()

    if args.cmd is None:
        # Interactive REPL
        repl = TecnoREPL(
            initial_ip=args.ip,
            initial_pin=args.pin,
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
    client, pico, debug_handler = _build_client(ip, pin, args.debug)

    try:
        if args.cmd == "info":
            print("Fetching info …")
            print_info(pico.get_info(timeout=12.0))

        elif args.cmd == "state":
            print("Fetching state …")
            print_state(pico.get_state(timeout=15.0))

        elif args.cmd == "on":
            if pico.turn_on():
                print(f"{C.green('✓')} Device {C.green('ON')}")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "off":
            if pico.turn_off():
                print(f"{C.green('✓')} Device {C.red('OFF')}")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "set":
            fields = parse_kv(" ".join(args.fields))
            if not fields:
                print(f"{C.red('✗')} No valid key=value pairs found.", file=sys.stderr)
                sys.exit(1)
            if pico.update(**fields):
                print(f"{C.green('✓')} " + " ".join(f"{k}={v}" for k, v in fields.items()))
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "speed":
            if pico.set_speed(args.value, speed_raw=args.raw):
                print(f"{C.green('✓')} Speed → {args.value}")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "mode":
            if pico.set_mode(args.value):
                print(f"{C.green('✓')} Mode → {args.value}")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "humidity":
            if pico.set_humidity(args.value):
                print(f"{C.green('✓')} Humidity → {args.value}%")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

        elif args.cmd == "night":
            enabled = args.state == "on"
            if pico.set_night_mode(enabled):
                print(f"{C.green('✓')} Night mode → {args.state}")
            else:
                print(f"{C.red('✗')} Timed out.")
                sys.exit(2)

    finally:
        client.stop()
        if debug_handler:
            disable_debug(debug_handler)
