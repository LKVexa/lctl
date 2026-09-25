#!/bin/sh
DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
exec java -jar "$DIR/lctl-fabric.jar" "$@"
