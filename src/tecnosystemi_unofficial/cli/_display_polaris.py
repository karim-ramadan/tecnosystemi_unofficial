"""Polaris 5X display helpers: zone utilities and state printer."""
from __future__ import annotations

from typing import Optional

from ..devices import Polaris5XDevice
from ._colors import C

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


def _as_int(v) -> Optional[int]:
    try:
        return int(v)
    except (TypeError, ValueError):
        return None


def _zone_nr(z: dict) -> Optional[int]:
    """Return the numeric zone number (id_zona full / nr ridotto)."""
    return _as_int(z.get("id_zona", z.get("nr")))


def _zone_name(z: dict) -> str:
    """Return the zone name string (name full / n ridotto)."""
    return str(z.get("name", z.get("n", ""))).strip()


def _find_zone(zones: list, raw: str) -> Optional[dict]:
    """Find a zone by its numeric number (1–4)."""
    try:
        numeric = int(raw)
    except ValueError:
        return None
    return next((z for z in zones if _zone_nr(z) == numeric), None)


def _zones_available(zones: list) -> str:
    """Human-readable list: '1 (CUCINA), 2 (CAMERETTA), …'"""
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
    print(f"  [{C.bold(str(nr))}] {name:<20} [{status}]  T={temp_str:<8} SP={setp_str}{extras_str}")


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
