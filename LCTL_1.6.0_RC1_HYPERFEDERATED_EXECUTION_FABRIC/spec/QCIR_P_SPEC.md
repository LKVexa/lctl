# QCIR-P 1.0 — Parallel / Distributed Quantum IR

QCIR-P extends QCIR with deterministic records from LCTL faces:

`DECL`, `CODE`, `PARALLEL`, `COUPLING`, `FIELD`, `DISTRIBUTED`, `TOPOLOGY`, `PROTOCOL`, `FAILURE`, and `MEMORY`.

Each record preserves source ID, face, lane, operation, operands/parameters, exactness regime, and proof reference. The document additionally carries the causal DAG, topology hash, schedule hash, source hash, physical-boundary flags, and execution class.

`qcir-p FILE [OUT]` is the reference serializer.
