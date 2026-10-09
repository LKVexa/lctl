#!/bin/sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
rm -rf "$ROOT/runtime/classes"
mkdir -p "$ROOT/runtime/classes"
javac -encoding UTF-8 -d "$ROOT/runtime/classes" "$ROOT/runtime/src/"*.java
jar --create --file "$ROOT/runtime/bin/lctl-federated.jar" --main-class LctlFederatedMain -C "$ROOT/runtime/classes" .
echo "PASS: built runtime/bin/lctl-federated.jar"
