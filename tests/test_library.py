"""
Unit tests for the library (no real device needed).

Approach: mock the UDPTransport to inject fake responses, then verify
that TecnoClient and PicoDevice behave correctly.
"""

import asyncio
import collections
import json
import threading
import time
from pathlib import Path
from typing import Optional
from unittest.mock import MagicMock, patch

import pytest

from tecnosystemi_unofficial import IDPManager, TecnoClient
from tecnosystemi_unofficial.devices import PicoDevice
from tecnosystemi_unofficial.idp import FileIDPStore, MemoryIDPStore
from tecnosystemi_unofficial.session import CONTROL_COMMANDS, RequestSession, RequestState
from tecnosystemi_unofficial.templates_loader import TemplateLoader


# ---------------------------------------------------------------------------
# IDPManager tests
# ---------------------------------------------------------------------------


class TestIDPManager:
    def test_starts_at_one(self):
        mgr = IDPManager()
        idp = mgr.acquire()
        assert idp == 1

    def test_increments(self):
        mgr = IDPManager()
        mgr.acquire()  # 1, not released
        mgr.release(1)
        idp2 = mgr.acquire()
        assert idp2 == 2

    def test_wraps_at_max(self):
        mgr = IDPManager()
        mgr._store.save(IDPManager.MAX_IDP)  # next candidate = 500
        idp = mgr.acquire()
        assert idp == IDPManager.MAX_IDP
        mgr.release(idp)
        idp2 = mgr.acquire()
        assert idp2 == 1  # wrapped

    def test_skips_in_flight_on_wrap(self):
        mgr = IDPManager()
        # Manually mark 500 as in-flight
        mgr._in_flight.add(IDPManager.MAX_IDP)
        mgr._store.save(IDPManager.MAX_IDP)
        idp = mgr.acquire()
        assert idp == 1  # skipped 500, landed on 1

    def test_thread_safety(self):
        mgr = IDPManager()
        results = []
        errors = []

        def worker():
            try:
                idp = mgr.acquire()
                time.sleep(0.01)
                results.append(idp)
                mgr.release(idp)
            except Exception as e:
                errors.append(e)

        threads = [threading.Thread(target=worker) for _ in range(20)]
        for t in threads:
            t.start()
        for t in threads:
            t.join()

        assert not errors
        assert len(set(results)) == len(results), "Duplicate IDPs allocated"

    def test_raises_when_all_in_flight(self):
        mgr = IDPManager()
        mgr._in_flight = set(range(1, IDPManager.MAX_IDP + 1))
        with pytest.raises(RuntimeError, match="in-flight"):
            mgr.acquire()


class TestFileIDPStore:
    def test_persists_and_reads(self, tmp_path):
        path = tmp_path / "idp.json"
        store = FileIDPStore(path)
        assert store.get() == 1  # default
        store.save(42)
        store2 = FileIDPStore(path)
        assert store2.get() == 42

    def test_handles_corrupt_file(self, tmp_path):
        path = tmp_path / "idp.json"
        path.write_text("not json")
        store = FileIDPStore(path)
        assert store.get() == 1

    def test_file_idp_manager(self, tmp_path):
        path = tmp_path / "idp.json"
        mgr = IDPManager(backend="file", path=path)
        idp = mgr.acquire()
        assert idp == 1
        mgr.release(idp)
        # Next session reads from file
        mgr2 = IDPManager(backend="file", path=path)
        idp2 = mgr2.acquire()
        assert idp2 == 2


# ---------------------------------------------------------------------------
# RequestSession tests
# ---------------------------------------------------------------------------


class TestRequestSession:
    def test_control_command_completes_on_ack(self):
        cmd = next(iter(CONTROL_COMMANDS))  # any control command
        session = RequestSession(idp=1, command=cmd)
        assert not session.expects_data

        complete = session.handle_packet({"idp": 1, "res": 99})
        assert complete is True
        assert session.state == RequestState.COMPLETED
        assert session.response is not None

    def test_info_command_waits_for_data_after_ack(self):
        session = RequestSession(idp=1, command="stato_sync")
        assert session.expects_data

        complete = session.handle_packet({"idp": 1, "res": 99})
        assert complete is False
        assert session.state == RequestState.WAITING_FINAL

        complete = session.handle_packet({"idp": 1, "res": 1, "on_off": 1})
        assert complete is True
        assert session.state == RequestState.COMPLETED
        assert session.response["on_off"] == 1

    def test_data_without_prior_ack_completes(self):
        """Device may skip the res:99 ACK in some edge cases."""
        session = RequestSession(idp=1, command="pico_info")
        complete = session.handle_packet({"idp": 1, "res": 1, "ser": "ABC"})
        assert complete is True
        assert session.state == RequestState.COMPLETED

    def test_all_packets_stored(self):
        session = RequestSession(idp=1, command="stato_sync")
        session.handle_packet({"idp": 1, "res": 99})
        session.handle_packet({"idp": 1, "res": 1, "speed": 3})
        assert len(session.all_packets) == 2


# ---------------------------------------------------------------------------
# TemplateLoader tests
# ---------------------------------------------------------------------------


class TestTemplateLoader:
    def test_list_bundled_templates(self):
        loader = TemplateLoader()
        templates = loader.list_templates()
        assert "pico/pico_info.json.j2" in templates
        assert "pico/stato_sync.json.j2" in templates
        assert "pico/upd_pico.json.j2" in templates
        assert "pico/check_pin.json.j2" in templates

    def test_render_pico_info(self):
        loader = TemplateLoader()
        rendered = loader.render("pico/pico_info.json.j2")
        payload = json.loads(rendered)
        assert payload["cmd"] == "pico_info"
        assert payload["pin"] == "-1"

    def test_render_stato_sync(self):
        loader = TemplateLoader()
        rendered = loader.render("pico/stato_sync.json.j2", pin="1234")
        payload = json.loads(rendered)
        assert payload["cmd"] == "stato_sync"
        assert payload["pin"] == "1234"

    def test_render_upd_pico_with_optional_fields(self):
        loader = TemplateLoader()
        rendered = loader.render("pico/upd_pico.json.j2", pin="1234", on_off=1, speed=3)
        payload = json.loads(rendered)
        assert payload["cmd"] == "upd_pico"
        assert payload["on_off"] == 1
        assert payload["speed"] == 3
        assert "mod" not in payload
        assert "s_umd" not in payload

    def test_render_upd_pico_all_fields(self):
        loader = TemplateLoader()
        rendered = loader.render(
            "pico/upd_pico.json.j2",
            pin="1234",
            on_off=1,
            mod=2,
            speed=3,
            spd_row=100,
            s_umd=50,
            led_on_off_breve=1,
            night_mod=0,
            m_crono=0,
            man_reset=[0, 1, 0],
        )
        payload = json.loads(rendered)
        assert payload["man_reset"] == [0, 1, 0]
        assert payload["spd_row"] == 100

    def test_render_upd_pico_minimal(self):
        loader = TemplateLoader()
        rendered = loader.render("pico/upd_pico.json.j2", pin="-1")
        payload = json.loads(rendered)
        assert set(payload.keys()) == {"cmd", "pin"}

    def test_custom_template_dir_takes_priority(self, tmp_path):
        custom = tmp_path / "pico"
        custom.mkdir()
        (custom / "pico_info.json.j2").write_text('{"cmd": "custom_pico_info", "pin": "-1"}')
        loader = TemplateLoader(extra_dirs=[tmp_path])
        rendered = loader.render("pico/pico_info.json.j2")
        payload = json.loads(rendered)
        assert payload["cmd"] == "custom_pico_info"


# ---------------------------------------------------------------------------
# TecnoClient + PicoDevice tests (transport mocked)
# ---------------------------------------------------------------------------


def _make_client_with_fake_transport(responses: dict[str, dict], *, loop: asyncio.AbstractEventLoop):
    """
    Create a TecnoClient whose transport is mocked.

    ``responses`` maps command names to the response dict the fake device
    should return.  For control commands, returns res:99.  For info commands,
    returns res:99 then the response.

    ``loop`` must be the running asyncio event loop (pass asyncio.get_running_loop()
    from the test).  Fake responses are scheduled as async tasks in that loop.
    """
    client = TecnoClient.__new__(TecnoClient)
    client.ip = "192.168.4.1"
    client.timeout = 2.0

    client.idp_manager = IDPManager()
    client.template_loader = TemplateLoader()
    client._sessions: dict = {}
    client._sessions_lock = threading.Lock()
    client._pending: dict = {}
    client._received: collections.deque = collections.deque(maxlen=TecnoClient.BUFFER_SIZE)

    send_calls = []

    def fake_send(data: bytes):
        payload = json.loads(data.decode())
        send_calls.append(payload)
        idp = payload.get("idp")
        cmd = payload.get("cmd", "")
        frm = payload.get("frm", "")

        if frm == "app" and idp is not None and "res" not in payload:
            resp = responses.get(cmd)
            if resp is not None:
                full_resp = {"idp": idp, "frm": "mst", **resp}

                async def _deliver():
                    await asyncio.sleep(0.02)
                    with client._sessions_lock:
                        session = client._sessions.get(idp)
                    if session and session.expects_data:
                        client._route_packet({"idp": idp, "frm": "mst", "res": 99})
                        await asyncio.sleep(0.01)
                    client._route_packet(full_resp)

                asyncio.run_coroutine_threadsafe(_deliver(), loop)

    mock_transport = MagicMock()
    mock_transport.send.side_effect = fake_send
    client.transport = mock_transport

    return client, send_calls


class TestTecnoClientWithMock:
    async def test_send_info_command(self):
        loop = asyncio.get_running_loop()
        client, _ = _make_client_with_fake_transport(
            {"pico_info": {"res": 1, "ser": "TS001", "fw_ver": "1.2.3"}},
            loop=loop,
        )
        result = await client.send_command({"cmd": "pico_info", "pin": "-1"})
        assert result is not None
        assert result["ser"] == "TS001"

    async def test_send_control_command(self):
        loop = asyncio.get_running_loop()
        client, _ = _make_client_with_fake_transport(
            {"upd_pico": {"res": 99}},
            loop=loop,
        )
        result = await client.send_command({"cmd": "upd_pico", "pin": "1234", "on_off": 1})
        assert result is not None
        assert result["res"] == 99

    async def test_timeout_returns_none(self):
        loop = asyncio.get_running_loop()
        client, _ = _make_client_with_fake_transport({}, loop=loop)
        result = await client.send_command({"cmd": "pico_info", "pin": "-1"}, timeout=0.1)
        assert result is None

    async def test_send_template(self):
        loop = asyncio.get_running_loop()
        client, sent = _make_client_with_fake_transport(
            {"upd_pico": {"res": 99}},
            loop=loop,
        )
        result = await client.send_template("pico/upd_pico.json.j2", pin="1234", on_off=1)
        assert result is not None
        cmd_sent = next(p for p in sent if p.get("cmd") == "upd_pico")
        assert cmd_sent["on_off"] == 1
        assert "idp" in cmd_sent
        assert cmd_sent["frm"] == "app"

    async def test_idp_increments_across_commands(self):
        loop = asyncio.get_running_loop()
        client, sent = _make_client_with_fake_transport(
            {"pico_info": {"res": 1, "ser": "X"}},
            loop=loop,
        )
        await client.send_command({"cmd": "pico_info", "pin": "-1"})
        await client.send_command({"cmd": "pico_info", "pin": "-1"})
        idps = [p["idp"] for p in sent if p.get("cmd") == "pico_info" and "res" not in p]
        assert idps[0] != idps[1]

    async def test_ack_sent_for_data_response(self):
        loop = asyncio.get_running_loop()
        client, sent = _make_client_with_fake_transport(
            {"pico_info": {"res": 1, "ser": "X"}},
            loop=loop,
        )
        await client.send_command({"cmd": "pico_info", "pin": "-1"})
        acks = [p for p in sent if p.get("res") == 99]
        assert len(acks) >= 1

    async def test_circular_buffer_stores_received_packets(self):
        loop = asyncio.get_running_loop()
        client, _ = _make_client_with_fake_transport(
            {"pico_info": {"res": 1, "ser": "X"}},
            loop=loop,
        )
        await client.send_command({"cmd": "pico_info", "pin": "-1"})
        recent = client.get_recent_packets()
        # Should have at least: res:99 ACK + res:1 data
        assert len(recent) >= 1
        assert all("ts" in e and "ip" in e and "idp" in e and "packet" in e for e in recent)

    async def test_circular_buffer_filter_by_idp(self):
        loop = asyncio.get_running_loop()
        client, _ = _make_client_with_fake_transport(
            {"pico_info": {"res": 1, "ser": "X"}},
            loop=loop,
        )
        result = await client.send_command({"cmd": "pico_info", "pin": "-1"})
        assert result is not None
        idp_used = result["idp"]
        filtered = client.get_recent_packets(idp=idp_used)
        assert all(e["idp"] == idp_used for e in filtered)


class TestPicoDevice:
    def _make_pico(self, responses=None, *, loop):
        responses = responses or {}
        client, sent = _make_client_with_fake_transport(responses, loop=loop)
        pico = PicoDevice(client, pin="9999")
        return pico, sent

    async def test_get_info(self):
        loop = asyncio.get_running_loop()
        pico, _ = self._make_pico({"pico_info": {"res": 1, "ser": "ABC", "name": "Pico1"}}, loop=loop)
        info = await pico.get_info()
        assert info["ser"] == "ABC"

    async def test_get_state(self):
        loop = asyncio.get_running_loop()
        pico, _ = self._make_pico(
            {"stato_sync": {"res": 1, "on_off": 1, "speed": 3, "AMB_tmpr": 22}},
            loop=loop,
        )
        state = await pico.get_state()
        assert state["speed"] == 3
        assert state["AMB_tmpr"] == 22

    async def test_turn_on(self):
        loop = asyncio.get_running_loop()
        pico, sent = self._make_pico({"upd_pico": {"res": 99}}, loop=loop)
        assert await pico.turn_on() is True
        cmd = next(p for p in sent if p.get("cmd") == "upd_pico")
        assert cmd["on_off"] == 1
        assert cmd["pin"] == "9999"

    async def test_turn_off(self):
        loop = asyncio.get_running_loop()
        pico, sent = self._make_pico({"upd_pico": {"res": 99}}, loop=loop)
        assert await pico.turn_off() is True
        cmd = next(p for p in sent if p.get("cmd") == "upd_pico")
        assert cmd["on_off"] == 2

    async def test_set_speed(self):
        loop = asyncio.get_running_loop()
        pico, sent = self._make_pico({"upd_pico": {"res": 99}}, loop=loop)
        assert await pico.set_speed(3, speed_raw=100) is True
        cmd = next(p for p in sent if p.get("cmd") == "upd_pico")
        assert cmd["speed"] == 3
        assert cmd["spd_row"] == 100

    async def test_set_mode(self):
        loop = asyncio.get_running_loop()
        pico, sent = self._make_pico({"upd_pico": {"res": 99}}, loop=loop)
        assert await pico.set_mode(2, on_off=1) is True
        cmd = next(p for p in sent if p.get("cmd") == "upd_pico")
        assert cmd["mod"] == 2
        assert cmd["on_off"] == 1

    async def test_set_humidity(self):
        loop = asyncio.get_running_loop()
        pico, sent = self._make_pico({"upd_pico": {"res": 99}}, loop=loop)
        await pico.set_humidity(60)
        cmd = next(p for p in sent if p.get("cmd") == "upd_pico")
        assert cmd["s_umd"] == 60

    async def test_set_night_mode(self):
        loop = asyncio.get_running_loop()
        pico, sent = self._make_pico({"upd_pico": {"res": 99}}, loop=loop)
        await pico.set_night_mode(True)
        cmd = next(p for p in sent if p.get("cmd") == "upd_pico")
        assert cmd["night_mod"] == 1

    async def test_check_pin_valid(self):
        loop = asyncio.get_running_loop()
        pico, _ = self._make_pico({"check_pin": {"res": 1}}, loop=loop)
        assert await pico.check_pin() is True

    async def test_check_pin_invalid(self):
        loop = asyncio.get_running_loop()
        pico, _ = self._make_pico({"check_pin": {"res": 0}}, loop=loop)
        assert await pico.check_pin() is False

    async def test_update_multiple_fields(self):
        loop = asyncio.get_running_loop()
        pico, sent = self._make_pico({"upd_pico": {"res": 99}}, loop=loop)
        await pico.update(on_off=1, speed=5, mod=3)
        cmd = next(p for p in sent if p.get("cmd") == "upd_pico")
        assert cmd["on_off"] == 1
        assert cmd["speed"] == 5
        assert cmd["mod"] == 3

    async def test_turn_on_returns_false_on_timeout(self):
        loop = asyncio.get_running_loop()
        pico, _ = self._make_pico({}, loop=loop)
        result = await pico.turn_on(timeout=0.1)
        assert result is False

    async def test_send_template_injects_pin(self):
        loop = asyncio.get_running_loop()
        pico, sent = self._make_pico({"upd_pico": {"res": 99}}, loop=loop)
        await pico.send_template("pico/upd_pico.json.j2", on_off=1)
        cmd = next(p for p in sent if p.get("cmd") == "upd_pico")
        assert cmd["pin"] == "9999"


# ---------------------------------------------------------------------------
# SharedUDPListener + UDPTransport integration tests
# ---------------------------------------------------------------------------


import socket as _socket
from tecnosystemi_unofficial.shared_listener import SharedUDPListener
from tecnosystemi_unofficial.transport import UDPTransport

# Use a high-numbered port that won't conflict with the real 40069.
_TEST_RECV_PORT = 49069
_TEST_SEND_PORT = 49070


def _send_udp(data: bytes, dest_ip: str, dest_port: int, src_port: int = 0) -> None:
    """Send a UDP datagram from an ephemeral or fixed source port."""
    sock = _socket.socket(_socket.AF_INET, _socket.SOCK_DGRAM)
    try:
        if src_port:
            sock.bind(("127.0.0.1", src_port))
        sock.sendto(data, (dest_ip, dest_port))
    finally:
        sock.close()


class TestSharedUDPListenerRouting:
    """Tests that exercise the real SharedUDPListener socket on loopback."""

    def setup_method(self):
        # Isolate each test: evict any cached singleton for the test port.
        with SharedUDPListener._instances_lock:
            SharedUDPListener._instances.pop(_TEST_RECV_PORT, None)

    def teardown_method(self):
        # Force-close any lingering socket after each test.
        with SharedUDPListener._instances_lock:
            inst = SharedUDPListener._instances.pop(_TEST_RECV_PORT, None)
        if inst is not None:
            with inst._lock:
                if inst._sock is not None:
                    try:
                        inst._sock.close()
                    except Exception:
                        pass
                    inst._sock = None

    def test_packet_reaches_registered_handler(self):
        received = []
        listener = SharedUDPListener.get(_TEST_RECV_PORT)
        listener.register("127.0.0.1", received.append)
        try:
            _send_udp(b'{"cmd":"ping"}', "127.0.0.1", _TEST_RECV_PORT)
            deadline = time.monotonic() + 2.0
            while not received and time.monotonic() < deadline:
                time.sleep(0.05)
            assert received == [{"cmd": "ping"}]
        finally:
            listener.unregister("127.0.0.1", received.append)

    def test_handler_for_other_ip_not_called(self):
        """Packets from 127.0.0.1 must not reach a handler registered for a different IP."""
        received_other = []
        listener = SharedUDPListener.get(_TEST_RECV_PORT)
        listener.register("10.0.0.1", received_other.append)  # will never match loopback
        try:
            _send_udp(b'{"cmd":"ping"}', "127.0.0.1", _TEST_RECV_PORT)
            time.sleep(0.2)  # give the thread time to dispatch if it were going to
            assert received_other == []
        finally:
            listener.unregister("10.0.0.1", received_other.append)

    def test_socket_closes_after_last_unregister(self):
        listener = SharedUDPListener.get(_TEST_RECV_PORT)
        handler = lambda p: None
        listener.register("127.0.0.1", handler)
        assert listener._sock is not None
        listener.unregister("127.0.0.1", handler)
        time.sleep(0.05)
        assert listener._sock is None

    def test_socket_reopens_after_reregister(self):
        listener = SharedUDPListener.get(_TEST_RECV_PORT)
        h = lambda p: None
        listener.register("127.0.0.1", h)
        listener.unregister("127.0.0.1", h)
        time.sleep(0.05)
        assert listener._sock is None
        # Re-register — socket should reopen
        listener.register("127.0.0.1", h)
        assert listener._sock is not None
        listener.unregister("127.0.0.1", h)

    def test_two_handlers_same_ip_both_receive(self):
        r1, r2 = [], []
        listener = SharedUDPListener.get(_TEST_RECV_PORT)
        listener.register("127.0.0.1", r1.append)
        listener.register("127.0.0.1", r2.append)
        try:
            _send_udp(b'{"val":1}', "127.0.0.1", _TEST_RECV_PORT)
            deadline = time.monotonic() + 2.0
            while (not r1 or not r2) and time.monotonic() < deadline:
                time.sleep(0.05)
            assert r1 == [{"val": 1}]
            assert r2 == [{"val": 1}]
        finally:
            listener.unregister("127.0.0.1", r1.append)
            listener.unregister("127.0.0.1", r2.append)


class TestUDPTransportShared:
    """Tests that UDPTransport instances share the listener without conflicts."""

    def setup_method(self):
        with SharedUDPListener._instances_lock:
            SharedUDPListener._instances.pop(_TEST_RECV_PORT, None)

    def teardown_method(self):
        with SharedUDPListener._instances_lock:
            inst = SharedUDPListener._instances.pop(_TEST_RECV_PORT, None)
        if inst is not None:
            with inst._lock:
                if inst._sock is not None:
                    try:
                        inst._sock.close()
                    except Exception:
                        pass
                    inst._sock = None

    def test_two_transports_start_without_conflict(self):
        t1 = UDPTransport("192.168.1.10", _TEST_SEND_PORT, _TEST_RECV_PORT)
        t2 = UDPTransport("192.168.1.11", _TEST_SEND_PORT, _TEST_RECV_PORT)
        t1.start()
        t2.start()  # must not raise "address already in use"
        t1.stop()
        t2.stop()

    def test_packets_routed_to_correct_transport(self):
        """
        Packets arriving from 127.0.0.1 should only be dispatched to the
        transport registered for that IP, not to others.
        """
        received_loopback = []
        received_other = []

        t_loopback = UDPTransport("127.0.0.1", _TEST_SEND_PORT, _TEST_RECV_PORT)
        t_loopback.add_packet_handler(received_loopback.append)

        t_other = UDPTransport("10.0.0.99", _TEST_SEND_PORT, _TEST_RECV_PORT)
        t_other.add_packet_handler(received_other.append)

        t_loopback.start()
        t_other.start()
        try:
            _send_udp(b'{"cmd":"hello"}', "127.0.0.1", _TEST_RECV_PORT)
            deadline = time.monotonic() + 2.0
            while not received_loopback and time.monotonic() < deadline:
                time.sleep(0.05)
            assert received_loopback == [{"cmd": "hello"}]
            assert received_other == []
        finally:
            t_loopback.stop()
            t_other.stop()

    def test_stop_does_not_affect_other_transport(self):
        """Stopping one transport must not close the shared socket used by another."""
        received = []
        t1 = UDPTransport("10.0.0.1", _TEST_SEND_PORT, _TEST_RECV_PORT)
        t2 = UDPTransport("127.0.0.1", _TEST_SEND_PORT, _TEST_RECV_PORT)
        t2.add_packet_handler(received.append)
        t1.start()
        t2.start()
        t1.stop()  # t2 must still work
        _send_udp(b'{"alive":1}', "127.0.0.1", _TEST_RECV_PORT)
        deadline = time.monotonic() + 2.0
        while not received and time.monotonic() < deadline:
            time.sleep(0.05)
        assert received == [{"alive": 1}]
        t2.stop()
