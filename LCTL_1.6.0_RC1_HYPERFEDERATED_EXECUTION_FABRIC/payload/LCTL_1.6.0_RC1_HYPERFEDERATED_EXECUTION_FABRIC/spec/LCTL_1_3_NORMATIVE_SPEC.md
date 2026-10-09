# LCTL 1.3 Native Parallel/Distributed Normative Specification

## Status
Development Candidate RC1.

LCTL 1.3 preserves LCTL/1.0–1.2 source compatibility while adding a native execution-fabric profile. The normative execution layers are: parser/type system, global quantum ownership, Semantic Execution Supergraph (SES), commutation/coupling analysis, partitioning, placement, classical/quantum routing, temporal scheduling, QCIR-P2, local worker fabric, distributed numerical engines, distributed protocol emulator, and evidence ledgers.

## Execution identity
Every result must distinguish classical computation from physical quantum execution. The reference fabric is offline and local by default:
`BACKEND=none`, `NETWORK=deny`, `physical_qpu=false`.

The fabric may execute classical numerical representations of quantum programs across threads/processes. Such execution is `CLASSICAL_DISTRIBUTED_*`, never physical distributed quantum execution.

## Operational rule
A feature is OPERATIONAL only when syntax/IR (if needed), semantic validation, executable implementation, positive test, negative test, deterministic evidence, and resource/error/provenance evidence exist.

## Native fabric profile
Canonical profile: `lctl.quantum.parallel.distributed.native`.
Compatibility input: `LCTL/1.2` and earlier supported LCTL source.
New planning IR: `QCIR-P2/1.0`.

## Physical boundary
Physical multi-QPU execution remains `BLOCKED_EXTERNAL_AUTHORITY` until an authenticated adapter supplies endpoint identity, topology, calibration, interconnect, execution and measurement receipts, and complete provenance.
