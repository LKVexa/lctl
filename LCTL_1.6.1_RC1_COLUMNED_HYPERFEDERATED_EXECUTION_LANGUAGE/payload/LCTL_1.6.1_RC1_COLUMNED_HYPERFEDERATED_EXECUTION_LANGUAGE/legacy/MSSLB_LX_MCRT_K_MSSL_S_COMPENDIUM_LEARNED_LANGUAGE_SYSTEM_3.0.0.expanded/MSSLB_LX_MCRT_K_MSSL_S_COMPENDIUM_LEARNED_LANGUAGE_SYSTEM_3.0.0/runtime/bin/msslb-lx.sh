#!/usr/bin/env sh
HERE="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
exec java -jar "$HERE/msslb-lx-runtime.jar" "$@"
