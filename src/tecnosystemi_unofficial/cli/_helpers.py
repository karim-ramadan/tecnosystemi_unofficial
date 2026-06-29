"""Shared utility functions and constants for the Tecnosystemi CLI."""
from __future__ import annotations

import asyncio
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
from ._session import SessionState

DEVICE_TYPES = ("pico", "polaris5x")

_SEND_PORT, _RECV_PORT = 40070, 40069
_SUBNETS = ["192.168.1", "192.168.0", "192.168.4"]
_DEBUG_TAG = "_tecno_cli_debug"


def discover(timeout: float = 2.0) -> list[str]:
    """Broadcast a pico_info probe and collect responding device IPs."""
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
    """Connect to *ip*, optionally validate *pin*, and persist both to *session*."""
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
            if not asyncio.run(pico.check_pin(timeout=8.0)):
                print(f"  {C.red('✗')} PIN rejected by device.")
                return False
            session.set_pin(ip, pin)
            print(f"  {C.green('✓')} PIN accepted and saved for {ip}")
        session.ip = ip
        session.save()
        no_pin_hint = f"  {C.dim('(no PIN — run: tecno --ip ' + ip + ' pin <value>)')}"
        print(f"  {C.green('✓')} Registered {C.bold(ip)}" + ("" if pin else no_pin_hint))
        return True
    finally:
        client.stop()


def enable_debug() -> Optional[logging.Handler]:
    """Attach a DEBUG-level stderr handler to the library logger."""
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
    """Remove a previously-attached debug handler."""
    lib_logger = logging.getLogger("tecnosystemi_unofficial")
    lib_logger.removeHandler(handler)
    if not lib_logger.handlers:
        lib_logger.setLevel(logging.WARNING)


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
