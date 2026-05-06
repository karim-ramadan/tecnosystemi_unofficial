#!/usr/bin/env bash
# Run the tecnosystemi-unofficial CLI in a Docker container.
#
# Usage:
#   ./docker_test.sh                        # interactive REPL
#   ./docker_test.sh 192.168.1.50           # register device, then open REPL
#   ./docker_test.sh 192.168.1.50 1234      # register device + PIN, then open REPL
#   TECNO_CMD="state" ./docker_test.sh 192.168.1.50 1234   # run one command and exit

set -euo pipefail

DEVICE_IP="${1:-}"
DEVICE_PIN="${2:-}"
TECNO_CMD="${TECNO_CMD:-}"
IMAGE="python:3.12-slim"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Port 40069 receives responses from the device (device always replies on this port).
# Outgoing traffic to device:40070 is handled automatically by Docker NAT.
# Note: on macOS Docker Desktop the container runs inside a Linux VM, so
# broadcast-based discovery won't reach your LAN. Use register <IP> instead.

DOCKER_ARGS=(
    --rm
    -it
    -p 40069:40069/udp
    -v "${PROJECT_DIR}:/app"
    -w /app
)

# Build the shell command that runs inside the container.
if [[ -n "$DEVICE_IP" && -n "$DEVICE_PIN" && -n "$TECNO_CMD" ]]; then
    # One-shot non-interactive command.
    INNER="pip install -e . -q && tecno register ${DEVICE_IP} ${DEVICE_PIN} && tecno --ip ${DEVICE_IP} --pin ${DEVICE_PIN} ${TECNO_CMD}"
elif [[ -n "$DEVICE_IP" && -n "$DEVICE_PIN" ]]; then
    # Register device + PIN, then open interactive REPL.
    INNER="pip install -e . -q && tecno register ${DEVICE_IP} ${DEVICE_PIN} && tecno"
elif [[ -n "$DEVICE_IP" ]]; then
    # Register device (no PIN), then open interactive REPL.
    INNER="pip install -e . -q && tecno register ${DEVICE_IP} && tecno"
else
    # No device specified — open REPL (use 'register <IP>' inside).
    INNER="pip install -e . -q && tecno"
fi

echo "Starting container (port 40069/udp mapped)…"
echo "  Image   : ${IMAGE}"
[[ -n "$DEVICE_IP" ]]  && echo "  Device  : ${DEVICE_IP}"
[[ -n "$DEVICE_PIN" ]] && echo "  PIN     : ${DEVICE_PIN}"
[[ -n "$TECNO_CMD" ]]  && echo "  Command : ${TECNO_CMD}"
echo

docker run "${DOCKER_ARGS[@]}" "${IMAGE}" bash -c "${INNER}"
