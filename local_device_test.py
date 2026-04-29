#!/usr/bin/env python3
"""
Live device test script for tecnosystemi_unofficial.

Run with:  uv run python local_device_test.py [IP [IP2 ...]] [--pin PIN]

If no IPs are provided, performs a UDP broadcast scan on common subnets and
tests every device found.  Results are collected and a summary table is printed
at the end — a failure on one device does not abort the run.
"""

import asyncio
import json
import socket
import sys
import threading
import time
from dataclasses import dataclass, field
from pathlib import Path
from typing import Optional

# ---------------------------------------------------------------------------
# Make sure the library is importable when run from this directory
# ---------------------------------------------------------------------------
import subprocess
result = subprocess.run(
    [sys.executable, "-c", "import tecnosystemi_unofficial"],
    capture_output=True,
)
if result.returncode != 0:
    print("Installing tecnosystemi-unofficial in the current venv …")
    subprocess.run([sys.executable, "-m", "pip", "install", "-e", "."], check=True)

from tecnosystemi_unofficial import TecnoClient
from tecnosystemi_unofficial.devices import PicoDevice
from tecnosystemi_unofficial.idp import IDPManager
from tecnosystemi_unofficial.shared_listener import SharedUDPListener


COMMON_SUBNETS = ["192.168.1", "192.168.0", "192.168.4"]
SEND_PORT = 40070
RECV_PORT = 40069


# ---------------------------------------------------------------------------
# Result dataclass
# ---------------------------------------------------------------------------

@dataclass
class DeviceResult:
    ip: str
    info: Optional[dict] = None
    state: Optional[dict] = None
    error: Optional[str] = None

    @property
    def ok(self) -> bool:
        return self.error is None and self.info is not None


# ---------------------------------------------------------------------------
# Discovery — uses the shared listener so no extra socket is opened
# ---------------------------------------------------------------------------

def discover_devices(subnets: list[str], timeout: float = 2.0) -> list[str]:
    """
    Broadcast a pico_info probe on each subnet and collect responding IPs.
    Uses the SharedUDPListener so there is no port conflict with TecnoClient.
    """
    found: list[str] = []
    found_lock = threading.Lock()

    probe = json.dumps({"cmd": "pico_info", "pin": "-1", "idp": 1, "frm": "app"}).encode()

    def on_packet(packet: dict, addr: tuple) -> None:
        if packet.get("res") in (1, 99):
            ip = addr[0]
            with found_lock:
                if ip not in found:
                    found.append(ip)

    listener = SharedUDPListener.get(RECV_PORT)
    listener.register_raw(on_packet)

    send_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    send_sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    try:
        for subnet in subnets:
            broadcast = f"{subnet}.255"
            print(f"  Probing {broadcast}:{SEND_PORT} …")
            send_sock.sendto(probe, (broadcast, SEND_PORT))
            if subnet == "192.168.4":
                send_sock.sendto(probe, ("192.168.4.1", SEND_PORT))
    finally:
        send_sock.close()

    time.sleep(timeout)
    listener.unregister_raw(on_packet)
    return found


# ---------------------------------------------------------------------------
# Per-device test runner
# ---------------------------------------------------------------------------

def print_section(title: str) -> None:
    print(f"\n{'─' * 60}")
    print(f"  {title}")
    print(f"{'─' * 60}")


async def run_tests(ip: str, pin: str) -> DeviceResult:
    """
    Run info + state tests against one device.  Never raises; errors are
    captured in the returned DeviceResult.
    """
    result = DeviceResult(ip=ip)
    print_section(f"Connecting to {ip}  (PIN: {pin!r})")

    idp_file = Path(__file__).parent / ".tecno_idp.json"
    idp_mgr = IDPManager(backend="file", path=idp_file)

    try:
        with TecnoClient(ip=ip, timeout=15.0, idp_manager=idp_mgr) as client:
            pico = PicoDevice(client, pin=pin)

            # --- pico_info -----------------------------------------------
            print_section("pico_info  (no PIN required)")
            print("Sending pico_info …")
            info = await pico.get_info(timeout=15.0)
            if info is None:
                result.error = "pico_info timed out — check IP and network"
                print(f"✗ {result.error}")
                return result

            result.info = info
            print("✓ Response received")
            for key in ("ser", "fw_ver", "fw_note", "name", "has_slave"):
                if key in info:
                    print(f"  {key:12s} = {info[key]}")

            # --- stato_sync -----------------------------------------------
            print_section("stato_sync  (PIN required for full data)")
            print(f"Sending stato_sync with pin={pin!r} …")
            state = await pico.get_state(timeout=20.0)
            if state is None:
                if pin == "-1":
                    print("✗ No full state (expected without a real PIN).")
                else:
                    result.error = "stato_sync timed out — PIN may be wrong"
                    print(f"✗ {result.error}")
                return result

            result.state = state
            print("✓ Response received")
            display_keys = [
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
            for key, fmt in display_keys:
                if key in state:
                    print(f"  {key:16s} = {fmt(state[key])}")

            known = {k for k, _ in display_keys} | {"idp", "frm", "res", "cmd"}
            extra = {k: v for k, v in state.items() if k not in known}
            if extra:
                print("\n  Additional fields:")
                for k, v in extra.items():
                    print(f"  {k:16s} = {v}")

    except Exception as exc:
        result.error = str(exc)
        print(f"✗ Exception: {exc}")

    return result


# ---------------------------------------------------------------------------
# Summary printer
# ---------------------------------------------------------------------------

def print_summary(results: list[DeviceResult]) -> None:
    print_section("Summary")
    for r in results:
        if r.ok:
            name = r.info.get("name", "?") if r.info else "?"
            fw   = r.info.get("fw_ver", "?") if r.info else "?"
            print(f"  ✓  {r.ip:20s}  name={name}  fw={fw}")
        else:
            print(f"  ✗  {r.ip:20s}  {r.error or 'unknown error'}")


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

async def main() -> None:
    args = sys.argv[1:]

    # Parse optional --pin flag
    pin = "-1"
    if "--pin" in args:
        idx = args.index("--pin")
        if idx + 1 < len(args):
            pin = args[idx + 1]
            args = args[:idx] + args[idx + 2:]
        else:
            print("Error: --pin requires a value", file=sys.stderr)
            sys.exit(1)

    ips: list[str] = args  # remaining positional args are device IPs

    if not ips:
        print("No IP provided — scanning local network for Tecnosystemi devices …")
        print(f"(Subnets: {', '.join(COMMON_SUBNETS)})\n")
        ips = discover_devices(COMMON_SUBNETS)
        if ips:
            print(f"\n✓ Found {len(ips)} device(s): {', '.join(ips)}")
        else:
            print("\n✗ No devices found automatically.")
            print("  Possible reasons:")
            print("    • Device is on a different subnet")
            print("    • Your OS blocked the broadcast (try sudo)")
            print("    • Device uses the AP mode IP "
                  "(try: uv run python local_device_test.py 192.168.4.1)")
            sys.exit(1)

    results = [await run_tests(ip, pin) for ip in ips]

    print_summary(results)

    if not all(r.ok for r in results):
        sys.exit(1)


if __name__ == "__main__":
    asyncio.run(main())
