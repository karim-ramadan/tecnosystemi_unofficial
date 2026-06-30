"""Pico-only REPL commands: info, speed, humidity, night."""
from __future__ import annotations

import asyncio
from typing import Optional

from ._colors import C
from ._display_pico import SPEEDS, print_info


class PicoCommands:
    """Mixin: commands only applicable to Pico ventilation units."""

    def do_info(self, _arg: str) -> None:
        """Fetch and display device information (Pico only — no PIN required)."""
        if not self._require_pico():  # type: ignore[attr-defined]
            return
        print("  Fetching info …")
        print_info(asyncio.run(self._device.get_info(timeout=12.0)))  # type: ignore[attr-defined]

    def do_speed(self, arg: str) -> None:
        """speed <1-3> [raw_0-100]  —  set fan speed (Pico only)."""
        if not self._require_pico():  # type: ignore[attr-defined]
            return
        if not self._ensure_pin():  # type: ignore[attr-defined]
            return
        parts = arg.strip().split()
        if not parts or not parts[0].isdigit():
            print("  Usage: speed <1-3> [raw_0-100]")
            return
        speed = int(parts[0])
        raw: Optional[int] = int(parts[1]) if len(parts) > 1 and parts[1].isdigit() else None
        ok = asyncio.run(self._device.set_speed(speed, speed_raw=raw))  # type: ignore[attr-defined]
        if ok:
            speed_name = SPEEDS.get(speed, "")
            label = f"{speed}" + (f"  {C.dim(f'({speed_name})')}" if speed_name else "")
            print(f"  {C.green('✓')} Speed → {label}" + (f" (raw {raw})" if raw is not None else ""))
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_humidity(self, arg: str) -> None:
        """humidity <0-100>  —  set target humidity / s_umd (Pico only)."""
        if not self._require_pico():  # type: ignore[attr-defined]
            return
        if not self._ensure_pin():  # type: ignore[attr-defined]
            return
        arg = arg.strip()
        if not arg.isdigit():
            print("  Usage: humidity <0-100>")
            return
        ok = asyncio.run(self._device.set_humidity(int(arg)))  # type: ignore[attr-defined]
        if ok:
            print(f"  {C.green('✓')} Humidity → {arg}%")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_night(self, arg: str) -> None:
        """night [on|off]  —  toggle night mode (Pico only)."""
        if not self._require_pico():  # type: ignore[attr-defined]
            return
        if not self._ensure_pin():  # type: ignore[attr-defined]
            return
        arg = arg.strip().lower()
        if arg in ("on", "1", "true", ""):
            enabled = True
        elif arg in ("off", "0", "false"):
            enabled = False
        else:
            print("  Usage: night on | night off")
            return
        ok = asyncio.run(self._device.set_night_mode(enabled))  # type: ignore[attr-defined]
        if ok:
            print(f"  {C.green('✓')} Night mode → {'on' if enabled else 'off'}")
        else:
            print(f"  {C.red('✗')} Command timed out.")
