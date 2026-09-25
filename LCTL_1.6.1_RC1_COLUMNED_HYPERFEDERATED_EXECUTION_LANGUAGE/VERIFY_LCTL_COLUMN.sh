#!/bin/sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
J="$ROOT/runtime/bin/lctl-hyperfederated.jar"
echo "[1/5] column compiler selftest"
java -jar "$J" column-selftest
echo "[2/5] Bell compact verify"
java -jar "$J" column-verify "$ROOT/examples_column/01_bell_state/program.lctlc"
echo "[3/5] Bell direct canonical verify through compact source"
java -jar "$J" verify "$ROOT/examples_column/01_bell_state/program.lctlc"
echo "[4/5] compact parallel-depth"
java -jar "$J" parallel-depth "$ROOT/examples_column/24_parallel_bell_pair_generation/program.lctlc" >/dev/null
echo "[5/5] 53-example campaign evidence"
grep -q '"passed": 53' "$ROOT/proof/COLUMN_LANGUAGE_EXAMPLE_CAMPAIGN.json"
grep -q '"failed": 0' "$ROOT/proof/COLUMN_LANGUAGE_EXAMPLE_CAMPAIGN.json"
echo "PASS: LCTL-C compact column language verification complete."
