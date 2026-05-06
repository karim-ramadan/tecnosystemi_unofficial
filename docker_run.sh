#!/usr/bin/env bash
# Build and run the tecnosystemi-unofficial Docker image.
#
# Usage:
#   ./docker_run.sh                              # shell only (no tecno)
#   ./docker_run.sh 192.168.1.50                 # register device, open tecno REPL
#   ./docker_run.sh 192.168.1.50 1234            # register device + PIN, open tecno REPL
#   TECNO_CMD="state" ./docker_run.sh 192.168.1.50 1234   # one-shot command, then exit
#
# Flags (can appear anywhere):
#   --build      Force rebuild of the Docker image.
#   --host-net   Use --network=host. Only effective on Linux Docker; on macOS
#                Docker Desktop the container still runs inside a Linux VM.

set -euo pipefail

IMAGE="tecnosystemi-unofficial"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
TECNO_CMD="${TECNO_CMD:-}"
HOST_NET=0
FORCE_BUILD=0

REMAINING_ARGS=()
for arg in "$@"; do
    case "$arg" in
        --host-net) HOST_NET=1 ;;
        --build)    FORCE_BUILD=1 ;;
        *)          REMAINING_ARGS+=("$arg") ;;
    esac
done

DEVICE_IP="${REMAINING_ARGS[0]:-}"
DEVICE_PIN="${REMAINING_ARGS[1]:-}"

# Build if the image doesn't exist yet or --build was passed.
if [[ "$FORCE_BUILD" -eq 1 ]] || ! docker image inspect "$IMAGE" &>/dev/null; then
    echo "Building image ${IMAGE}…"
    docker build -t "$IMAGE" "$PROJECT_DIR"
    echo
fi

DOCKER_ARGS=(
    --rm
    -it
    -v "${PROJECT_DIR}:/app"
    -w /app
)

if [[ "$HOST_NET" -eq 1 ]]; then
    DOCKER_ARGS+=(--network host)
    echo "WARNING: --network=host only works on native Linux Docker."
    echo "         On macOS Docker Desktop the container lives in a VM — LAN devices"
    echo "         are still unreachable this way. Use port mapping instead."
    echo
else
    DOCKER_ARGS+=(-p 40069:40069/udp)
fi

echo "Starting container ${IMAGE}…"
[[ "$HOST_NET" -eq 1 ]] \
    && echo "  Network : host" \
    || echo "  Network : bridge  (40069/udp → container)"
[[ -n "$DEVICE_IP" ]]  && echo "  Device  : ${DEVICE_IP}"
[[ -n "$DEVICE_PIN" ]] && echo "  PIN     : ${DEVICE_PIN}"
[[ -n "$TECNO_CMD" ]]  && echo "  Command : ${TECNO_CMD}"
echo

if [[ -n "$DEVICE_IP" && -n "$DEVICE_PIN" && -n "$TECNO_CMD" ]]; then
    docker run "${DOCKER_ARGS[@]}" "$IMAGE" \
        bash -c "tecno register ${DEVICE_IP} ${DEVICE_PIN} && tecno --ip ${DEVICE_IP} --pin ${DEVICE_PIN} ${TECNO_CMD}"

elif [[ -n "$DEVICE_IP" && -n "$DEVICE_PIN" ]]; then
    docker run "${DOCKER_ARGS[@]}" "$IMAGE" \
        bash -c "tecno register ${DEVICE_IP} ${DEVICE_PIN} && tecno"

elif [[ -n "$DEVICE_IP" ]]; then
    docker run "${DOCKER_ARGS[@]}" "$IMAGE" \
        bash -c "tecno register ${DEVICE_IP} && tecno"

else
    # No device — drop into a plain shell with networking tools available.
    docker run "${DOCKER_ARGS[@]}" "$IMAGE" bash
fi
