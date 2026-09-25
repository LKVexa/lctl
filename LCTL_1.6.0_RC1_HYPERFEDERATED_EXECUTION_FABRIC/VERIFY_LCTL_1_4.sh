#!/bin/sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
EX="$ROOT/examples/24_parallel_bell_pair_generation/program.lctl"
DENS="$ROOT/examples/09_density_matrix_noise/program.lctl"
TEL="$ROOT/examples/42_teleportation_across_logical_nodes/program.lctl"
echo "[1/6] internal 320-case invariant selftest"
java -jar "$ROOT/runtime/bin/lctl-mesh.jar" selftest14 dummy
echo "[2/6] partial-order / parallel discovery smoke"
java -jar "$ROOT/runtime/bin/lctl-mesh.jar" partial-order "$EX"
echo "[3/6] process-sharded density smoke"
java -jar "$ROOT/runtime/bin/lctl-mesh.jar" simulate-density2 "$DENS" 2
echo "[4/6] teleportation numerical equivalence smoke"
java -jar "$ROOT/runtime/bin/lctl-mesh.jar" protocol-run "$TEL" 314159
echo "[5/6] event replay smoke"
TRACE="${TMPDIR:-/tmp}/lctl14-verify.trace"
java -jar "$ROOT/runtime/bin/lctl-mesh.jar" run-task "$EX" 4 "$TRACE" >/dev/null
java -jar "$ROOT/runtime/bin/lctl-mesh.jar" reconstruct "$TRACE"
rm -f "$TRACE"
echo "[6/6] physical boundary fail-closed"
if java -jar "$ROOT/runtime/bin/lctl-mesh.jar" physical-distributed-run "$EX" >/dev/null 2>&1; then
  echo "FAIL: physical distributed execution unexpectedly succeeded" >&2
  exit 1
fi
echo "PASS: LCTL 1.4 smoke verification complete."
echo "For full suites run: python3 tools/run_lctl13_conformance.py and python3 tools/run_lctl14_conformance.py"
echo "The required 8-hour soak remains a separate long-duration qualification gate."
