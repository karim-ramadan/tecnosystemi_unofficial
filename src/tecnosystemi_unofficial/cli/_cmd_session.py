"""Session management REPL commands: pin, check_pin, debug."""
from __future__ import annotations

import asyncio

from ._colors import C
from ._helpers import disable_debug, enable_debug


class SessionCommands:
    """Mixin: PIN and debug management commands."""

    def do_pin(self, arg: str) -> None:
        """pin [<value> | forget | list]  —  manage per-device PINs.

        pin             Show PIN for the active device
        pin 1234        Save PIN 1234 for the active device (validates first)
        pin forget      Remove stored PIN for the active device
        pin list        Show all stored device PINs
        """
        arg = arg.strip()

        if arg == "list":
            pins = self._session.device_pins  # type: ignore[attr-defined]
            if not pins:
                print("  No PINs stored yet.")
            else:
                print("  Stored PINs:")
                for dev_ip, dev_pin in pins.items():
                    active_ip = self._client.ip if self._client else ""  # type: ignore[attr-defined]
                    marker = "  ◀" if dev_ip == active_ip else ""
                    print(f"    {dev_ip:20s}  {dev_pin}{marker}")
            return

        if not self._require_device():  # type: ignore[attr-defined]
            return

        ip = self._client.ip  # type: ignore[attr-defined]

        if not arg:
            stored = self._session.get_pin(ip)  # type: ignore[attr-defined]
            print(f"  PIN for {ip}: {stored!r}" + ("  (not set)" if stored == "-1" else ""))
            return

        if arg == "forget":
            self._session.forget_pin(ip)  # type: ignore[attr-defined]
            if self._device:  # type: ignore[attr-defined]
                self._device.pin = "-1"  # type: ignore[attr-defined]
            print(f"  PIN for {ip} removed.")
            return

        old_pin = self._device.pin  # type: ignore[attr-defined]
        self._device.pin = arg  # type: ignore[attr-defined]
        print("  Checking PIN …")
        if asyncio.run(self._device.check_pin(timeout=8.0)):  # type: ignore[attr-defined]
            self._session.set_pin(ip, arg)  # type: ignore[attr-defined]
            print(f"  {C.green('✓')} PIN accepted and saved for {ip}")
        else:
            self._device.pin = old_pin  # type: ignore[attr-defined]
            print(f"  {C.red('✗')} PIN rejected by device.  PIN not saved.")

    def do_check_pin(self, _arg: str) -> None:
        """Check whether the stored PIN is accepted by the active device."""
        if not self._require_device():  # type: ignore[attr-defined]
            return
        print("  Checking PIN …")
        if asyncio.run(self._device.check_pin()):  # type: ignore[attr-defined]
            print(f"  {C.green('✓')} PIN accepted.")
        else:
            print(f"  {C.red('✗')} PIN rejected (or no response).")

    def do_debug(self, arg: str) -> None:
        """debug [on|off]  —  toggle verbose TX/RX packet logging."""
        arg = arg.strip().lower()
        if arg in ("on", "1", "true", "yes", ""):
            if not self._debug_handler:  # type: ignore[attr-defined]
                self._debug_handler = enable_debug()  # type: ignore[attr-defined]
                self._session.debug = True  # type: ignore[attr-defined]
                self._session.save()  # type: ignore[attr-defined]
            print("  Debug ON  (TX/RX packets → stderr)")
        elif arg in ("off", "0", "false", "no"):
            if self._debug_handler:  # type: ignore[attr-defined]
                disable_debug(self._debug_handler)  # type: ignore[attr-defined]
                self._debug_handler = None  # type: ignore[attr-defined]
                self._session.debug = False  # type: ignore[attr-defined]
                self._session.save()  # type: ignore[attr-defined]
            print("  Debug OFF")
        else:
            status = "ON" if self._debug_handler else "OFF"  # type: ignore[attr-defined]
            print(f"  Debug is {status}.  Use: debug on | debug off")
