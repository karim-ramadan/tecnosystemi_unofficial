"""
Interactive REPL and shared command helpers for the Tecnosystemi CLI.
"""

from __future__ import annotations

import asyncio
import cmd
import json
import logging
import socket
import sys
import threading
import time
from typing import Optional

from ..client import TecnoClient
from ..devices import PicoDevice
from ..idp import IDPManager
from ..shared_listener import SharedUDPListener
from ._colors import C
from ._session import CONFIG_DIR, HISTORY_FILE, IDP_FILE, SessionState

_SEND_PORT = 40070
_RECV_PORT = 40069
_SUBNETS = ["192.168.1", "192.168.0", "192.168.4"]

# Tag placed on the CLI debug handler so we can detect/avoid duplicates.
_DEBUG_TAG = "_tecno_cli_debug"

# ---------------------------------------------------------------------------
# Mode / speed tables
# ---------------------------------------------------------------------------

MODES: dict[int, tuple[str, str]] = {
    1:  ("Recupero",          "Heat-recovery: simultaneous supply + exhaust"),
    2:  ("Estrazione",        "Extraction only: exhaust air out"),
    3:  ("Immissione",        "Supply only: fresh air in"),
    4:  ("Auto Umidità ☀",    "Auto humidity – summer (fans activate when humidity is high)"),
    5:  ("Auto Umidità ❄",    "Auto humidity – winter"),
    6:  ("Comfort Estate",    "Comfort summer: CO₂ + humidity controlled heat-recovery"),
    7:  ("Comfort Inverno",   "Comfort winter: CO₂ + humidity controlled heat-recovery"),
    8:  ("CO₂ Recupero",      "CO₂-triggered heat-recovery ventilation"),
    9:  ("CO₂ Estrazione",    "CO₂-triggered extraction only"),
    10: ("Auto Umidità 2 ☀",  "Secondary humidity auto – summer"),
    11: ("Auto Umidità 2 ❄",  "Secondary humidity auto – winter"),
    12: ("Ricambio Naturale", "Natural air exchange (minimal/no forced ventilation)"),
}

SPEEDS: dict[int, str] = {1: "Min", 2: "Low", 3: "Medium", 4: "High", 5: "Max"}


# ---------------------------------------------------------------------------
# Module-level helpers
# ---------------------------------------------------------------------------


def discover(timeout: float = 2.0) -> list[str]:
    """
    Broadcast a pico_info probe and collect responding device IPs.
    Uses :class:`SharedUDPListener` — no separate raw socket needed.
    """
    found: list[str] = []
    lock = threading.Lock()
    probe = json.dumps({"cmd": "pico_info", "pin": "-1", "idp": 1, "frm": "app"}).encode()

    def on_packet(packet: dict, addr: tuple) -> None:
        if packet.get("res") in (1, 99):
            ip = addr[0]
            with lock:
                if ip not in found:
                    found.append(ip)

    listener = SharedUDPListener.get(_RECV_PORT)
    listener.register_raw(on_packet)
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        try:
            for subnet in _SUBNETS:
                sock.sendto(probe, (f"{subnet}.255", _SEND_PORT))
                if subnet == "192.168.4":
                    sock.sendto(probe, ("192.168.4.1", _SEND_PORT))
        finally:
            sock.close()
        time.sleep(timeout)
    finally:
        listener.unregister_raw(on_packet)
    return found


def enable_debug() -> Optional[logging.Handler]:
    """
    Enable DEBUG-level logging on the library's root logger.

    Returns the added handler, or ``None`` if debug was already active.
    Does not add duplicate handlers.
    """
    lib_logger = logging.getLogger("tecnosystemi_unofficial")
    for h in lib_logger.handlers:
        if getattr(h, _DEBUG_TAG, False):
            return None  # already enabled

    handler = logging.StreamHandler(sys.stderr)
    handler.setFormatter(
        logging.Formatter(fmt="[debug] %(message)s")
    )
    handler.setLevel(logging.DEBUG)
    setattr(handler, _DEBUG_TAG, True)
    lib_logger.setLevel(logging.DEBUG)
    lib_logger.addHandler(handler)
    return handler


def disable_debug(handler: logging.Handler) -> None:
    """Remove the debug handler and reset the library log level."""
    lib_logger = logging.getLogger("tecnosystemi_unofficial")
    lib_logger.removeHandler(handler)
    if not lib_logger.handlers:
        lib_logger.setLevel(logging.WARNING)


_STATE_DISPLAY = [
    ("on_off",    lambda v: "ON" if v == 1 else "OFF"),
    ("mod",       str),
    ("speed",     str),
    ("spd_row",   str),
    ("spd_rich",  str),
    ("umd",       str),
    ("s_umd",     str),
    ("AMB_tmpr",  lambda v: f"{v} °C"),
    ("EXT_tmpr",  lambda v: f"{v} °C"),
    ("night_mod", lambda v: "on" if v else "off"),
    ("m_crono",   str),
    ("fw_ver",    str),
    ("has_slave", str),
    ("vr",        str),
]
_KNOWN_STATE = {k for k, _ in _STATE_DISPLAY} | {"idp", "frm", "res", "cmd", "pin"}


def print_info(info: Optional[dict]) -> None:
    if info is None:
        print(f"  {C.red('✗')} No response (timeout).")
        return
    header = C.bold("─────  Device Info  ─────")
    print(f"\n  {header}\n")
    for k in ("ser", "fw_ver", "fw_note", "name", "has_slave"):
        if k in info:
            print(f"  {C.cyan(f'{k:14s}')} = {info[k]}")
    print()


def print_state(state: Optional[dict]) -> None:
    if state is None:
        print(f"  {C.red('✗')} No response (timeout).")
        return
    header = C.bold("─────  Device State  ─────")
    print(f"\n  {header}\n")
    for k, fmt in _STATE_DISPLAY:
        if k not in state:
            continue
        raw = state[k]
        value = fmt(raw)
        # Special colouring for certain keys
        if k == "on_off":
            value = C.green("ON") if raw == 1 else C.red("OFF")
        elif k == "mod":
            mode_info = MODES.get(raw)
            if mode_info:
                value = f"{raw}  {C.dim(f'({mode_info[0]} – {mode_info[1]})')}"
            else:
                value = str(raw)
        elif k == "speed":
            speed_name = SPEEDS.get(raw)
            value = f"{raw}  {C.dim(f'({speed_name})')}" if speed_name else str(raw)
        print(f"  {C.cyan(f'{k:16s}')} = {value}")
    extra = {k: v for k, v in state.items() if k not in _KNOWN_STATE}
    if extra:
        print(f"  {C.dim('┄ extra:')}")
        for k, v in extra.items():
            print(f"  {C.cyan(f'{k:16s}')} = {v}")
    print()


def parse_kv(arg: str) -> dict:
    """Parse ``"key=value key=value …"`` into a dict, coercing ints/floats."""
    fields: dict = {}
    for part in arg.split():
        if "=" not in part:
            continue
        k, _, v_str = part.partition("=")
        k = k.strip()
        if not k:
            continue
        try:
            v: object = int(v_str)
        except ValueError:
            try:
                v = float(v_str)
            except ValueError:
                v = v_str
        fields[k] = v
    return fields


# ---------------------------------------------------------------------------
# Interactive REPL
# ---------------------------------------------------------------------------


class TecnoREPL(cmd.Cmd):
    """
    Interactive REPL for Tecnosystemi device control.

    Commands: discover, select, info, state, set, on, off, speed, mode,
              humidity, night, pin, check_pin, debug, quit/exit.
    """

    intro = (
        f"\n  {C.bold('Tecnosystemi CLI')}\n"
        f"  Type {C.cyan('help')} for commands or {C.cyan('discover')} to find devices.\n"
    )

    def __init__(
        self,
        initial_ip: str = "",
        initial_pin: str = "",
        debug: bool = False,
    ) -> None:
        super().__init__()
        self._session = SessionState.load()

        # CLI args override saved state; save if anything changed.
        changed = False
        if initial_ip and initial_ip != self._session.ip:
            self._session.ip = initial_ip
            changed = True
        if initial_pin:
            # --pin on command line → save for this specific IP
            target_ip = initial_ip or self._session.ip
            if target_ip and self._session.get_pin(target_ip) != initial_pin:
                self._session.set_pin(target_ip, initial_pin)
                changed = False  # set_pin already saves
        if debug and not self._session.debug:
            self._session.debug = True
            changed = True
        if changed:
            self._session.save()

        self._client: Optional[TecnoClient] = None
        self._pico: Optional[PicoDevice] = None
        self._last_discovered: list[str] = []
        self._debug_handler: Optional[logging.Handler] = None
        self._cleaned_up = False

        if self._session.debug:
            self._debug_handler = enable_debug()

        self._update_prompt()
        self._load_history()

        # Auto-connect to last known device, but don't fail if it's stale.
        if self._session.ip:
            self._connect(self._session.ip, silent=True)

    # ------------------------------------------------------------------
    # Lifecycle
    # ------------------------------------------------------------------

    def _update_prompt(self) -> None:
        ip = self._client.ip if self._client else ""
        if ip:
            self.prompt = C.prompt(f"  (tecno {ip}) > ")
        else:
            self.prompt = C.prompt("  (tecno) > ")

    def _connect(self, ip: str, silent: bool = False) -> bool:
        """
        Connect to *ip*.  Stops any existing client first.
        Assigns ``self._client`` only after ``start()`` succeeds.
        Returns True on success.
        """
        if self._client:
            try:
                self._client.stop()
            except Exception:
                pass
            self._client = None
            self._pico = None
            self._update_prompt()

        client = None
        try:
            idp_mgr = IDPManager(backend="file", path=IDP_FILE)
            client = TecnoClient(ip=ip, idp_manager=idp_mgr, timeout=12.0)
            client.start()
            self._client = client
            pin = self._session.get_pin(ip)
            self._pico = PicoDevice(self._client, pin=pin)
            self._session.ip = ip
            self._session.save()
            self._update_prompt()
            if not silent:
                stored = pin != "-1"
                print(f"  {C.green('✓')} Connected to {C.bold(ip)}" + ("" if stored else f"  {C.dim('(no PIN stored — run \"pin <value>\" to save one)')}"))
            return True
        except Exception as exc:
            if client is not None:
                try:
                    client.stop()
                except Exception:
                    pass
            print(f"  {C.red('✗')} Could not connect to {ip}: {exc}")
            return False

    def _require_device(self) -> bool:
        if self._pico is None:
            print(f"  {C.yellow('!')} No device selected.  Run {C.cyan('discover')}, then {C.cyan('select <n>')}.")
            return False
        return True

    def _ensure_pin(self) -> bool:
        """
        Ensure the active device has a valid PIN.

        If the stored PIN is "-1", prompts the user interactively,
        validates it against the device with ``check_pin``, and saves
        it on success.  Returns ``True`` when a valid PIN is available.
        """
        if self._pico is None:
            return False
        if self._pico.pin != "-1":
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

        # Validate against the device.
        old_pin = self._pico.pin
        self._pico.pin = candidate
        print("  Checking PIN …")
        if asyncio.run(self._pico.check_pin(timeout=8.0)):
            ip = self._client.ip  # type: ignore[union-attr]
            self._session.set_pin(ip, candidate)
            print(f"  {C.green('✓')} PIN accepted and saved for {ip}")
            return True
        else:
            self._pico.pin = old_pin
            print(f"  {C.red('✗')} PIN rejected by device.")
            return False

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
            self._cleanup()  # postloop not called when exception unwinds super()

    # ------------------------------------------------------------------
    # Commands
    # ------------------------------------------------------------------

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
            self._last_discovered = []
            return
        self._last_discovered = found
        print(f"\n  Found {C.bold(str(len(found)))} device(s):\n")
        for i, ip in enumerate(found, 1):
            if ip == self._session.ip:
                marker = f"  {C.green('◀ active')}"
            else:
                marker = ""
            print(f"    [{i}]  {C.bold(ip)}{marker}")
        print()

        # Interactive auto-connect
        if not sys.stdin.isatty():
            print(f"  Use 'select <n>' or 'select <IP>' to connect.")
            return

        if len(found) == 1:
            print(f"  Auto-connecting to {C.bold(found[0])} …")
            self._connect(found[0])
        else:
            try:
                choice = input("  Connect to device? (number or Enter to skip): ").strip()
            except (EOFError, KeyboardInterrupt):
                print()
                return
            if choice.isdigit():
                idx = int(choice) - 1
                if 0 <= idx < len(found):
                    self._connect(found[idx])
                else:
                    print(f"  {C.yellow('!')} Index out of range.")

    def do_select(self, arg: str) -> None:
        """select <n|IP>  —  connect to a device by list number or IP."""
        arg = arg.strip()
        if not arg:
            if self._last_discovered:
                for i, ip in enumerate(self._last_discovered, 1):
                    marker = "  ◀" if ip == self._session.ip else ""
                    print(f"    [{i}]  {ip}{marker}")
                print("\n  Re-run 'discover' to refresh the list.")
            else:
                print("  Run 'discover' first.")
            return
        if arg.isdigit():
            idx = int(arg) - 1
            if not self._last_discovered:
                print("  Run 'discover' first to get a numbered list.")
                return
            if not (0 <= idx < len(self._last_discovered)):
                print(f"  ✗ Index out of range (1–{len(self._last_discovered)}).")
                return
            ip = self._last_discovered[idx]
        else:
            ip = arg
        self._connect(ip)

    def do_info(self, _arg: str) -> None:
        """Fetch and display device information (no PIN required)."""
        if not self._require_device():
            return
        print("  Fetching info …")
        print_info(asyncio.run(self._pico.get_info(timeout=12.0)))  # type: ignore[union-attr]

    def do_state(self, _arg: str) -> None:
        """Fetch and display full device state (PIN required)."""
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        print("  Fetching state …")
        print_state(asyncio.run(self._pico.get_state(timeout=15.0)))  # type: ignore[union-attr]

    def do_set(self, arg: str) -> None:
        """set key=value [key=value ...]  —  update device fields.

        Examples:
          set on_off=1
          set speed=3 mod=2
          set night_mod=1 s_umd=60
        """
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        fields = parse_kv(arg)
        if not fields:
            print("  Usage: set key=value [key=value ...]")
            return
        ok = asyncio.run(self._pico.update(**fields))  # type: ignore[union-attr]
        if ok:
            print(f"  {C.green('✓')}  {' '.join(f'{k}={v}' for k, v in fields.items())}")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_on(self, _arg: str) -> None:
        """Turn the device ON."""
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        if asyncio.run(self._pico.turn_on()):  # type: ignore[union-attr]
            print(f"  {C.green('✓')} Device {C.green('ON')}")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_off(self, _arg: str) -> None:
        """Turn the device OFF."""
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        if asyncio.run(self._pico.turn_off()):  # type: ignore[union-attr]
            print(f"  {C.green('✓')} Device {C.red('OFF')}")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_speed(self, arg: str) -> None:
        """speed <1-5> [raw_0-100]  —  set fan speed."""
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        parts = arg.strip().split()
        if not parts or not parts[0].isdigit():
            print("  Usage: speed <1-5> [raw_0-100]")
            return
        speed = int(parts[0])
        raw: Optional[int] = int(parts[1]) if len(parts) > 1 and parts[1].isdigit() else None
        ok = asyncio.run(self._pico.set_speed(speed, speed_raw=raw))  # type: ignore[union-attr]
        if ok:
            speed_name = SPEEDS.get(speed, "")
            label = f"{speed}" + (f"  {C.dim(f'({speed_name})')}" if speed_name else "")
            print(f"  {C.green('✓')} Speed → {label}" + (f" (raw {raw})" if raw is not None else ""))
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_mode(self, arg: str) -> None:
        """mode [1-12]  —  set operating mode (shows menu if no argument).

        Modes:
          1  Recupero           – Heat-recovery (supply + exhaust simultaneously)
          2  Estrazione         – Extraction only (exhaust air out)
          3  Immissione         – Supply only (fresh air in)
          4  Auto Umidità ☀     – Auto humidity, summer
          5  Auto Umidità ❄     – Auto humidity, winter
          6  Comfort Estate     – Comfort summer (CO₂ + humidity controlled)
          7  Comfort Inverno    – Comfort winter (CO₂ + humidity controlled)
          8  CO₂ Recupero       – CO₂-triggered heat-recovery
          9  CO₂ Estrazione     – CO₂-triggered extraction
         10  Auto Umidità 2 ☀   – Secondary humidity auto, summer
         11  Auto Umidità 2 ❄   – Secondary humidity auto, winter
         12  Ricambio Naturale  – Natural air exchange (no forced ventilation)
        """
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        arg = arg.strip()

        if not arg:
            # Show interactive menu
            print(f"\n  {C.bold('Operating modes:')}\n")
            for num, (name, desc) in MODES.items():
                print(f"   {C.cyan(f'[{num:2d}]')}  {name:<20s} {C.dim('–')} {desc}")
            print()
            if not sys.stdin.isatty():
                return
            try:
                choice = input("  Select mode (1-12, or Enter to cancel): ").strip()
            except (EOFError, KeyboardInterrupt):
                print()
                return
            if not choice:
                return
            arg = choice

        if not arg.isdigit():
            print(f"  {C.yellow('!')} Usage: mode <1-12>")
            return
        mode_num = int(arg)
        ok = asyncio.run(self._pico.set_mode(mode_num))  # type: ignore[union-attr]
        if ok:
            mode_info = MODES.get(mode_num)
            label = f"{mode_num}" + (f"  {C.dim(f'({mode_info[0]})')}" if mode_info else "")
            print(f"  {C.green('✓')} Mode → {label}")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_humidity(self, arg: str) -> None:
        """humidity <0-100>  —  set target humidity (s_umd)."""
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        arg = arg.strip()
        if not arg.isdigit():
            print("  Usage: humidity <0-100>")
            return
        ok = asyncio.run(self._pico.set_humidity(int(arg)))  # type: ignore[union-attr]
        if ok:
            print(f"  {C.green('✓')} Humidity → {arg}%")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_night(self, arg: str) -> None:
        """night [on|off]  —  toggle night mode."""
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        arg = arg.strip().lower()
        if arg in ("on", "1", "true", ""):
            enabled = True
        elif arg in ("off", "0", "false"):
            enabled = False
        else:
            print("  Usage: night on | night off")
            return
        ok = asyncio.run(self._pico.set_night_mode(enabled))  # type: ignore[union-attr]
        if ok:
            print(f"  {C.green('✓')} Night mode → {'on' if enabled else 'off'}")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_pin(self, arg: str) -> None:
        """pin [<value> | forget | list]  —  manage per-device PINs.

        pin             Show PIN for the active device
        pin 1234        Save PIN 1234 for the active device (validates first)
        pin forget      Remove stored PIN for the active device
        pin list        Show all stored device PINs
        """
        arg = arg.strip()

        if arg == "list":
            pins = self._session.device_pins
            if not pins:
                print("  No PINs stored yet.")
            else:
                print("  Stored PINs:")
                for dev_ip, dev_pin in pins.items():
                    marker = "  ◀" if dev_ip == (self._client.ip if self._client else "") else ""
                    print(f"    {dev_ip:20s}  {dev_pin}{marker}")
            return

        if not self._require_device():
            return

        ip = self._client.ip  # type: ignore[union-attr]

        if not arg:
            stored = self._session.get_pin(ip)
            print(f"  PIN for {ip}: {stored!r}" + ("  (not set)" if stored == "-1" else ""))
            return

        if arg == "forget":
            self._session.forget_pin(ip)
            if self._pico:
                self._pico.pin = "-1"
            print(f"  PIN for {ip} removed.")
            return

        # Set new PIN — validate against the device first.
        old_pin = self._pico.pin  # type: ignore[union-attr]
        self._pico.pin = arg  # type: ignore[union-attr]
        print("  Checking PIN …")
        if asyncio.run(self._pico.check_pin(timeout=8.0)):  # type: ignore[union-attr]
            self._session.set_pin(ip, arg)
            print(f"  {C.green('✓')} PIN accepted and saved for {ip}")
        else:
            self._pico.pin = old_pin  # type: ignore[union-attr]
            print(f"  {C.red('✗')} PIN rejected by device.  PIN not saved.")

    def do_check_pin(self, _arg: str) -> None:
        """Check whether the stored PIN is accepted by the active device."""
        if not self._require_device():
            return
        print("  Checking PIN …")
        if asyncio.run(self._pico.check_pin()):  # type: ignore[union-attr]
            print(f"  {C.green('✓')} PIN accepted.")
        else:
            print(f"  {C.red('✗')} PIN rejected (or no response).")

    def do_debug(self, arg: str) -> None:
        """debug [on|off]  —  toggle verbose TX/RX packet logging."""
        arg = arg.strip().lower()
        if arg in ("on", "1", "true", "yes", ""):
            if not self._debug_handler:
                self._debug_handler = enable_debug()
                self._session.debug = True
                self._session.save()
            print("  Debug ON  (TX/RX packets → stderr)")
        elif arg in ("off", "0", "false", "no"):
            if self._debug_handler:
                disable_debug(self._debug_handler)
                self._debug_handler = None
                self._session.debug = False
                self._session.save()
            print("  Debug OFF")
        else:
            status = "ON" if self._debug_handler else "OFF"
            print(f"  Debug is {status}.  Use: debug on | debug off")

    def do_quit(self, _arg: str) -> bool:  # type: ignore[override]
        """Exit the CLI."""
        print(f"  {C.dim('Bye!')}")
        return True

    do_exit = do_quit
    do_EOF = do_quit

    def emptyline(self) -> None:
        pass  # don't re-run last command on empty input

    def default(self, line: str) -> None:
        cmd_name = line.split()[0] if line.split() else line
        print(f"  {C.yellow('!')} Unknown command: {cmd_name!r}  —  type {C.cyan('help')} for a list.")
