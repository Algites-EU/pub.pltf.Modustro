#!/usr/bin/env bash
set -euo pipefail
locRepo="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)"
locBootstrap="$locRepo/devops/bootstrap"
export MODUSTRO_BOOTSTRAP_REPOSITORY="$locRepo"
export MODUSTRO_BOOTSTRAP_ROOT="$locRepo/platform/builder/core/intf"
"${ALGITES_PYTHON_EXECUTABLE:-python3}" "$locBootstrap/clean_generated.py" clean \
    --repository "$locRepo" --root "$MODUSTRO_BOOTSTRAP_ROOT"
exec bash "$locBootstrap/generate.sh" "$@"
