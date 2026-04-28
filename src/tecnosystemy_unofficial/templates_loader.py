"""
Jinja2-based command template loader.

Templates live under ``src/tecnosystemy_unofficial/templates/`` and produce
JSON strings when rendered. Users can supply additional template directories
that take priority over the bundled ones, making it easy to define custom
commands without writing Python.

Template contract
-----------------
Each template must produce a valid JSON object.  The ``idp`` and ``frm``
fields are injected automatically by the client, so templates should *not*
include them.  Example::

    {# templates/pico/pico_info.json.j2 #}
    {
      "cmd": "pico_info",
      "pin": "-1"
    }

Optional fields can be added conditionally::

    {%- if speed is defined and speed is not none %}, "speed": {{ speed }}{% endif %}
"""

from pathlib import Path
from typing import Optional

import jinja2


_BUNDLED_TEMPLATES = Path(__file__).parent / "templates"


class TemplateLoader:
    """
    Loads and renders Jinja2 command templates.

    Args:
        extra_dirs: Additional template directories searched before the bundled
                    templates. Useful for custom or device-specific commands.
    """

    def __init__(self, extra_dirs: Optional[list[Path]] = None):
        loaders: list[jinja2.BaseLoader] = []
        if extra_dirs:
            for d in extra_dirs:
                loaders.append(jinja2.FileSystemLoader(str(d)))
        loaders.append(jinja2.FileSystemLoader(str(_BUNDLED_TEMPLATES)))

        self._env = jinja2.Environment(
            loader=jinja2.ChoiceLoader(loaders),
            undefined=jinja2.Undefined,
            trim_blocks=True,
            lstrip_blocks=True,
        )

    def render(self, name: str, **context) -> str:
        """
        Render *name* with *context* and return the resulting JSON string.

        Args:
            name:    Template path relative to any template directory,
                     e.g. ``"pico/upd_pico.json.j2"``.
            context: Variables passed to the template.

        Returns:
            A JSON string ready to be parsed or sent over the wire.
        """
        template = self._env.get_template(name)
        return template.render(**context).strip()

    def list_templates(self) -> list[str]:
        """Return all available template names."""
        return self._env.list_templates()
