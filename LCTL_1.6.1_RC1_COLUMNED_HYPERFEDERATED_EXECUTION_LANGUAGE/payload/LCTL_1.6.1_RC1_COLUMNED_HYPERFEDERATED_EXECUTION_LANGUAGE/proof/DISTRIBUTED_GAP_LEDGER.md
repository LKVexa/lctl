# LCTL 1.2.0-rc1 — Exact Gap Ledger

## Implemented reference authority
- LCTL/1.2 parser/profile.
- distributed/topology/protocol/failure/memory faces.
- global ownership bind/move/remote validation and no-cloning rejection.
- causal DAG extraction.
- conservative parallel scheduling/work-span reporting.
- topology validation and hashing.
- QCIR-P deterministic serialization.
- classical distributed emulator.
- reference EPR/teleport/remote-gate/swap validation.
- distributed resource reporting.
- 30 positive and 30 mandatory negative distributed tests.

## Remaining engineering gaps
1. full proof-catalog commutation engine for distributed rewrites;
2. automatic multi-objective graph partitioner rather than metadata-driven reference partition plans;
3. topology-calibrated placement optimizer;
4. hardware route discovery and entanglement-routing optimizer;
5. complete crosstalk/timing-aware scheduler;
6. distributed state-vector/density/stabilizer engines that actually partition numerical state across OS processes/nodes (the RC examples model these partitions but the bundled Java reference simulator remains local);
7. executable protocol-level state transfer inside the simulator for every distributed protocol primitive;
8. complete dynamic resilience engine for arbitrary failure injection;
9. dimensioned/calibrated composition law for all distributed quantum error sources;
10. qualified physical target adapters.

## Physical qualification blocker
`PHYSICAL_DISTRIBUTED_EXECUTION_BLOCKED`

No authenticated physical multi-QPU infrastructure, calibrated quantum interconnect, target timing/crosstalk evidence, or physical execution receipts are bundled. The reference emulator must not be used as evidence of physical distributed quantum execution.
