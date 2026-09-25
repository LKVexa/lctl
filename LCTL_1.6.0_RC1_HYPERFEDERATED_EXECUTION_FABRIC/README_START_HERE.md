# LCTL Parallel & Distributed 1.6.0-RC1 — Hyperfederated Execution Fabric

This package is the QUORUM-applied successor to LCTL 1.5.0-RC1. It preserves the core, 1.3 fabric, 1.4 adaptive mesh, and 1.5 federated runtime, then adds the Java 21 `runtime/bin/lctl-hyperfederated.jar` layer.

## Start

Unix/macOS:

```sh
./START_LCTL_1_6.sh fabric-topology
./START_LCTL_1_6.sh fabric-calibrate
./START_LCTL_1_6.sh run-stealing examples/24_parallel_bell_pair_generation/program.lctl 4 128
./START_LCTL_1_6.sh run-elastic examples/24_parallel_bell_pair_generation/program.lctl 1 4 64
./VERIFY_LCTL_1_6.sh
```

Windows:

```bat
START_LCTL_1_6.cmd fabric-topology
START_LCTL_1_6.cmd run-stealing examples\24_parallel_bell_pair_generation\program.lctl 4 128
VERIFY_LCTL_1_6.cmd
```

## Verified in this build

- LCTL 1.3 baseline: **189/189 PASS**
- LCTL 1.4 baseline: **320/320 PASS**
- LCTL 1.5 conformance: **512/512 PASS**
- LCTL 1.6 conformance: **1024/1024 PASS**
- Property campaign: **5000/5000 PASS**
- Metamorphic campaign: **5000/5000 PASS**
- Actual local cross-queue work stealing: **VERIFIED**
- Live local worker growth: **VERIFIED**
- Pure-classical straggler mitigation: **VERIFIED**
- Strong/weak scaling: **CHARACTERIZED on the current local host**

## Qualification boundary

This archive is a **1.6.0-RC1 development candidate**, not a final qualified 1.6.0 release. The 1-hour, 8-hour, and 24-hour workflow soak gates were not run. Canonical full KL/FM, complete event reconstruction, production-scale tensor execution, live remote multi-host authority, and physical multi-QPU authority remain incomplete or externally blocked.

Physical QPU execution remains `BLOCKED_EXTERNAL_AUTHORITY`.

Start evidence review with:

- `proof/RELEASE_QUALIFICATION.json`
- `proof/LCTL_1_6_MANDATORY_FINAL_STATUS.txt`
- `proof/LCTL_1_6_GAP_MATRIX.json`
- `proof/LCTL_1_6_CONFORMANCE_REPORT.md`
- `proof/LCTL_1_6_SOAK_REPORT.md`
