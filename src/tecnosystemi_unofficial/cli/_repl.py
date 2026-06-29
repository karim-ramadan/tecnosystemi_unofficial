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
from typing import Optional, Union

from ..client import TecnoClient
from ..devices import PicoDevice, Polaris5XDevice
from ..idp import IDPManager
from ..polaris_client import PolarisClient
from ..shared_listener import SharedUDPListener
from ._colors import C
from ._session import CONFIG_DIR, HISTORY_FILE, SessionState

_SEND_PORT = 40070
_RECV_PORT = 40069
_SUBNETS = ["192.168.1", "192.168.0", "192.168.4"]

_DEBUG_TAG = "_tecno_cli_debug"

DEVICE_TYPES = ("pico", "polaris5x")

# ---------------------------------------------------------------------------
# Pico mode / speed tables
# ---------------------------------------------------------------------------

MODES: dict[int, tuple[str, str]] = {
    1: ("Recupero", "Heat-recovery: simultaneous supply + exhaust"),
    2: ("Estrazione", "Extraction only: exhaust air out"),
    3: ("Immissione", "Supply only: fresh air in"),
    4: ("Auto Umidità ☀", "Auto humidity – summer (fans activate when humidity is high)"),
    5: ("Auto Umidità ❄", "Auto humidity – winter"),
    6: ("Comfort Estate", "Comfort summer: CO₂ + humidity controlled heat-recovery"),
    7: ("Comfort Inverno", "Comfort winter: CO₂ + humidity controlled heat-recovery"),
    8: ("CO₂ Recupero", "CO₂-triggered heat-recovery ventilation"),
    9: ("CO₂ Estrazione", "CO₂-triggered extraction only"),
    10: ("Auto Umidità 2 ☀", "Secondary humidity auto – summer"),
    11: ("Auto Umidità 2 ❄", "Secondary humidity auto – winter"),
    12: ("Ricambio Naturale", "Natural air exchange (minimal/no forced ventilation)"),
}

SPEEDS: dict[int, str] = {1: "Min", 2: "Medium", 3: "Max"}

LED_COLORS: dict[int, tuple[str, str]] = {
    1: ("Turchese", "#4DB6AC"),
    2: ("Verde", "#5CB85C"),
    3: ("Fucsia", "#D81B60"),
    4: ("Giallo", "#E6DC2A"),
    5: ("Bianco", "#FFFFFF"),
    6: ("Viola", "#5B4B8A"),
    7: ("Verde (CO₂)", "#7FBF3F"),
    8: ("Blu", "#466FA6"),
    9: ("Blu scuro", "#2F5597"),
    10: ("Arancione", "#E67E2E"),
    11: ("Viola chiaro", "#B784A7"),
    12: ("Grigio", "#9E9E9E"),
}

# ---------------------------------------------------------------------------
# Polaris 5X mode table
# ---------------------------------------------------------------------------

P6X_MODES: dict[int, tuple[str, str]] = {
    0: ("Riscaldamento", "Heating"),
    1: ("Raffrescamento", "Cooling"),
    2: ("Deumidificazione", "Dehumidification"),
    3: ("Ventilazione", "Ventilation only"),
}

_P6X_COOL_MOD: dict[int, str] = {
    1: "Raffrescamento",
    2: "Deumidificazione",
    3: "Ventilazione",
}


def _led_swatch(hex_color: str) -> str:
    h = hex_color.lstrip("#")
    r, g, b = int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)
    return C.rgb(r, g, b, "■")


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


def register_device(ip: str, pin: Optional[str], session: SessionState) -> bool:
    """
    Connect to *ip*, optionally validate *pin*, and persist both to *session*.
    Returns True on success.
    """
    idp_mgr = IDPManager(backend="memory")
    client = TecnoClient(ip=ip, idp_manager=idp_mgr, timeout=12.0)
    try:
        client.start()
    except Exception as exc:
        print(f"  {C.red('✗')} Could not reach {ip}: {exc}")
        return False

    pico = PicoDevice(client, pin=pin or "-1")
    try:
        if pin:
            print("  Checking PIN …")
            ok = asyncio.run(pico.check_pin(timeout=8.0))
            if not ok:
                print(f"  {C.red('✗')} PIN rejected by device.")
                return False
            session.set_pin(ip, pin)
            print(f"  {C.green('✓')} PIN accepted and saved for {ip}")
        session.ip = ip
        session.save()
        print(f"  {C.green('✓')} Registered {C.bold(ip)}" + (
            "" if pin else f"  {C.dim('(no PIN — run: tecno --ip ' + ip + ' pin <value>)')}"))
        return True
    finally:
        client.stop()


def enable_debug() -> Optional[logging.Handler]:
    lib_logger = logging.getLogger("tecnosystemi_unofficial")
    for h in lib_logger.handlers:
        if getattr(h, _DEBUG_TAG, False):
            return None
    handler = logging.StreamHandler(sys.stderr)
    handler.setFormatter(logging.Formatter(fmt="[debug] %(message)s"))
    handler.setLevel(logging.DEBUG)
    setattr(handler, _DEBUG_TAG, True)
    lib_logger.setLevel(logging.DEBUG)
    lib_logger.addHandler(handler)
    return handler


def disable_debug(handler: logging.Handler) -> None:
    lib_logger = logging.getLogger("tecnosystemi_unofficial")
    lib_logger.removeHandler(handler)
    if not lib_logger.handlers:
        lib_logger.setLevel(logging.WARNING)


# ---------------------------------------------------------------------------
# State display helpers
# ---------------------------------------------------------------------------

_PICO_STATE_DISPLAY = [
    ("on_off", lambda v: "ON" if v == 1 else "OFF"),
    ("mod", str),
    ("speed", str),
    ("spd_row", str),
    ("spd_rich", str),
    ("umd", str),
    ("s_umd", str),
    ("AMB_tmpr", lambda v: f"{v} °C"),
    ("EXT_tmpr", lambda v: f"{v} °C"),
    ("night_mod", lambda v: "on" if v else "off"),
    ("m_crono", str),
    ("fw_ver", str),
    ("has_slave", str),
    ("vr", str),
]
_PICO_KNOWN_STATE = {k for k, _ in _PICO_STATE_DISPLAY} | {"idp", "frm", "res", "cmd", "pin"}


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
    for k, fmt in _PICO_STATE_DISPLAY:
        if k not in state:
            continue
        raw = state[k]
        value = fmt(raw)
        if k == "on_off":
            value = C.green("ON") if raw == 1 else C.red("OFF")
        elif k == "mod":
            mode_info = MODES.get(raw)
            led = LED_COLORS.get(raw)
            if mode_info:
                swatch = f" {_led_swatch(led[1])} {C.dim(led[0])}" if led else ""
                value = f"{raw}  {C.dim(f'({mode_info[0]} – {mode_info[1]})')}{swatch}"
            else:
                value = str(raw)
        elif k == "speed":
            speed_name = SPEEDS.get(raw)
            value = f"{raw}  {C.dim(f'({speed_name})')}" if speed_name else str(raw)
        print(f"  {C.cyan(f'{k:16s}')} = {value}")
    extra = {k: v for k, v in state.items() if k not in _PICO_KNOWN_STATE}
    if extra:
        print(f"  {C.dim('┄ extra:')}")
        for k, v in extra.items():
            print(f"  {C.cyan(f'{k:16s}')} = {v}")
    print()


def _as_int(v) -> Optional[int]:
    try:
        return int(v)
    except (TypeError, ValueError):
        return None


def _zone_nr(z: dict) -> Optional[int]:
    """Return the numeric zone number from a zone dict (id_zona full / nr ridotto)."""
    return _as_int(z.get("id_zona", z.get("nr")))


def _zone_name(z: dict) -> str:
    """Return the zone name string from a zone dict (name full / n ridotto)."""
    return str(z.get("name", z.get("n", ""))).strip()


def _find_zone(zones: list, raw: str) -> Optional[dict]:
    """Find a zone by its numeric number (id_zona / nr, 1–4)."""
    try:
        numeric = int(raw)
    except ValueError:
        return None
    return next((z for z in zones if _zone_nr(z) == numeric), None)


def _zones_available(zones: list) -> str:
    """Human-readable list of zones: '1 (CUCINA), 2 (CAMERETTA), …'"""
    parts = []
    for z in zones:
        nr = _zone_nr(z)
        name = _zone_name(z)
        parts.append(f"{nr} ({name})" if name else str(nr))
    return ", ".join(parts)


def _parse_setpoint(raw) -> Optional[float]:
    """Convert raw setpoint (string or int ×10) to °C."""
    if raw is None:
        return None
    try:
        v = float(raw)
        return v / 10.0 if abs(v) >= 100 else v
    except (ValueError, TypeError):
        return None


def _print_zone_row(z: dict) -> None:
    nr = _zone_nr(z) or "?"
    name = _zone_name(z)
    z_off = z.get("is_off", z.get("off", 0))
    status = C.red("OFF") if z_off == 1 else C.green("ON ")

    t_c = Polaris5XDevice.parse_zone_temperature(z.get("t"))
    ts_c = _parse_setpoint(z.get("t_set", z.get("ts")))

    temp_str = f"{t_c:.1f}°C" if t_c is not None else "–"
    setp_str = f"{ts_c:.1f}°C" if ts_c is not None else "–"

    fan = z.get("fan")
    extras = []
    if fan is not None and fan != -1:
        fan_set = z.get("fan_set")
        extras.append("fan=" + str(fan) + (f"→{fan_set}" if fan_set is not None and fan_set != -1 else ""))
    if z.get("is_crono"):
        extras.append(C.dim("crono"))
    if z.get("err"):
        extras.append(C.red(f"err={z['err']}"))
    extras_str = "  " + "  ".join(extras) if extras else ""

    print(
        f"  [{C.bold(str(nr))}] {name:<20} [{status}]"
        f"  T={temp_str:<8} SP={setp_str}{extras_str}"
    )


def _print_zone_detail(z: dict) -> None:
    z_off = z.get("is_off", z.get("off", 0))
    print(f"  {'Power':<20} " + (C.red("OFF") if z_off == 1 else C.green("ON")))

    t_c = Polaris5XDevice.parse_zone_temperature(z.get("t"))
    if t_c is not None:
        print(f"  {'Temperature':<20} {t_c:.1f} °C")

    ts_c = _parse_setpoint(z.get("t_set", z.get("ts")))
    if ts_c is not None:
        print(f"  {'Setpoint':<20} {ts_c:.1f} °C")

    fan = z.get("fan")
    if fan is not None:
        if fan == -1:
            print(f"  {'Fan coil':<20} not installed")
        else:
            fan_set = z.get("fan_set", "–")
            print(f"  {'Fan coil':<20} {fan}  (setpoint: {fan_set})")

    shu = z.get("shu")
    if shu is not None and shu != -1:
        print(f"  {'Shutter':<20} {shu}  (setpoint: {z.get('shu_set', '–')})")

    is_crono = z.get("is_crono", 0)
    print(f"  {'Schedule':<20} " + (C.green("on") if is_crono else C.dim("off")))

    err = z.get("err")
    if err:
        print(f"  {'Error':<20} {C.red(str(err))}")


def print_state_polaris5x(state: Optional[dict]) -> None:
    if state is None:
        print(f"  {C.red('✗')} No response (timeout).")
        return

    print(f"\n  {C.bold('─────  Polaris 5X  ─────')}\n")

    is_off = state.get("is_off", state.get("off"))
    is_cool = state.get("is_cool", state.get("cl"))
    cool_mod = state.get("cool_mod", state.get("cl_m"))

    if is_off is not None:
        power = C.red("OFF") if is_off == 1 else C.green("ON")
        print(f"  {'Power':<20} {power}")

    if is_cool is not None:
        if is_cool == 0:
            mode_str = "Riscaldamento (Heating)"
        else:
            sub = _P6X_COOL_MOD.get(cool_mod or 1, f"sub-mode {cool_mod}")
            mode_str = f"Raffrescamento — {sub}"
        print(f"  {'Mode':<20} {mode_str}")

    for label, keys in [
        ("Name",       ("name",)),
        ("Firmware",   ("fw_ver", "vfw")),
        ("Model",      ("modello",)),
        ("Uptime",     ("up_time",)),
        ("Wi-Fi RSSI", ("w_rssi",)),
    ]:
        for k in keys:
            if k in state:
                print(f"  {label:<20} {state[k]}")
                break

    t_can = state.get("t_can", state.get("tc"))
    f_inv = state.get("f_inv", state.get("fi"))
    f_est = state.get("f_est", state.get("fe"))
    if any(v is not None for v in (t_can, f_inv, f_est)):
        print(f"\n  {C.bold('CU Settings')}")
        if t_can is not None:
            tc = Polaris5XDevice.parse_zone_temperature(t_can)
            print(f"  {'Canal setpoint':<20} " + (f"{tc:.1f} °C" if tc is not None else str(t_can)))
        if f_inv is not None:
            print(f"  {'Fan (winter)':<20} {f_inv}")
        if f_est is not None:
            print(f"  {'Fan (summer)':<20} {f_est}")

    zones = state.get("zone") or state.get("z") or []
    if zones:
        print(f"\n  {C.bold('Zones:')}\n")
        for z in zones:
            _print_zone_row(z)
        print()

    _p6x_known = {
        "is_off", "off", "is_cool", "cl", "cool_mod", "cl_m",
        "zone", "z", "zp", "idp", "frm", "res", "cmd", "pin",
        "name", "fw_ver", "vfw", "modello", "ip", "up_time", "w_rssi",
        "master_nr", "maxcom", "vr", "m_crono", "config_mod",
        "t_can", "tc", "f_inv", "fi", "f_est", "fe", "err_cu",
    }
    extra = {k: v for k, v in state.items() if k not in _p6x_known}
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

AnyDevice = Union[PicoDevice, Polaris5XDevice]


class TecnoREPL(cmd.Cmd):
    """
    Interactive REPL for Tecnosystemi device control.

    Commands: discover, select, type, info, state, set, on, off, speed, mode,
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
    # Lifecycle
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
                stored = pin != "-1"
                print(f"  {C.green('✓')} Connected to {C.bold(ip)}" + (
                    "" if stored else f"  {C.dim('(no PIN stored — run \"pin <value>\" to save one)')}"))
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
        else:
            self._device.pin = old_pin
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
            self._cleanup()

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
            marker = f"  {C.green('◀ active')}" if ip == self._session.ip else ""
            print(f"    [{i}]  {C.bold(ip)}{marker}")
        print()

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

    def do_register(self, arg: str) -> None:
        """register <IP> [PIN]  —  manually add a device by IP (and optional PIN)."""
        parts = arg.strip().split()
        if not parts:
            print("  Usage: register <IP> [PIN]")
            return
        ip = parts[0]
        pin = parts[1] if len(parts) > 1 else None

        if not self._connect(ip):
            return

        if pin:
            old_pin = self._device.pin  # type: ignore[union-attr]
            self._device.pin = pin  # type: ignore[union-attr]
            print("  Checking PIN …")
            if asyncio.run(self._device.check_pin(timeout=8.0)):  # type: ignore[union-attr]
                self._session.set_pin(ip, pin)
                print(f"  {C.green('✓')} PIN accepted and saved for {ip}")
            else:
                self._device.pin = old_pin  # type: ignore[union-attr]
                print(f"  {C.red('✗')} PIN rejected by device.  Run 'pin <value>' to try again.")

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

    def do_type(self, arg: str) -> None:
        """type [pico|polaris5x]  —  show or switch device type.

        Examples:
          type              Show current device type
          type pico         Switch to Pico ventilation unit
          type polaris5x    Switch to Polaris 5X multi-zone HVAC
        """
        arg = arg.strip().lower()
        if not arg:
            print(f"  Device type: {C.bold(self._device_type)}")
            print(f"  Available: {', '.join(DEVICE_TYPES)}")
            return
        if arg not in DEVICE_TYPES:
            print(f"  {C.yellow('!')} Unknown type {arg!r}.  Choose from: {', '.join(DEVICE_TYPES)}")
            return
        if arg == self._device_type:
            print(f"  Already set to {C.bold(arg)}.")
            return
        self._device_type = arg
        self._session.device_type = arg
        self._session.save()
        print(f"  {C.green('✓')} Device type → {C.bold(arg)}")

        # Reconnect if a device is active so the correct class is used.
        if self._client and self._session.ip:
            ip = self._client.ip
            self._connect(ip, silent=True)
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

        dtype = self._device_type
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
            row("zone", "List all zones with state")
            row("zone <id>", "Show detailed zone state")
            row("zone <id> on|off", "Turn zone on / off")
            row("zone <id> temp <°C>", "Set temperature setpoint  (e.g. zone 1 temp 21.5)")
            row("zone <id> crono on|off", "Enable / disable schedule mode")
            row("zone <id> fan <n>", "Set fan coil speed")
            print()

        print(f"  {C.bold('Session:')}")
        row("debug [on|off]", "Toggle raw packet logging")
        row("quit / exit", "Exit the REPL")
        print(f"\n  {C.dim('Tip: help <command>  for full usage and examples')}\n")

    def do_info(self, _arg: str) -> None:
        """Fetch and display device information (Pico only — no PIN required)."""
        if not self._require_pico():
            return
        print("  Fetching info …")
        print_info(asyncio.run(self._device.get_info(timeout=12.0)))  # type: ignore[union-attr]

    def do_state(self, _arg: str) -> None:
        """Fetch and display full device state (PIN required)."""
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        print("  Fetching state …")
        state = asyncio.run(self._device.get_state(timeout=15.0))  # type: ignore[union-attr]
        if self._is_polaris5x():
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
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        fields = parse_kv(arg)
        if not fields:
            print("  Usage: set key=value [key=value ...]")
            return
        ok = asyncio.run(self._device.update(**fields))  # type: ignore[union-attr]
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
        if asyncio.run(self._device.turn_on()):  # type: ignore[union-attr]
            print(f"  {C.green('✓')} Device {C.green('ON')}")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_off(self, _arg: str) -> None:
        """Turn the device OFF."""
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        if asyncio.run(self._device.turn_off()):  # type: ignore[union-attr]
            print(f"  {C.green('✓')} Device {C.red('OFF')}")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_speed(self, arg: str) -> None:
        """speed <1-3> [raw_0-100]  —  set fan speed (Pico only)."""
        if not self._require_pico():
            return
        if not self._ensure_pin():
            return
        parts = arg.strip().split()
        if not parts or not parts[0].isdigit():
            print("  Usage: speed <1-3> [raw_0-100]")
            return
        speed = int(parts[0])
        raw: Optional[int] = int(parts[1]) if len(parts) > 1 and parts[1].isdigit() else None
        ok = asyncio.run(self._device.set_speed(speed, speed_raw=raw))  # type: ignore[union-attr]
        if ok:
            speed_name = SPEEDS.get(speed, "")
            label = f"{speed}" + (f"  {C.dim(f'({speed_name})')}" if speed_name else "")
            print(f"  {C.green('✓')} Speed → {label}" + (f" (raw {raw})" if raw is not None else ""))
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
        if not self._require_device():
            return
        if not self._ensure_pin():
            return
        arg = arg.strip()

        if self._is_polaris5x():
            mode_table = P6X_MODES
            range_hint = "0-3"
        else:
            mode_table = MODES
            range_hint = "1-12"

        if not arg:
            print(f"\n  {C.bold('Operating modes:')}\n")
            for num, info in mode_table.items():
                name, desc = info
                if self._is_pico():
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
        ok = asyncio.run(self._device.set_mode(mode_num))  # type: ignore[union-attr]
        if ok:
            mode_info = mode_table.get(mode_num)
            if mode_info:
                name, desc = mode_info
                if self._is_pico():
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

    def do_humidity(self, arg: str) -> None:
        """humidity <0-100>  —  set target humidity / s_umd (Pico only)."""
        if not self._require_pico():
            return
        if not self._ensure_pin():
            return
        arg = arg.strip()
        if not arg.isdigit():
            print("  Usage: humidity <0-100>")
            return
        ok = asyncio.run(self._device.set_humidity(int(arg)))  # type: ignore[union-attr]
        if ok:
            print(f"  {C.green('✓')} Humidity → {arg}%")
        else:
            print(f"  {C.red('✗')} Command timed out.")

    def do_night(self, arg: str) -> None:
        """night [on|off]  —  toggle night mode (Pico only)."""
        if not self._require_pico():
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
        ok = asyncio.run(self._device.set_night_mode(enabled))  # type: ignore[union-attr]
        if ok:
            print(f"  {C.green('✓')} Night mode → {'on' if enabled else 'off'}")
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
        if not self._require_device():
            return
        if not self._is_polaris5x():
            print(f"  {C.yellow('!')} 'zone' is only available for Polaris 5X.")
            return
        if not self._ensure_pin():
            return

        parts = arg.strip().split()

        print("  Fetching state …")
        state = asyncio.run(self._device.get_state(timeout=10.0))  # type: ignore[union-attr]
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
            ok = asyncio.run(self._device.update_zone(  # type: ignore[union-attr]
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
            ok = asyncio.run(self._device.update_zone(  # type: ignore[union-attr]
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
            ok = asyncio.run(self._device.update_zone(  # type: ignore[union-attr]
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
            ok = asyncio.run(self._device.update_zone(  # type: ignore[union-attr]
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
            if self._device:
                self._device.pin = "-1"
            print(f"  PIN for {ip} removed.")
            return

        old_pin = self._device.pin  # type: ignore[union-attr]
        self._device.pin = arg  # type: ignore[union-attr]
        print("  Checking PIN …")
        if asyncio.run(self._device.check_pin(timeout=8.0)):  # type: ignore[union-attr]
            self._session.set_pin(ip, arg)
            print(f"  {C.green('✓')} PIN accepted and saved for {ip}")
        else:
            self._device.pin = old_pin  # type: ignore[union-attr]
            print(f"  {C.red('✗')} PIN rejected by device.  PIN not saved.")

    def do_check_pin(self, _arg: str) -> None:
        """Check whether the stored PIN is accepted by the active device."""
        if not self._require_device():
            return
        print("  Checking PIN …")
        if asyncio.run(self._device.check_pin()):  # type: ignore[union-attr]
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
        pass

    def default(self, line: str) -> None:
        cmd_name = line.split()[0] if line.split() else line
        print(f"  {C.yellow('!')} Unknown command: {cmd_name!r}  —  type {C.cyan('help')} for a list.")
