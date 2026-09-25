# LCTL 1.3 Workflow Phase Ledger

| Phase | Area | Status | Evidence |
|---:|---|---|---|
| 0 | Forensic baseline | PASS | `proof/LCTL_1_2_BASELINE_REGRESSION.txt` |
| 1 | Gap-to-requirement matrix | PASS | `proof/LCTL_1_3_GAP_LEDGER.md` |
| 2 | SES supergraph | PASS_REFERENCE | `runtime/bin/lctl-fabric.jar: ses` |
| 3 | Commutation authority | PARTIAL | `spec/COMMUTATION_AUTHORITY.md` |
| 4 | Hierarchical partitioner | PARTIAL | `proof/LCTL_1_3_PARTITIONER_QUALITY.json` |
| 5 | Placement engine | PARTIAL | `runtime/bin/lctl-fabric.jar: place` |
| 6 | Classical router | PASS_REFERENCE | `runtime/bin/lctl-fabric.jar: route` |
| 7 | Quantum router | PARTIAL | `proof/LCTL_1_3_ROUTING_QUALITY.json` |
| 8 | Temporal scheduler | PARTIAL | `proof/LCTL_1_3_SCHEDULER_QUALITY.json` |
| 9 | QCIR-P2 | PASS_REFERENCE | `runtime/bin/lctl-fabric.jar: qcir-p2` |
| 10 | Native local worker fabric | PASS_REFERENCE | `multi-process statevector worker subprocesses` |
| 11 | Distributed statevector | PASS_REFERENCE | `proof/LCTL_1_3_NUMERICAL_EQUIVALENCE.json` |
| 12 | Distributed density matrix | PARTIAL | `pure-unitary row-sharded density construction only` |
| 13 | Stabilizer engine | PASS_REFERENCE_SUBSET | `binary symplectic tableau subset` |
| 14 | Numerical planner | SCAFFOLDED | `spec/DISTRIBUTED_NUMERICAL_ENGINE_SPEC.md` |
| 15 | EPR resource engine | PASS_REFERENCE | `runtime/bin/lctl-fabric.jar: protocol-execute` |
| 16 | Teleportation executor | PARTIAL | `protocol state machine; arbitrary-state numerical integration incomplete` |
| 17 | Remote gate executor | PARTIAL | `reference protocol accounting only` |
| 18 | Swapping / purification | PARTIAL | `reference lifecycle state transitions` |
| 19 | Distributed classical feedback | PASS_REFERENCE | `happens-before protocol event` |
| 20 | Failure injection | PASS_REFERENCE | `proof/LCTL_1_3_RESILIENCE.json` |
| 21 | Recovery engine | PARTIAL | `classification operational; live recovery orchestration incomplete` |
| 22 | Event replay | PARTIAL | `trace integrity/replay inspection; full event-sourced state reconstruction incomplete` |
| 23 | Error algebra | PARTIAL | `spec/ERROR_ALGEBRA_SPEC.md` |
| 24 | Memory / NUMA / out-of-core | SCAFFOLDED | `existing memory model preserved; mmap statevector is out-of-core-capable primitive` |
| 25 | Task / BSP / async runtime | SCAFFOLDED | `legacy metadata preserved; dedicated executor incomplete` |
| 26 | Collectives | SCAFFOLDED | `semantic catalog preserved; executable collectives incomplete` |
| 27 | Shot / variational fabric | IMPLEMENTED_PARTIAL | `legacy simulator supports shots; dedicated distributed batch executor incomplete` |
| 28 | Load balance / stragglers | SCAFFOLDED | `automatic partitioning exists; dynamic rebalancing incomplete` |
| 29 | Adapter ABI + mocks | PASS_REFERENCE | `adapters/mocks/two_qpu_mock.json` |
| 30 | Security / trust / network policy | PASS | `BACKEND=none, NETWORK=deny by default` |
| 31 | Full conformance | PASS | `proof/LCTL_1_3_CONFORMANCE.json` |
| 32 | Numerical equivalence | PASS_REFERENCE | `proof/LCTL_1_3_NUMERICAL_EQUIVALENCE.json` |
| 33 | Scalability characterization | PASS | `proof/LCTL_1_3_SCALABILITY.json` |
| 34 | Optimizer quality | PARTIAL | `partition exact comparison present; exact schedule optimizer incomplete` |
| 35 | Soak / stress | PARTIAL | `25-iteration short stress PASS; 8-hour soak not executed` |
| 36 | Release gap audit | PASS | `proof/LCTL_1_3_GAP_LEDGER.md` |
| 37 | Logical/native distributed qualification | BLOCKED | `partial mandatory subsystems and soak gate` |
| 38 | Physical multi-QPU qualification | BLOCKED_EXTERNAL_AUTHORITY | `proof/LCTL_1_3_PHYSICAL_QUALIFICATION_BLOCKER.md` |
