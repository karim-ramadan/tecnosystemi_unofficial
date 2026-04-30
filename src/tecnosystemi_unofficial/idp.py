"""
IDP (Incremental Data Packet) ID management.

Every UDP command to a Tecnosystemi device carries an `idp` field that acts as
a request correlation ID. The device echoes it back in the response.

Rules from the firmware:
- Starts at 1, increments per request
- Wraps back to 1 after MAX_IDP (500)
- Two backends: memory (default, starts fresh each process) or file (persists across restarts)
"""

import json
from abc import ABC, abstractmethod
from pathlib import Path
from typing import Optional


class IDPStore(ABC):
    @abstractmethod
    def get(self) -> int: ...

    @abstractmethod
    def save(self, value: int): ...


class MemoryIDPStore(IDPStore):
    def __init__(self, start: int = 1):
        self._value = start

    def get(self) -> int:
        return self._value

    def save(self, value: int):
        self._value = value


class FileIDPStore(IDPStore):
    """Persists the next IDP to a JSON file so it survives process restarts."""

    def __init__(self, path: Path):
        self._path = Path(path)
        self._path.parent.mkdir(parents=True, exist_ok=True)

    def get(self) -> int:
        try:
            return json.loads(self._path.read_text()).get("idp", 1)
        except (FileNotFoundError, json.JSONDecodeError, KeyError, ValueError):
            return 1

    def save(self, value: int):
        self._path.write_text(json.dumps({"idp": value}))


class IDPManager:
    """
    IDP allocator with in-flight tracking.

    Each device should have its own IDPManager instance.  All calls happen on
    the asyncio event loop thread, so no locking is needed.
    """

    MAX_IDP = 500

    def __init__(self, backend: str = "memory", path: Optional[Path] = None):
        """
        Args:
            backend: "memory" (default) or "file" for persistent storage.
            path:    Required when backend="file". Path to the state file.
        """
        self._in_flight: set[int] = set()
        if backend == "file":
            if path is None:
                raise ValueError("path is required for the 'file' backend")
            self._store: IDPStore = FileIDPStore(path)
        else:
            self._store = MemoryIDPStore()

    def acquire(self) -> int:
        """
        Reserve the next available IDP and mark it in-flight.

        Raises RuntimeError if all 500 slots are currently in use.
        """
        candidate = self._store.get()
        for _ in range(self.MAX_IDP):
            if candidate not in self._in_flight:
                break
            candidate = (candidate % self.MAX_IDP) + 1
        else:
            raise RuntimeError(
                "All IDP slots are in-flight. Cannot send a new command."
            )
        self._in_flight.add(candidate)
        self._store.save((candidate % self.MAX_IDP) + 1)
        return candidate

    def release(self, idp: int):
        """Mark an IDP as no longer in-flight."""
        self._in_flight.discard(idp)

    @property
    def in_flight_count(self) -> int:
        return len(self._in_flight)
