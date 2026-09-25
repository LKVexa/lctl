#!/bin/sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
"$ROOT/VERIFY_LCTL_1_6.sh"
"$ROOT/VERIFY_LCTL_COLUMN.sh"
echo "PASS: LCTL 1.6.1 columned hyperfederated verification complete."
