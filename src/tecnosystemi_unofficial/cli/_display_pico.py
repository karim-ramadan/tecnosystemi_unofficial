"""Pico device state display: mode/speed/LED tables and print helpers."""
from __future__ import annotations

from typing import Optional

from ._colors import C

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


def _led_swatch(hex_color: str) -> str:
    h = hex_color.lstrip("#")
    r, g, b = int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)
    return C.rgb(r, g, b, "■")


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
    print(f"\n  {C.bold('─────  Device Info  ─────')}\n")
    for k in ("ser", "fw_ver", "fw_note", "name", "has_slave"):
        if k in info:
            print(f"  {C.cyan(f'{k:14s}')} = {info[k]}")
    print()


def print_state(state: Optional[dict]) -> None:
    if state is None:
        print(f"  {C.red('✗')} No response (timeout).")
        return
    print(f"\n  {C.bold('─────  Device State  ─────')}\n")
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
