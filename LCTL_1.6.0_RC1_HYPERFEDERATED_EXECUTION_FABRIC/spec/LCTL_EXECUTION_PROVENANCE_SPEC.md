# LCTL Execution Provenance Specification
## LCTL-EP/1.0

Each runtime-produced evidence record SHOULD expose these fields when the output format permits:

`language`, `execution_class`, `state_realization`, `host_authority`, `storage_authority`, `physical_qpu`, `quantum_boundary`, `source_sha256`, `target`, `adapter`, `evidence_status`.

Normative values for this reference implementation:

- `language=LCTL/1.0`
- `host_authority=CLASSICAL_REFERENCE_HOST`
- `storage_authority=CLASSICAL_STORAGE_AGNOSTIC`
- `physical_qpu=false` unless a qualified physical adapter proves otherwise
- `quantum_boundary=NOT_CROSSED` for local verification/simulation/compilation/estimation

A classical simulator result is authoritative only for the declared mathematical simulation model and numerical tolerance. It is not empirical evidence from physical quantum hardware.

## Parallel provenance additions

Parallel planning evidence adds `logical_parallel`, `scheduled_parallel`, `physical_parallel`, `work`, `span`, `schedule_layers`, `target_parallel_capability`, and `parallel_evidence_status`. The reference host always emits `physical_parallel=false` unless a qualified target receipt proves otherwise.
