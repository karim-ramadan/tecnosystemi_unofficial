"""
IDP (Incremental Data Packet) ID management.

Every UDP command to a Tecnosystemi device carries an `idp` field that acts as
a request correlation ID. The device echoes it back in the response.

Rules from the firmware:
- Starts at 1, increments per request
- Wraps back to 1 after MAX_IDP (500)
- Two backends: memory (default, starts fresh each process) or file (persists across restarts)
"""

import asyncio
import json
from abc import ABC, abstractmethod
from pathlib import Path
from typing import Optional


class IDPStore(ABC):
    @abstractmethod
    async def get(self) -> int: ...

    @abstractmethod
    async def save(self, value: int) -> None: ...


class MemoryIDPStore(IDPStore):
    def __init__(self, start: int = 100):
        self._value = start

    async def get(self) -> int:
        return self._value

    async def save(self, value: int) -> None:
        self._value = value


class FileIDPStore(IDPStore):
    """Persists the next IDP to a JSON file so it survives process restarts.

    All file I/O runs in a thread-pool executor so it never blocks the event loop.
    Directory creation is deferred to the first write.
    """

    def __init__(self, path: Path, start: int = 100):
        self._path = Path(path)
        self._start: int = start

    def _read(self) -> int:
        try:
            return json.loads(self._path.read_text()).get("idp", self._start)
        except (FileNotFoundError, json.JSONDecodeError, KeyError, ValueError):
            return self._start

    def _write(self, value: int) -> None:
        self._path.parent.mkdir(parents=True, exist_ok=True)
        self._path.write_text(json.dumps({"idp": value}))

    async def get(self) -> int:
        return await asyncio.to_thread(self._read)

    async def save(self, value: int) -> None:
        await asyncio.to_thread(self._write, value)


class IDPManager:
    """
    IDP allocator with in-flight tracking.

    Each device should have its own IDPManager instance.  ``acquire`` is
    protected by an asyncio lock so concurrent coroutines cannot race on
    the store read/write sequence.
    """

    MAX_IDP = 500
    MIN_IDP = 100 #Low idps are more likely to be used by other apps, so we start higher to reduce collision chances.

    def __init__(self, backend: str = "memory", path: Optional[Path] = None):
        """
        Args:
            backend: "memory" (default) or "file" for persistent storage.
            path:    Required when backend="file". Path to the state file.
        """
        self._in_flight: set[int] = set()
        self._lock = asyncio.Lock()
        if backend == "file":
            if path is None:
                raise ValueError("path is required for the 'file' backend")
            self._store: IDPStore = FileIDPStore(path, self.MIN_IDP)
        else:
            self._store = MemoryIDPStore(self.MIN_IDP)

    async def acquire(self) -> int:
        """
        Reserve the next available IDP and mark it in-flight.

        Raises RuntimeError if all 500 slots are currently in use.
        """
        async with self._lock:
            candidate = await self._store.get()
            _range = self.MAX_IDP - self.MIN_IDP + 1
            for _ in range(_range):
                if candidate not in self._in_flight:
                    break
                candidate = self.MIN_IDP + (candidate - self.MIN_IDP + 1) % _range
            else:
                raise RuntimeError(
                    "All IDP slots are in-flight. Cannot send a new command."
                )
            self._in_flight.add(candidate)
            try:
                next_candidate = self.MIN_IDP + (candidate - self.MIN_IDP + 1) % _range
                await self._store.save(next_candidate)
            except Exception:
                self._in_flight.discard(candidate)
                raise
            return candidate

    def release(self, idp: int):
        """Mark an IDP as no longer in-flight."""
        self._in_flight.discard(idp)

    @property
    def in_flight_count(self) -> int:
        return len(self._in_flight)
