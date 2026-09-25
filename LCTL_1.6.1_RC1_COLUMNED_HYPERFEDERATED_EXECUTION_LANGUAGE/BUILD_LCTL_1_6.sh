#!/bin/sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
rm -rf "$ROOT/runtime/classes16"
mkdir -p "$ROOT/runtime/classes16"
javac -encoding UTF-8 -d "$ROOT/runtime/classes16" "$ROOT/runtime/src/"*.java
jar --create --file "$ROOT/runtime/bin/lctl-hyperfederated.jar" --main-class LctlHyperFederatedMain -C "$ROOT/runtime/classes16" .
echo "PASS: built runtime/bin/lctl-hyperfederated.jar"
