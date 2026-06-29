"""
Polaris5XDevice: high-level async interface for Tecnosystemi Polaris 5X (CU)
multi-zone HVAC control units.

Transport: TCP port 1235 via ``PolarisClient``.
Protocol:  stateless per-command connections; command key is ``"c"``.
           No IDP/frm envelope; PIN sent with every command.

Commands
---------
  stato_r        Compact state poll (ridotto format); device replies res=4 if
                 unsupported → caller falls back to full ``stato``.
  stato          Full state (all fields).
  upd_cu         Update CU-level settings (power, mode, fan speeds).
  upd_zona       Update a single zone (temp setpoint, on/off, crono, fan/shutter).

State field names
------------------
Both the ridotto (stato_r) and full (stato) responses use snake_case keys.
Ridotto uses short aliases (``off``, ``cl``, ``cl_m``, ``tc``, ``fi``, ``fe``);
full uses longer names (``is_off``, ``is_cool``, ``cool_mod``, ``t_can``,
``f_inv``, ``f_est``).  ``Polaris5XDevice`` transparently handles both.

Zone fields are nested under the ``"zone"`` list in the response.

Operating modes (``cool_mod`` / ``operating_mode``)
----------------------------------------------------
  0  Riscaldamento    – heating      (is_cool=0)
  1  Raffrescamento   – cooling      (is_cool=1)
  2  Deumidificazione – dehumidification (is_cool=1)
  3  Ventilazione     – ventilation only (is_cool=1)

on/off encoding (CU and zones)
-------------------------------
  0 = ON   (device / zone is active)
  1 = OFF  (device / zone is inactive)
"""

from __future__ import annotations

from typing import Optional

from ..polaris_client import PolarisClient

OPERATING_MODE_HEATING = 0
OPERATING_MODE_COOLING = 1
OPERATING_MODE_DEHUMIDIFICATION = 2
OPERATING_MODE_VENTILATION = 3

OPERATING_MODE_NAMES: dict[int, str] = {
    OPERATING_MODE_HEATING: "Riscaldamento",
    OPERATING_MODE_COOLING: "Raffrescamento",
    OPERATING_MODE_DEHUMIDIFICATION: "Deumidificazione",
    OPERATING_MODE_VENTILATION: "Ventilazione",
}

# Error bitmask for the CU (err_cu field).  Bit 4 = PIN error.
_CU_PIN_ERROR_BIT = 4


def _get(state: dict, *keys, default=0):
    """Return the first key found in *state*, falling back to *default*."""
    for k in keys:
        if k in state:
            return state[k]
    return default


class Polaris5XDevice:
    """
    High-level async Polaris 5X (CU) device controller.

    Args:
        client: A ``PolarisClient`` pointed at the device IP.
    """

    def __init__(self, client: PolarisClient) -> None:
        self.client = client

    # PIN forwarded to/from the underlying client so the CLI can manage it.
    @property
    def pin(self) -> str:
        return self.client.pin

    @pin.setter
    def pin(self, value: str) -> None:
        self.client.pin = value

    # ------------------------------------------------------------------
    # Read operations
    # ------------------------------------------------------------------

    async def get_state(self, timeout: Optional[float] = None) -> Optional[dict]:
        """
        Return the full device state (all CU fields + zone list).

        Tries the compact ``stato_r`` first; if the device replies with
        ``res=4`` (command not found) it retries with the full ``stato``.
        """
        response = await self.client.send_command(
            {"c": "stato_r", "pin": self.pin}, timeout=timeout
        )
        if response is not None and response.get("res") == 4:
            response = await self.client.send_command(
                {"c": "stato", "pin": self.pin}, timeout=timeout
            )
        return response

    async def check_pin(self, timeout: Optional[float] = None) -> bool:
        """
        Return ``True`` if the PIN is accepted by the device.

        Sends a state request; a PIN error is signalled by bit 4 of ``err_cu``.
        """
        response = await self.get_state(timeout=timeout or 5.0)
        if response is None:
            return False
        err_cu = _get(response, "err_cu", "err", default=0)
        if isinstance(err_cu, int) and (err_cu >> _CU_PIN_ERROR_BIT) & 1:
            return False
        return True

    # ------------------------------------------------------------------
    # CU-level control
    # ------------------------------------------------------------------

    async def turn_on(self, timeout: Optional[float] = None) -> bool:
        """Turn the CU ON (is_off=0)."""
        return await self._upd_cu({"is_off": 0}, timeout=timeout)

    async def turn_off(self, timeout: Optional[float] = None) -> bool:
        """Turn the CU OFF (is_off=1)."""
        return await self._upd_cu({"is_off": 1}, timeout=timeout)

    async def set_mode(self, mode: int, timeout: Optional[float] = None) -> bool:
        """
        Set operating mode (also ensures CU is ON).

        Modes:
            0  OPERATING_MODE_HEATING          – Riscaldamento
            1  OPERATING_MODE_COOLING          – Raffrescamento
            2  OPERATING_MODE_DEHUMIDIFICATION – Deumidificazione
            3  OPERATING_MODE_VENTILATION      – Ventilazione
        """
        if mode == OPERATING_MODE_HEATING:
            overrides = {"is_off": 0, "is_cool": 0, "cool_mod": 0}
        else:
            overrides = {"is_off": 0, "is_cool": 1, "cool_mod": mode}
        return await self._upd_cu(overrides, timeout=timeout)

    async def update(self, timeout: Optional[float] = None, **fields) -> bool:
        """
        Low-level: send arbitrary ``upd_cu`` fields.

        Example::

            await polaris.update(is_off=0, is_cool=1, cool_mod=1)
        """
        return await self._upd_cu(fields, timeout=timeout)

    # ------------------------------------------------------------------
    # Zone-level control
    # ------------------------------------------------------------------

    async def update_zone(
        self,
        zone_id: int,
        name: str,
        *,
        is_off: int = 0,
        set_temp: float,
        is_crono: int = 0,
        fan_set: Optional[int] = None,
        shu_set: Optional[int] = None,
        timeout: Optional[float] = None,
    ) -> bool:
        """
        Update a zone's settings.

        Args:
            zone_id:  Zone ID (``id_zona`` from state).
            name:     Zone name.
            is_off:   0=on, 1=off.
            set_temp: Temperature setpoint in °C (sent as int × 10).
            is_crono: 0=manual, 1=schedule mode.
            fan_set:  Fan coil setpoint (-1 = not installed).
            shu_set:  Shutter setpoint (-1 = not installed).
        """
        cmd: dict = {
            "c": "upd_zona",
            "id_zona": zone_id,
            "name": name,
            "is_off": is_off,
            "t_set": str(round(set_temp * 10)),
            "is_crono": is_crono,
            "pin": self.pin,
        }
        # The protocol always requires fan_set and shu_set together and uses
        # the same value for both (mirrors Java update_ZONA_Command logic).
        combined = fan_set if fan_set is not None else shu_set
        if combined is not None and combined != -1:
            cmd["fan_set"] = combined
            cmd["shu_set"] = combined

        result = await self.client.send_command(cmd, timeout=timeout)
        return result is not None

    # ------------------------------------------------------------------
    # Helpers
    # ------------------------------------------------------------------

    @staticmethod
    def parse_zone_temperature(raw) -> Optional[float]:
        """
        Convert a raw zone temperature to °C.

        Values ≥ 100 are integer-encoded (e.g. 195 → 19.5 °C).
        Float values are returned as-is.
        Returns ``None`` for ``None`` or ``0``.
        """
        if raw is None or raw == 0:
            return None
        try:
            v = float(raw)
            return v / 10.0 if abs(v) >= 100 else v
        except (ValueError, TypeError):
            return None

    @staticmethod
    def get_mode_name(mode: int) -> Optional[str]:
        """Return the human-readable name for an operating mode integer."""
        return OPERATING_MODE_NAMES.get(mode)

    # ------------------------------------------------------------------
    # Internal
    # ------------------------------------------------------------------

    async def _upd_cu(self, overrides: dict, timeout: Optional[float]) -> bool:
        """
        Fetch current state, merge *overrides*, send ``upd_cu``.

        The protocol requires ``t_can``, ``f_inv``, and ``f_est`` in every
        ``upd_cu`` command, so we read them from the current device state.
        """
        state = await self.get_state(timeout=timeout or 5.0)
        if state is None:
            return False

        cmd: dict = {
            "c": "upd_cu",
            "pin": self.pin,
            "is_off": _get(state, "is_off", "off", default=0),
            "is_cool": _get(state, "is_cool", "cl", default=0),
            "cool_mod": _get(state, "cool_mod", "cl_m", default=0),
            "t_can": _get(state, "t_can", "tc", default=0),
            "f_inv": _get(state, "f_inv", "fi", default=0),
            "f_est": _get(state, "f_est", "fe", default=0),
            **overrides,
        }
        result = await self.client.send_command(cmd, timeout=timeout or 5.0)
        return result is not None
