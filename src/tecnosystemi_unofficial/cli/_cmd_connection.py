"""Connection management REPL commands: discover, register, select, type, help."""
from __future__ import annotations

import asyncio
import sys

from ._colors import C
from ._helpers import DEVICE_TYPES, discover


class ConnectionCommands:
    """Mixin: connection-related REPL commands (discover, register, select, type, help)."""

    def do_discover(self, arg: str) -> None:
        """Scan the local network for Tecnosystemi devices."""
        timeout = 2.0
        if arg.strip():
            try:
                timeout = float(arg.strip())
            except ValueError:
                pass
        print(f"  Scanning ({timeout}s) …")
        found = discover(timeout)
        if not found:
            print(f"  {C.red('✗')} No devices found.")
            self._last_discovered = []  # type: ignore[attr-defined]
            return
        self._last_discovered = found  # type: ignore[attr-defined]
        print(f"\n  Found {C.bold(str(len(found)))} device(s):\n")
        for i, ip in enumerate(found, 1):
            marker = f"  {C.green('◀ active')}" if ip == self._session.ip else ""  # type: ignore[attr-defined]
            print(f"    [{i}]  {C.bold(ip)}{marker}")
        print()
        if not sys.stdin.isatty():
            print("  Use 'select <n>' or 'select <IP>' to connect.")
            return
        if len(found) == 1:
            print(f"  Auto-connecting to {C.bold(found[0])} …")
            self._connect(found[0])  # type: ignore[attr-defined]
        else:
            try:
                choice = input("  Connect to device? (number or Enter to skip): ").strip()
            except (EOFError, KeyboardInterrupt):
                print()
                return
            if choice.isdigit():
                idx = int(choice) - 1
                if 0 <= idx < len(found):
                    self._connect(found[idx])  # type: ignore[attr-defined]
                else:
                    print(f"  {C.yellow('!')} Index out of range.")

    def do_register(self, arg: str) -> None:
        """register <IP> [PIN]  —  manually add a device by IP (and optional PIN)."""
        parts = arg.strip().split()
        if not parts:
            print("  Usage: register <IP> [PIN]")
            return
        ip = parts[0]
        pin = parts[1] if len(parts) > 1 else None
        if not self._connect(ip):  # type: ignore[attr-defined]
            return
        if pin:
            old_pin = self._device.pin  # type: ignore[attr-defined]
            self._device.pin = pin  # type: ignore[attr-defined]
            print("  Checking PIN …")
            if asyncio.run(self._device.check_pin(timeout=8.0)):  # type: ignore[attr-defined]
                self._session.set_pin(ip, pin)  # type: ignore[attr-defined]
                print(f"  {C.green('✓')} PIN accepted and saved for {ip}")
            else:
                self._device.pin = old_pin  # type: ignore[attr-defined]
                print(f"  {C.red('✗')} PIN rejected by device.  Run 'pin <value>' to try again.")

    def do_select(self, arg: str) -> None:
        """select <n|IP>  —  connect to a device by list number or IP."""
        arg = arg.strip()
        if not arg:
            if self._last_discovered:  # type: ignore[attr-defined]
                for i, ip in enumerate(self._last_discovered, 1):  # type: ignore[attr-defined]
                    marker = "  ◀" if ip == self._session.ip else ""  # type: ignore[attr-defined]
                    print(f"    [{i}]  {ip}{marker}")
                print("\n  Re-run 'discover' to refresh the list.")
            else:
                print("  Run 'discover' first.")
            return
        if arg.isdigit():
            idx = int(arg) - 1
            if not self._last_discovered:  # type: ignore[attr-defined]
                print("  Run 'discover' first to get a numbered list.")
                return
            if not (0 <= idx < len(self._last_discovered)):  # type: ignore[attr-defined]
                print(f"  ✗ Index out of range (1–{len(self._last_discovered)}).")  # type: ignore[attr-defined]
                return
            ip = self._last_discovered[idx]  # type: ignore[attr-defined]
        else:
            ip = arg
        self._connect(ip)  # type: ignore[attr-defined]

    def do_type(self, arg: str) -> None:
        """type [pico|polaris5x]  —  show or switch device type.

        Examples:
          type              Show current device type
          type pico         Switch to Pico ventilation unit
          type polaris5x    Switch to Polaris 5X multi-zone HVAC
        """
        arg = arg.strip().lower()
        if not arg:
            print(f"  Device type: {C.bold(self._device_type)}")  # type: ignore[attr-defined]
            print(f"  Available: {', '.join(DEVICE_TYPES)}")
            return
        if arg not in DEVICE_TYPES:
            print(f"  {C.yellow('!')} Unknown type {arg!r}.  Choose from: {', '.join(DEVICE_TYPES)}")
            return
        if arg == self._device_type:  # type: ignore[attr-defined]
            print(f"  Already set to {C.bold(arg)}.")
            return
        self._device_type = arg  # type: ignore[attr-defined]
        self._session.device_type = arg  # type: ignore[attr-defined]
        self._session.save()  # type: ignore[attr-defined]
        print(f"  {C.green('✓')} Device type → {C.bold(arg)}")
        if self._client and self._session.ip:  # type: ignore[attr-defined]
            ip = self._client.ip  # type: ignore[attr-defined]
            self._connect(ip, silent=True)  # type: ignore[attr-defined]
            print(f"  {C.green('✓')} Reconnected to {ip} as {C.bold(arg)}")

    def do_help(self, arg: str) -> None:
        """Show help, or help for a specific command: help <command>."""
        if arg:
            try:
                doc = getattr(self, f"do_{arg}").__doc__ or ""
                print(f"\n  {doc.strip()}\n")
            except AttributeError:
                print(f"  {C.yellow('!')} Unknown command: {arg!r}")
            return

        def row(cmd_str: str, desc: str) -> None:
            print(f"    {C.cyan(f'{cmd_str:<32s}')}  {desc}")

        dtype = self._device_type  # type: ignore[attr-defined]
        print(f"\n  {C.bold('─────  Tecnosystemi CLI  ─────')}\n")

        print(f"  {C.bold('Connection:')}")
        row("discover [timeout]", "Scan local network for devices")
        row("select <n|IP>", "Connect to a device")
        row("register <IP> [PIN]", "Manually add a device by IP")
        row("type [pico|polaris5x]", "Show / switch device type")
        print()

        print(f"  {C.bold('Control  (all device types):')}")
        row("state", "Show current device state")
        row("on / off", "Power control")
        row("mode [value]", "Set operating mode (interactive menu if no value)")
        row("set key=value …", "Update device fields (low-level)")
        row("pin [value|forget|list]", "Manage stored PIN")
        row("check_pin", "Validate stored PIN against device")
        print()

        if dtype != "polaris5x":
            print(f"  {C.bold('Pico-only:')}")
            row("info", "Device info (serial, firmware, name)")
            row("speed <1-3>", "Fan speed: 1=Min  2=Medium  3=Max")
            row("humidity <0-100>", "Target humidity (%)")
            row("night [on|off]", "Night mode")
            print()

        if dtype != "pico":
            print(f"  {C.bold('Polaris 5X-only:')}")
            row("temp <°C>", "Set CU canal setpoint  (e.g. temp 21.5)")
            row("zone", "List all zones with state")
            row("zone <id>", "Show detailed zone state")
            row("zone <id> on|off", "Turn zone on / off")
            row("zone <id> temp <°C>", "Set zone temperature setpoint")
            row("zone <id> crono on|off", "Enable / disable schedule mode")
            row("zone <id> fan <n>", "Set fan coil speed")
            print()

        print(f"  {C.bold('Session:')}")
        row("debug [on|off]", "Toggle raw packet logging")
        row("quit / exit", "Exit the REPL")
        print(f"\n  {C.dim('Tip: help <command>  for full usage and examples')}\n")
