# LCTL 1.3 Native Fabric Command Catalog

| Command | Function | Physical QPU? |
|---|---|---|
| `ses FILE` | Build Semantic Execution Supergraph | No |
| `commute-plan FILE` | Explain pairwise commutation/serialization decisions | No |
| `partition FILE [k]` | Automatic deterministic reference partitioning | No |
| `place FILE TOPOLOGY [k]` | Bind partitions to topology nodes | No |
| `route FILE TOPOLOGY [k]` | Classical + logical quantum route discovery | No |
| `schedule FILE TOPOLOGY [k]` | Communication-aware temporal schedule | No |
| `qcir-p2 FILE TOPOLOGY [k]` | Emit deterministic QCIR-P2 plan | No |
| `simulate-distributed FILE [workers]` | Multi-process memory-mapped statevector simulation | No |
| `simulate-density-distributed FILE [workers]` | Multi-process pure-state density construction | No |
| `simulate-stabilizer FILE` | Binary-symplectic Clifford tableau simulation | No |
| `protocol-execute FILE [seed]` | Distributed quantum-protocol reference state machine | No |
| `failure-inject FILE SCENARIO [seed]` | Deterministic failure injection | No |
| `recover FILE SCENARIO [seed]` | Recovery admissibility classification | No |
| `error-algebra FILE` | Typed error-ledger reduction | No |
| `benchmark FILE [workers]` | Classical fabric performance characterization | No |
| `replay TRACE` | Deterministic trace integrity/replay inspection | No |
| `adapter-check ADAPTER` | Inspect adapter qualification evidence | No |
| `physical-distributed-run FILE` | Fail closed without external authority | Requires real authority |
