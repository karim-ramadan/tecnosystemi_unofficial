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
from typing import Optional

from ._display_pico import MODES, SPEEDS, LED_COLORS, print_info, print_state
from ._display_polaris import (
    P6X_MODES,
    _find_zone,
    _parse_setpoint,
    _zone_name,
    _zone_nr,
    _zones_available,
    print_state_polaris5x,
)
from ._helpers import DEVICE_TYPES, disable_debug, discover, enable_debug, parse_kv, register_device
from ._noninteractive import run as _run_noninteractive
from ._session import SessionState
from ._shell import TecnoREPL


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
    p_register.add_argument("reg_pin", metavar="PIN", nargs="?", default=None)

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
    p_zone.add_argument("zone_id", metavar="ZONE_ID")
    p_zone.add_argument("action", choices=["on", "off", "temp", "crono", "fan"], metavar="ACTION")
    p_zone.add_argument("value", nargs="?", metavar="VALUE")

    args = parser.parse_args(argv)
    session = SessionState.load()
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

    _run_noninteractive(args, session, device_type)
