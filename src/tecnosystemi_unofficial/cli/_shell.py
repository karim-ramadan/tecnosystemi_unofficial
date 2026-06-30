"""TecnoREPL: assembles all command mixins into the interactive REPL."""
from __future__ import annotations

import asyncio
import cmd
import logging
from typing import Optional, Union

from ..client import TecnoClient
from ..devices import PicoDevice, Polaris5XDevice
from ..idp import IDPManager
from ..polaris_client import PolarisClient
from ._cmd_common import CommonCommands
from ._cmd_connection import ConnectionCommands
from ._cmd_pico import PicoCommands
from ._cmd_polaris import PolarisCommands
from ._cmd_session import SessionCommands
from ._colors import C
from ._helpers import DEVICE_TYPES, disable_debug, enable_debug
from ._session import CONFIG_DIR, HISTORY_FILE, SessionState

AnyDevice = Union[PicoDevice, Polaris5XDevice]


class TecnoREPL(ConnectionCommands, SessionCommands, CommonCommands, PicoCommands, PolarisCommands, cmd.Cmd):
    """Interactive REPL for Tecnosystemi device control.

    Inherits do_* commands from each mixin. The shell infrastructure
    (connection, guards, history, cleanup) lives here; adding autocomplete
    or switching to prompt_toolkit only requires editing this file.
    """

    intro = (
        f"\n  {C.bold('Tecnosystemi CLI')}\n"
        f"  Type {C.cyan('help')} for commands or {C.cyan('discover')} to find devices.\n"
    )

    def __init__(
            self,
            initial_ip: str = "",
            initial_pin: str = "",
            initial_device_type: str = "",
            debug: bool = False,
    ) -> None:
        super().__init__()
        self._session = SessionState.load()

        changed = False
        if initial_ip and initial_ip != self._session.ip:
            self._session.ip = initial_ip
            changed = True
        if initial_pin:
            target_ip = initial_ip or self._session.ip
            if target_ip and self._session.get_pin(target_ip) != initial_pin:
                self._session.set_pin(target_ip, initial_pin)
                changed = False
        if initial_device_type and initial_device_type != self._session.device_type:
            self._session.device_type = initial_device_type
            changed = True
        if debug and not self._session.debug:
            self._session.debug = True
            changed = True
        if changed:
            self._session.save()

        self._client: Optional[TecnoClient] = None
        self._device: Optional[AnyDevice] = None
        self._device_type: str = self._session.device_type
        self._last_discovered: list[str] = []
        self._debug_handler: Optional[logging.Handler] = None
        self._cleaned_up = False

        if self._session.debug:
            self._debug_handler = enable_debug()

        self._update_prompt()
        self._load_history()

        if self._session.ip:
            self._connect(self._session.ip, silent=True)

    # ------------------------------------------------------------------
    # Prompt and connection lifecycle
    # ------------------------------------------------------------------

    def _update_prompt(self) -> None:
        ip = self._client.ip if self._client else ""
        dtype = self._device_type
        if ip:
            self.prompt = C.prompt(f"  (tecno/{dtype} {ip}) > ")
        else:
            self.prompt = C.prompt(f"  (tecno/{dtype}) > ")

    def _make_client(self, ip: str, pin: str):
        if self._device_type == "polaris5x":
            return PolarisClient(ip=ip, pin=pin)
        idp_mgr = IDPManager(backend="memory")
        return TecnoClient(ip=ip, idp_manager=idp_mgr, timeout=12.0)

    def _make_device(self, client, pin: str) -> AnyDevice:
        if self._device_type == "polaris5x":
            return Polaris5XDevice(client)
        return PicoDevice(client, pin=pin)

    def _connect(self, ip: str, silent: bool = False) -> bool:
        if self._client:
            try:
                self._client.stop()
            except Exception:
                pass
            self._client = None
            self._device = None
            self._update_prompt()
        client = None
        try:
            pin = self._session.get_pin(ip)
            client = self._make_client(ip, pin)
            client.start()
            self._client = client
            self._device = self._make_device(client, pin)
            self._session.ip = ip
            self._session.save()
            self._update_prompt()
            if not silent:
                hint = "" if pin != "-1" else f"  {C.dim('(no PIN stored — run \"pin <value>\" to save one)')}"
                print(f"  {C.green('✓')} Connected to {C.bold(ip)}{hint}")
            return True
        except Exception as exc:
            if client is not None:
                try:
                    client.stop()
                except Exception:
                    pass
            print(f"  {C.red('✗')} Could not connect to {ip}: {exc}")
            return False

    # ------------------------------------------------------------------
    # Guards (used by mixin do_* methods via self.*)
    # ------------------------------------------------------------------

    def _require_device(self) -> bool:
        if self._device is None:
            print(f"  {C.yellow('!')} No device selected.  Run {C.cyan('discover')}, then {C.cyan('select <n>')}.")
            return False
        return True

    def _is_pico(self) -> bool:
        return isinstance(self._device, PicoDevice)

    def _is_polaris5x(self) -> bool:
        return isinstance(self._device, Polaris5XDevice)

    def _require_pico(self) -> bool:
        if not self._require_device():
            return False
        if not self._is_pico():
            print(f"  {C.yellow('!')} This command is not supported for Polaris 5X.")
            return False
        return True

    def _ensure_pin(self) -> bool:
        if self._device is None:
            return False
        if self._device.pin != "-1":
            return True
        print("  This command requires a PIN.  Enter it below (Ctrl-C to cancel).")
        try:
            candidate = input("  PIN: ").strip()
        except (KeyboardInterrupt, EOFError):
            print()
            return False
        if not candidate:
            print("  ✗ PIN cannot be empty.")
            return False
        old_pin = self._device.pin
        self._device.pin = candidate
        print("  Checking PIN …")
        if asyncio.run(self._device.check_pin(timeout=8.0)):
            ip = self._client.ip  # type: ignore[union-attr]
            self._session.set_pin(ip, candidate)
            print(f"  {C.green('✓')} PIN accepted and saved for {ip}")
            return True
        self._device.pin = old_pin
        print(f"  {C.red('✗')} PIN rejected by device.")
        return False

    # ------------------------------------------------------------------
    # History and cleanup
    # ------------------------------------------------------------------

    def _load_history(self) -> None:
        try:
            import readline
            CONFIG_DIR.mkdir(parents=True, exist_ok=True)
            if HISTORY_FILE.exists():
                readline.read_history_file(str(HISTORY_FILE))
            readline.set_history_length(500)
        except (ImportError, OSError):
            pass

    def _save_history(self) -> None:
        try:
            import readline
            CONFIG_DIR.mkdir(parents=True, exist_ok=True)
            readline.write_history_file(str(HISTORY_FILE))
        except (ImportError, OSError):
            pass

    def _cleanup(self) -> None:
        if self._cleaned_up:
            return
        self._cleaned_up = True
        self._save_history()
        if self._client:
            try:
                self._client.stop()
            except Exception:
                pass
        if self._debug_handler:
            disable_debug(self._debug_handler)

    def postloop(self) -> None:
        self._cleanup()

    def cmdloop(self, intro=None) -> None:  # type: ignore[override]
        try:
            super().cmdloop(intro)
        except KeyboardInterrupt:
            print()
            self._cleanup()

    # ------------------------------------------------------------------
    # Exit and shell housekeeping
    # ------------------------------------------------------------------

    def do_quit(self, _arg: str) -> bool:  # type: ignore[override]
        """Exit the CLI."""
        print(f"  {C.dim('Bye!')}")
        return True

    do_exit = do_quit
    do_EOF = do_quit

    def emptyline(self) -> None:
        pass

    def default(self, line: str) -> None:
        cmd_name = line.split()[0] if line.split() else line
        print(f"  {C.yellow('!')} Unknown command: {cmd_name!r}  —  type {C.cyan('help')} for a list.")
