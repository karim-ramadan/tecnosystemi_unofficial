"""
PicoDevice: high-level interface for Tecnosystemi Pico ventilation units.

Commands are sent through the shared ``TecnoClient``.  Every method also has a
``send_template`` shortcut so callers can drive the device from Jinja2 templates
without writing extra Python.

Operating modes (``mod`` field)
--------------------------------
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

on_off
------
1 = ON, 2 = OFF
"""

from typing import Optional

from ..client import TecnoClient


class PicoDevice:
    """
    High-level Pico device controller.

    Args:
        client: A started (or context-managed) ``TecnoClient``.
        pin:    Device PIN.  Use ``"-1"`` if the device has no PIN configured.
    """

    def __init__(self, client: TecnoClient, pin: str = "-1"):
        self.client = client
        self.pin = pin

    # ------------------------------------------------------------------
    # Read operations
    # ------------------------------------------------------------------

    def get_info(self, timeout: float = 15.0) -> Optional[dict]:
        """Return device information (serial, firmware, name …).  No PIN needed."""
        return self.client.send_command({"cmd": "pico_info", "pin": "-1"}, timeout=timeout)

    def get_state(self, timeout: float = 20.0) -> Optional[dict]:
        """
        Return the full device state (temperatures, speed, mode, humidity …).

        The device sends ``res:99`` first to acknowledge, then the real state
        payload, so a generous timeout is recommended.
        """
        return self.client.send_command(
            {"cmd": "stato_sync", "pin": self.pin}, timeout=timeout
        )

    def check_pin(self, timeout: float = 5.0) -> bool:
        """Return ``True`` if the current PIN is accepted by the device."""
        result = self.client.send_command(
            {"cmd": "check_pin", "pin": self.pin}, timeout=timeout
        )
        return result is not None and result.get("res") == 1

    # ------------------------------------------------------------------
    # Control operations
    # ------------------------------------------------------------------

    def turn_on(self, timeout: float = 5.0) -> bool:
        """Turn the device ON (on_off=1)."""
        return self._upd_pico({"on_off": 1}, timeout=timeout)

    def turn_off(self, timeout: float = 5.0) -> bool:
        """Turn the device OFF (on_off=2)."""
        return self._upd_pico({"on_off": 2}, timeout=timeout)

    def set_speed(
        self, speed: int, speed_raw: Optional[int] = None, timeout: float = 5.0
    ) -> bool:
        """Set fan speed.  Optionally also set the raw speed register."""
        fields: dict = {"speed": speed}
        if speed_raw is not None:
            fields["spd_row"] = speed_raw
        return self._upd_pico(fields, timeout=timeout)

    def set_mode(
        self, mode: int, on_off: Optional[int] = None, timeout: float = 5.0
    ) -> bool:
        """Set operating mode, optionally combined with an on/off change.

        Modes:
             1  Recupero           – Heat-recovery (supply + exhaust simultaneously)
             2  Estrazione         – Extraction only (exhaust air out)
             3  Immissione         – Supply only (fresh air in)
             4  Auto Umidità ☀     – Auto humidity, summer (fans on when humidity high)
             5  Auto Umidità ❄     – Auto humidity, winter
             6  Comfort Estate     – Comfort summer (CO₂ + humidity controlled recovery)
             7  Comfort Inverno    – Comfort winter (CO₂ + humidity controlled recovery)
             8  CO₂ Recupero       – CO₂-triggered heat-recovery
             9  CO₂ Estrazione     – CO₂-triggered extraction
            10  Auto Umidità 2 ☀   – Secondary humidity auto, summer
            11  Auto Umidità 2 ❄   – Secondary humidity auto, winter
            12  Ricambio Naturale  – Natural air exchange (no forced ventilation)
        """
        fields: dict = {"mod": mode}
        if on_off is not None:
            fields["on_off"] = on_off
        return self._upd_pico(fields, timeout=timeout)

    def set_humidity(self, humidity: int, timeout: float = 5.0) -> bool:
        """Set target humidity (s_umd)."""
        return self._upd_pico({"s_umd": humidity}, timeout=timeout)

    def set_led(self, led_state: int, timeout: float = 5.0) -> bool:
        """Set LED state (led_on_off_breve)."""
        return self._upd_pico({"led_on_off_breve": led_state}, timeout=timeout)

    def set_night_mode(self, enabled: bool, timeout: float = 5.0) -> bool:
        """Enable (True) or disable (False) night mode."""
        return self._upd_pico({"night_mod": 1 if enabled else 0}, timeout=timeout)

    def set_crono_mode(self, mode: int, timeout: float = 5.0) -> bool:
        """Set crono (timer schedule) mode."""
        return self._upd_pico({"m_crono": mode}, timeout=timeout)

    def reset_manual(self, man_reset: list[int], timeout: float = 5.0) -> bool:
        """Send a manual reset array."""
        return self._upd_pico({"man_reset": man_reset}, timeout=timeout)

    def check_led(self, led_color: int = 2, timeout: float = 5.0) -> Optional[dict]:
        """Query LED status."""
        return self.client.send_command(
            {"cmd": "check_led", "pin": self.pin, "led_color": led_color},
            timeout=timeout,
        )

    def update(self, timeout: float = 5.0, **fields) -> bool:
        """
        Low-level helper to set arbitrary ``upd_pico`` fields in one call.

        Example::

            pico.update(on_off=1, speed=3, mod=2)
        """
        return self._upd_pico(fields, timeout=timeout)

    # ------------------------------------------------------------------
    # Template-based interface
    # ------------------------------------------------------------------

    def send_template(
        self, template_name: str, timeout: Optional[float] = None, **context
    ) -> Optional[dict]:
        """
        Render a Jinja2 template and send the resulting command.

        The device PIN is injected as ``pin`` unless *context* already contains
        it.  ``idp`` and ``frm`` are always managed by the client.

        Example::

            pico.send_template("pico/upd_pico.json.j2", speed=3, on_off=1)
        """
        context.setdefault("pin", self.pin)
        return self.client.send_template(template_name, timeout=timeout, **context)

    # ------------------------------------------------------------------
    # Internal
    # ------------------------------------------------------------------

    def _upd_pico(self, fields: dict, timeout: float) -> bool:
        payload = {"cmd": "upd_pico", "pin": self.pin, **fields}
        result = self.client.send_command(payload, timeout=timeout)
        # Control commands return res:99; success means we got *any* response
        return result is not None and result.get("res") in (1, 99)
