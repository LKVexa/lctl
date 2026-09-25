#!/bin/sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
JAR="$ROOT/runtime/bin/lctl-federated.jar"
EX="$ROOT/examples/24_parallel_bell_pair_generation/program.lctl"
TOPO="$ROOT/topology/multi_route_logical_topology.json"
CFG="$ROOT/runtime/federation/reference_federation.json"
echo "[1/12] 1.5 512-case conformance"; java -jar "$JAR" federation-selftest
echo "[2/12] 1.4 320-case baseline"; java -jar "$JAR" baseline14-check "$ROOT"
echo "[3/12] federation plan"; java -jar "$JAR" federation-plan "$EX" "$TOPO"
echo "[4/12] native local federation"; java -jar "$JAR" run-federation "$EX" "$CFG"
echo "[5/12] actor/channel/pipeline"; java -jar "$JAR" run-actor 4 >/dev/null; java -jar "$JAR" run-channel 4 >/dev/null; java -jar "$JAR" run-pipeline 4 >/dev/null
echo "[6/12] CRDT"; java -jar "$JAR" run-crdt G-COUNTER 4
echo "[7/12] statevector equivalence"; java -jar "$JAR" simulate-statevector3 "$EX" 2
echo "[8/12] arbitrary-state remote CNOT"; java -jar "$JAR" remote-cnot-check 314159
echo "[9/12] checkpoint/restore"; CP="${TMPDIR:-/tmp}/lctl15-checkpoint.json"; java -jar "$JAR" checkpoint "$EX" "$CP" >/dev/null; java -jar "$JAR" restore "$CP"; rm -f "$CP"
echo "[10/12] adapter ABI mock"; java -jar "$JAR" adapter-check3 "$ROOT/adapters/mocks/abi3_reference.json"
echo "[11/12] physical evidence firewall"; OUT="$(java -jar "$JAR" adapter-check3 "$ROOT/adapters/mocks/abi3_physical_without_authority.json")"; echo "$OUT" | grep -q BLOCKED_EXTERNAL_AUTHORITY
echo "[12/12] preserved 1.3 command delegation"; java -jar "$JAR" ses "$ROOT/examples/01_bell_state/program.lctl" >/dev/null
echo "PASS: LCTL 1.5 RC1 smoke verification complete."
echo "Full 1.3 revalidation: python3 tools/run_lctl13_conformance.py"
echo "Full 1.5 descriptor suite: python3 tools/run_lctl15_conformance.py"
echo "8-hour soak remains NOT RUN in the packaged development candidate."
