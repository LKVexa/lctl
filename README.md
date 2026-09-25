# LCTL

Public distribution of two LCTL release-candidate corpora by **RUSSELL PHILIP SMITHSON**.

Licensed under the [Apache License, Version 2.0](LICENSE). Attribution is in [NOTICE](NOTICE).

| Corpus | Version | Directory |
| --- | --- | --- |
| Hyperfederated Execution Fabric | 1.6.0-RC1 | [`LCTL_1.6.0_RC1_HYPERFEDERATED_EXECUTION_FABRIC`](LCTL_1.6.0_RC1_HYPERFEDERATED_EXECUTION_FABRIC) |
| Columned Hyperfederated Execution Language | 1.6.1-RC1 | [`LCTL_1.6.1_RC1_COLUMNED_HYPERFEDERATED_EXECUTION_LANGUAGE`](LCTL_1.6.1_RC1_COLUMNED_HYPERFEDERATED_EXECUTION_LANGUAGE) |

1.6.1-RC1 refines 1.6.0-RC1. It keeps the Java 21 hyperfederated runtime and adds **LCTL-C 1.0**, an 8-column source form that lowers into the sealed 18-cell canonical LCTL before verification or execution authority is claimed.

Both packages are **development candidates**, not fully qualified releases. The 1-hour, 8-hour, and 24-hour soak gates are `NOT_RUN`. Physical parallel, distributed, and multi-QPU execution is `BLOCKED_EXTERNAL_AUTHORITY`.

## License

Copyright 2026 RUSSELL PHILIP SMITHSON

Licensed under the Apache License, Version 2.0. You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.

The corpora originally included a placeholder that withheld a distribution grant. That placeholder is superseded by the `LICENSE` and `NOTICE` files at the repository root and inside each corpus.

## 1.6.0-RC1 — Hyperfederated Execution Fabric

Successor to LCTL 1.5.0-RC1. It preserves the core, 1.3 fabric, 1.4 adaptive mesh, and 1.5 federated runtime, and adds `runtime/bin/lctl-hyperfederated.jar`.

Start inside that directory. On Unix or macOS:

```sh
./START_LCTL_1_6.sh fabric-topology
./START_LCTL_1_6.sh fabric-calibrate
./START_LCTL_1_6.sh run-stealing examples/24_parallel_bell_pair_generation/program.lctl 4 128
./START_LCTL_1_6.sh run-elastic examples/24_parallel_bell_pair_generation/program.lctl 1 4 64
./VERIFY_LCTL_1_6.sh
```

On Windows:

```bat
START_LCTL_1_6.cmd fabric-topology
START_LCTL_1_6.cmd run-stealing examples\24_parallel_bell_pair_generation\program.lctl 4 128
VERIFY_LCTL_1_6.cmd
```

Evidence recorded in this candidate:

- LCTL 1.3 baseline: 189/189 PASS
- LCTL 1.4 baseline: 320/320 PASS
- LCTL 1.5 conformance: 512/512 PASS
- LCTL 1.6 conformance: 1024/1024 PASS
- Property campaign: 5000/5000 PASS
- Metamorphic campaign: 5000/5000 PASS

Read first: [`README_START_HERE.md`](LCTL_1.6.0_RC1_HYPERFEDERATED_EXECUTION_FABRIC/README_START_HERE.md), then `proof/RELEASE_QUALIFICATION.json`, `proof/LCTL_1_6_MANDATORY_FINAL_STATUS.txt`, `proof/LCTL_1_6_GAP_MATRIX.json`, `proof/LCTL_1_6_CONFORMANCE_REPORT.md`, and `proof/LCTL_1_6_SOAK_REPORT.md`.

## 1.6.1-RC1 — Columned Hyperfederated Execution Language

LCTL-C source uses eight columns. Stable or inferable cells are no longer repeated on every ordinary row:

```text
ID │ LANE │ OP │ OUT │ CTRL │ IN │ ARG │ META
```

Start inside that directory. On Unix or macOS:

```sh
./START_LCTL_1_6_1.sh column-selftest
./START_LCTL_1_6_1.sh column-verify examples_column/01_bell_state/program.lctlc
./START_LCTL_1_6_1.sh simulate examples_column/01_bell_state/program.lctlc 256 20260810
./START_LCTL_1_6_1.sh parallel-depth examples_column/24_parallel_bell_pair_generation/program.lctlc
./VERIFY_LCTL_1_6_1.sh
```

On Windows:

```bat
START_LCTL_1_6_1.cmd column-selftest
START_LCTL_1_6_1.cmd column-verify examples_column\01_bell_state\program.lctlc
VERIFY_LCTL_1_6_1.cmd
```

Column commands: `columnize`, `column-compile`, `column-verify`, `column-stats`, `column-roundtrip`, `column-selftest`.

Evidence recorded for this refinement:

- LCTL-C compiler/parser selftest: 512/512 PASS
- Bundled canonical examples converted and round-tripped: 53/53 PASS
- Aggregate example source: 58,857 bytes compact versus 123,330 bytes lowered canonical (52.28% reduction)
- Existing 1.6 conformance preserved: 1024/1024 PASS

Read first: [`README_START_HERE.md`](LCTL_1.6.1_RC1_COLUMNED_HYPERFEDERATED_EXECUTION_LANGUAGE/README_START_HERE.md), then `language/LCTL_C_1_0_SPEC.md`, `language/LCTL_C_ALIAS_CATALOG.md`, `language/LCTL_C_MIGRATION_GUIDE.md`, and `proof/LCTL_1_6_1_COLUMN_LANGUAGE_REPORT.md`.

## Requirements

The packaged runtimes are Java 21 jars (`lctl-hyperfederated.jar`, with the preserved federated, mesh, fabric, and core jars). Network default in each manifest is `deny`. Backend is `none`.

## Layout

Each corpus keeps its own manifest, checksums, spec, compiler, runtime, examples, conformance suite, and proof record:

- `MANIFEST.json` — version, runtime hashes, evidence, qualification boundary
- `PACKAGE_SHA256SUMS.txt` — package checksums
- `README_START_HERE.md` — candidate-specific start notes
- `spec/`, `language/` (1.6.1), `compiler/`, `runtime/`, `examples/`, `examples_column/` (1.6.1), `conformance/`, `proof/`
