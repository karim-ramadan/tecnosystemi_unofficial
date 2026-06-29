"""Polaris 5X-only REPL commands: temp, zone."""
from __future__ import annotations

import asyncio

from ._colors import C
from ._display_polaris import (
    _find_zone,
    _parse_setpoint,
    _print_zone_detail,
    _print_zone_row,
    _zone_name,
    _zone_nr,
    _zones_available,
)


class PolarisCommands:
    """Mixin: commands only applicable to Polaris 5X multi-zone HVAC units."""

    def do_temp(self, arg: str) -> None:
        """temp <°C>  —  set the CU canal temperature setpoint (Polaris 5X only).

        Example:
          temp 21.5
        """
        if not self._require_device():  # type: ignore[attr-defined]
            return
        if not self._is_polaris5x():  # type: ignore[attr-defined]
            print(f"  {C.yellow('!')} 'temp' is only available for Polaris 5X.")
            return
        if not self._ensure_pin():  # type: ignore[attr-defined]
            return
        arg = arg.strip()
        if not arg:
            print("  Usage: temp <°C>  (e.g. temp 21.5)")
            return
        try:
            temp = float(arg)
        except ValueError:
            print(f"  {C.yellow('!')} Temperature must be a number (e.g. 21.5).")
            return
        ok = asyncio.run(self._device.set_canal_temperature(temp))  # type: ignore[attr-defined]
        if ok:
            print(f"  {C.green('✓')} Canal setpoint → {temp:.1f} °C")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_zone(self, arg: str) -> None:
        """zone [<id> [on|off|temp <°C>|crono on|off|fan <n>]]  —  per-zone control (Polaris 5X only).

        zone                     List all zones with state
        zone <id>                Show detailed state for zone <id>
        zone <id> on             Turn zone on
        zone <id> off            Turn zone off
        zone <id> temp <°C>      Set temperature setpoint  (e.g. zone 1 temp 21.5)
        zone <id> crono on|off   Enable / disable schedule mode
        zone <id> fan <n>        Set fan coil speed
        """
        if not self._require_device():  # type: ignore[attr-defined]
            return
        if not self._is_polaris5x():  # type: ignore[attr-defined]
            print(f"  {C.yellow('!')} 'zone' is only available for Polaris 5X.")
            return
        if not self._ensure_pin():  # type: ignore[attr-defined]
            return

        parts = arg.strip().split()

        print("  Fetching state …")
        state = asyncio.run(self._device.get_state(timeout=10.0))  # type: ignore[attr-defined]
        if state is None:
            print(f"  {C.red('✗')} No response from device.")
            return

        zones = state.get("zone") or state.get("z") or []

        if not parts:
            if not zones:
                print("  No zones found in device state.")
                return
            print(f"\n  {C.bold('Zones:')}\n")
            for z in zones:
                _print_zone_row(z)
            print()
            return

        try:
            int(parts[0])
        except ValueError:
            print(f"  {C.yellow('!')} Zone must be addressed by number.  Available: {_zones_available(zones)}")
            return

        zone = _find_zone(zones, parts[0])
        if zone is None:
            print(f"  {C.red('✗')} Zone {parts[0]} not found.  Available: {_zones_available(zones)}")
            return

        zone_nr = _zone_nr(zone)
        zone_name = _zone_name(zone)
        cur_setpoint = _parse_setpoint(zone.get("t_set", zone.get("ts"))) or 20.0
        cur_is_off = zone.get("is_off", zone.get("off", 0))
        cur_crono = zone.get("is_crono", 0)

        if len(parts) == 1:
            print(f"\n  {C.bold(f'Zone {zone_nr} — {zone_name}')}\n")
            _print_zone_detail(zone)
            print()
            return

        action = parts[1].lower()

        if action in ("on", "off"):
            is_off = 0 if action == "on" else 1
            ok = asyncio.run(self._device.update_zone(  # type: ignore[attr-defined]
                zone_nr, zone_name,
                is_off=is_off, set_temp=cur_setpoint, is_crono=cur_crono,
                timeout=8.0,
            ))
            if ok:
                lbl = C.green("ON") if action == "on" else C.red("OFF")
                print(f"  {C.green('✓')} Zone {zone_nr} ({zone_name}) → {lbl}")
            else:
                print(f"  {C.red('✗')} Command timed out.")
            return

        if action == "temp":
            if len(parts) < 3:
                print("  Usage: zone <nr> temp <°C>  (e.g. zone 1 temp 21.5)")
                return
            try:
                temp = float(parts[2])
            except ValueError:
                print(f"  {C.yellow('!')} Temperature must be a number (e.g. 21.5).")
                return
            ok = asyncio.run(self._device.update_zone(  # type: ignore[attr-defined]
                zone_nr, zone_name,
                is_off=cur_is_off, set_temp=temp, is_crono=cur_crono,
                timeout=8.0,
            ))
            if ok:
                print(f"  {C.green('✓')} Zone {zone_nr} ({zone_name}) setpoint → {temp:.1f} °C")
            else:
                print(f"  {C.red('✗')} Command timed out.")
            return

        if action == "crono":
            if len(parts) < 3 or parts[2].lower() not in ("on", "off"):
                print("  Usage: zone <nr> crono on|off")
                return
            is_crono = 1 if parts[2].lower() == "on" else 0
            ok = asyncio.run(self._device.update_zone(  # type: ignore[attr-defined]
                zone_nr, zone_name,
                is_off=cur_is_off, set_temp=cur_setpoint, is_crono=is_crono,
                timeout=8.0,
            ))
            if ok:
                clbl = C.green("on") if is_crono else C.dim("off")
                print(f"  {C.green('✓')} Zone {zone_nr} ({zone_name}) schedule → {clbl}")
            else:
                print(f"  {C.red('✗')} Command timed out.")
            return

        if action == "fan":
            if len(parts) < 3 or not parts[2].lstrip("-").isdigit():
                print("  Usage: zone <nr> fan <n>")
                return
            fan_speed = int(parts[2])
            ok = asyncio.run(self._device.update_zone(  # type: ignore[attr-defined]
                zone_nr, zone_name,
                is_off=cur_is_off, set_temp=cur_setpoint, is_crono=cur_crono,
                fan_set=fan_speed, timeout=8.0,
            ))
            if ok:
                print(f"  {C.green('✓')} Zone {zone_nr} ({zone_name}) fan → {fan_speed}")
            else:
                print(f"  {C.red('✗')} Command timed out.")
            return

        print(f"  {C.yellow('!')} Unknown action: {action!r}")
        print("  Usage: zone <nr> on|off|temp <°C>|crono on|off|fan <n>")
