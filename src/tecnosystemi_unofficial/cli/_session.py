"""Persistent session state for the Tecnosystemi CLI."""

from __future__ import annotations

import json
import sys
from dataclasses import dataclass, field
from pathlib import Path

CONFIG_DIR = Path.home() / ".tecno"
CONFIG_FILE = CONFIG_DIR / "config.json"
HISTORY_FILE = CONFIG_DIR / "history"
IDP_FILE = CONFIG_DIR / "idp.json"

_NO_PIN = "-1"
_DEFAULT_PIN = "1234"


@dataclass
class SessionState:
    """
    User preferences that survive between CLI invocations.

    Stored as JSON in ``~/.tecno/config.json``.

    PINs are stored per device IP in ``device_pins``:
        {"192.168.1.16": "1234", "192.168.1.40": "5678"}
    """

    ip: str = ""
    debug: bool = False
    device_type: str = "pico"
    device_pins: dict = field(default_factory=dict)

    # ------------------------------------------------------------------
    # PIN helpers
    # ------------------------------------------------------------------

    def get_pin(self, ip: str) -> str:
        """Return the stored PIN for *ip*, or ``"1234"`` if unknown."""
        return self.device_pins.get(ip, _DEFAULT_PIN)

    def set_pin(self, ip: str, pin: str) -> None:
        """Store *pin* for *ip* and persist to disk."""
        self.device_pins[ip] = pin
        self.save()

    def forget_pin(self, ip: str) -> None:
        """Remove the stored PIN for *ip* (if any) and persist."""
        self.device_pins.pop(ip, None)
        self.save()

    # ------------------------------------------------------------------
    # Persistence
    # ------------------------------------------------------------------

    def save(self) -> None:
        CONFIG_DIR.mkdir(parents=True, exist_ok=True)
        data = {
            "ip": self.ip,
            "debug": self.debug,
            "device_type": self.device_type,
            "device_pins": self.device_pins,
        }
        CONFIG_FILE.write_text(json.dumps(data, indent=2))

    @classmethod
    def load(cls) -> "SessionState":
        try:
            raw = CONFIG_FILE.read_text()
            data = json.loads(raw)
        except FileNotFoundError:
            return cls()
        except (json.JSONDecodeError, TypeError) as exc:
            print(f"  Warning: could not load {CONFIG_FILE}: {exc}", file=sys.stderr)
            return cls()

        obj = cls(
            ip=data.get("ip", ""),
            debug=bool(data.get("debug", False)),
            device_type=data.get("device_type", "pico"),
            device_pins=dict(data.get("device_pins", {})),
        )

        # Migrate legacy single-pin config: {"ip": "x.x.x.x", "pin": "1234"}
        legacy_pin = data.get("pin", _NO_PIN)
        if legacy_pin != _NO_PIN and obj.ip and obj.ip not in obj.device_pins:
            obj.device_pins[obj.ip] = legacy_pin

        return obj
