"""Common device-control REPL commands: state, on, off, set, mode."""
from __future__ import annotations

import asyncio
import sys

from ._colors import C
from ._display_pico import LED_COLORS, MODES, SPEEDS, _led_swatch, print_state
from ._display_polaris import P6X_MODES, print_state_polaris5x
from ._helpers import parse_kv


class CommonCommands:
    """Mixin: device-control commands shared across all device types."""

    def do_state(self, _arg: str) -> None:
        """Fetch and display full device state (PIN required)."""
        if not self._require_device():  # type: ignore[attr-defined]
            return
        if not self._ensure_pin():  # type: ignore[attr-defined]
            return
        print("  Fetching state …")
        state = asyncio.run(self._device.get_state(timeout=15.0))  # type: ignore[attr-defined]
        if self._is_polaris5x():  # type: ignore[attr-defined]
            print_state_polaris5x(state)
        else:
            print_state(state)

    def do_set(self, arg: str) -> None:
        """set key=value [key=value ...]  —  update device fields.

        Examples:
          set on_off=1
          set speed=3 mod=2
          set night_mod=1 s_umd=60
        """
        if not self._require_device():  # type: ignore[attr-defined]
            return
        if not self._ensure_pin():  # type: ignore[attr-defined]
            return
        fields = parse_kv(arg)
        if not fields:
            print("  Usage: set key=value [key=value ...]")
            return
        ok = asyncio.run(self._device.update(**fields))  # type: ignore[attr-defined]
        if ok:
            print(f"  {C.green('✓')}  {' '.join(f'{k}={v}' for k, v in fields.items())}")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_on(self, _arg: str) -> None:
        """Turn the device ON."""
        if not self._require_device():  # type: ignore[attr-defined]
            return
        if not self._ensure_pin():  # type: ignore[attr-defined]
            return
        if asyncio.run(self._device.turn_on()):  # type: ignore[attr-defined]
            print(f"  {C.green('✓')} Device {C.green('ON')}")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_off(self, _arg: str) -> None:
        """Turn the device OFF."""
        if not self._require_device():  # type: ignore[attr-defined]
            return
        if not self._ensure_pin():  # type: ignore[attr-defined]
            return
        if asyncio.run(self._device.turn_off()):  # type: ignore[attr-defined]
            print(f"  {C.green('✓')} Device {C.red('OFF')}")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_mode(self, arg: str) -> None:
        """mode [value]  —  set operating mode (shows menu if no argument).

        Pico modes (1-12): Recupero, Estrazione, Immissione, Auto, Comfort, CO₂ …
        Polaris 5X modes (0-3):
          0  Riscaldamento    – heating
          1  Raffrescamento   – cooling
          2  Deumidificazione – dehumidification
          3  Ventilazione     – ventilation only
        """
        if not self._require_device():  # type: ignore[attr-defined]
            return
        if not self._ensure_pin():  # type: ignore[attr-defined]
            return
        arg = arg.strip()

        if self._is_polaris5x():  # type: ignore[attr-defined]
            mode_table = P6X_MODES
            range_hint = "0-3"
        else:
            mode_table = MODES
            range_hint = "1-12"

        if not arg:
            print(f"\n  {C.bold('Operating modes:')}\n")
            for num, info in mode_table.items():
                name, desc = info
                if self._is_pico():  # type: ignore[attr-defined]
                    led = LED_COLORS.get(num)
                    color_str = f"  {_led_swatch(led[1])} {C.dim(led[0])}" if led else ""
                else:
                    color_str = ""
                print(f"   {C.cyan(f'[{num:2d}]')}  {name:<22s} {C.dim('–')} {desc}{color_str}")
            print()
            if not sys.stdin.isatty():
                return
            try:
                choice = input(f"  Select mode ({range_hint}, or Enter to cancel): ").strip()
            except (EOFError, KeyboardInterrupt):
                print()
                return
            if not choice:
                return
            arg = choice

        if not arg.lstrip("-").isdigit():
            print(f"  {C.yellow('!')} Usage: mode <{range_hint}>")
            return
        mode_num = int(arg)
        ok = asyncio.run(self._device.set_mode(mode_num))  # type: ignore[attr-defined]
        if ok:
            mode_info = mode_table.get(mode_num)
            if mode_info:
                name, desc = mode_info
                if self._is_pico():  # type: ignore[attr-defined]
                    led = LED_COLORS.get(mode_num)
                    swatch = f"  {_led_swatch(led[1])} {C.dim(led[0])}" if led else ""
                else:
                    swatch = ""
                label = f"{mode_num}  {C.dim(f'({name})')}{swatch}"
            else:
                label = str(mode_num)
            print(f"  {C.green('✓')} Mode → {label}")
        else:
            print(f"  {C.red('✗')} Command timed out.")
