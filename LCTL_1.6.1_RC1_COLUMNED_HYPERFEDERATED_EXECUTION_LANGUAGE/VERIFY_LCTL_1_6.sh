#!/bin/sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
JAR="$ROOT/runtime/bin/lctl-hyperfederated.jar"
EX="$ROOT/examples/24_parallel_bell_pair_generation/program.lctl"
TOPO="$ROOT/topology/multi_route_logical_topology.json"
CFG="$ROOT/runtime/federation/reference_federation.json"
echo "[1/16] build"; "$ROOT/BUILD_LCTL_1_6.sh"
echo "[2/16] 1.6 1024-case selftest"; java -jar "$JAR" fabric-selftest
echo "[3/16] preserved 1.5 512-case selftest"; java -jar "$JAR" federation-selftest
echo "[4/16] preserved 1.4 320-case baseline"; java -jar "$JAR" baseline14-check "$ROOT"
echo "[5/16] topology discovery"; java -jar "$JAR" fabric-topology
echo "[6/16] measured cost calibration"; java -jar "$JAR" fabric-calibrate
echo "[7/16] parallel depth / grain"; java -jar "$JAR" parallel-depth "$EX" >/dev/null; java -jar "$JAR" grain-plan "$EX"
echo "[8/16] recursive partition / placement"; java -jar "$JAR" hyperpartition4 "$EX" "$TOPO" 2 >/dev/null; java -jar "$JAR" partition-tree "$EX" "$TOPO" >/dev/null; java -jar "$JAR" place4 "$EX" "$TOPO" 2 >/dev/null
echo "[9/16] routing / scheduler"; java -jar "$JAR" route4 "$EX" "$TOPO" >/dev/null; java -jar "$JAR" resource-route "$EX" "$TOPO" >/dev/null; java -jar "$JAR" schedule4 "$EX" "$TOPO" 2 >/dev/null
echo "[10/16] actual work stealing"; java -jar "$JAR" run-stealing "$EX" 4 128
echo "[11/16] live elasticity / straggler mitigation"; java -jar "$JAR" run-elastic "$EX" 1 4 64 >/dev/null; java -jar "$JAR" run-straggler "$EX" 4 >/dev/null
echo "[12/16] actor/channel/pipeline/collective"; java -jar "$JAR" run-actor3 "$EX" 4 >/dev/null; java -jar "$JAR" run-channel3 "$EX" 4 >/dev/null; java -jar "$JAR" run-pipeline3 "$EX" 4 >/dev/null; java -jar "$JAR" run-collective3 broadcast 4 >/dev/null
echo "[13/16] numerical / protocol"; java -jar "$JAR" simulate-statevector4 "$EX" 2 >/dev/null; java -jar "$JAR" protocol-compile3 "$EX" >/dev/null; java -jar "$JAR" remote-cnot-check 314159 >/dev/null
echo "[14/16] checkpoint / restore"; CP="${TMPDIR:-/tmp}/lctl16-checkpoint.json"; java -jar "$JAR" checkpoint4 "$EX" "$CP" >/dev/null; java -jar "$JAR" restore4 "$CP" >/dev/null; rm -f "$CP" "$CP.manifest.json"
echo "[15/16] chaos reference"; java -jar "$JAR" chaos "$EX" worker_crash >/dev/null
echo "[16/16] physical firewall"; OUT="$(java -jar "$JAR" adapter-check4 "$ROOT/adapters/mocks/abi3_physical_without_authority.json")"; echo "$OUT" | grep -q BLOCKED_EXTERNAL_AUTHORITY
echo "PASS: LCTL 1.6 RC1 smoke verification complete."
echo "Long 1h/8h/24h soak gates remain NOT_RUN unless separately executed."
