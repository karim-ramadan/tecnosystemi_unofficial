"""
ANSI color helpers for the Tecnosystemi CLI.

Colors are automatically disabled when:
- ``NO_COLOR`` environment variable is set (https://no-color.org/)
- ``sys.stdout`` is not a TTY

Usage::

    from ._colors import C
    print(C.green("✓ OK"))
    print(C.red("✗ Error"))
    print(C.cyan("key") + " = " + C.bold("value"))
"""

from __future__ import annotations

import os
import sys


class Colors:
    """ANSI color wrapper.  All methods are no-ops when ``enabled`` is False."""

    enabled: bool = (
        sys.stdout.isatty()
        and not os.environ.get("NO_COLOR")
    )

    # ANSI escape codes
    _RESET  = "\033[0m"
    _GREEN  = "\033[92m"
    _RED    = "\033[91m"
    _YELLOW = "\033[93m"
    _CYAN   = "\033[96m"
    _BOLD   = "\033[1m"
    _DIM    = "\033[2m"
    _BLUE   = "\033[94m"

    # readline-invisible wrappers (prevent prompt width miscalculation)
    _RL_START = "\001"
    _RL_END   = "\002"

    @classmethod
    def _wrap(cls, code: str, s: str) -> str:
        if not cls.enabled:
            return s
        return f"{code}{s}{cls._RESET}"

    @classmethod
    def green(cls, s: str) -> str:
        return cls._wrap(cls._GREEN, s)

    @classmethod
    def red(cls, s: str) -> str:
        return cls._wrap(cls._RED, s)

    @classmethod
    def yellow(cls, s: str) -> str:
        return cls._wrap(cls._YELLOW, s)

    @classmethod
    def cyan(cls, s: str) -> str:
        return cls._wrap(cls._CYAN, s)

    @classmethod
    def bold(cls, s: str) -> str:
        return cls._wrap(cls._BOLD, s)

    @classmethod
    def dim(cls, s: str) -> str:
        return cls._wrap(cls._DIM, s)

    @classmethod
    def blue(cls, s: str) -> str:
        return cls._wrap(cls._BLUE, s)

    @classmethod
    def prompt(cls, s: str) -> str:
        """Wrap *s* for use in a readline prompt (invisible-sequence wrappers)."""
        if not cls.enabled:
            return s
        return f"{cls._RL_START}{cls._CYAN}{cls._RL_END}{s}{cls._RL_START}{cls._RESET}{cls._RL_END}"


C = Colors
