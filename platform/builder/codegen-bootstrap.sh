#!/usr/bin/env bash
set -euo pipefail
# Legacy alias. Generation, dependency staging and Java SDO are orchestrated
# together by the canonical bootstrap; never call generate.sh directly.
locRepository="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)"
exec bash "$locRepository/devops/bootstrap/bootstrap.sh" \
    --root platform/builder/core/intf --prepare-only "$@"
