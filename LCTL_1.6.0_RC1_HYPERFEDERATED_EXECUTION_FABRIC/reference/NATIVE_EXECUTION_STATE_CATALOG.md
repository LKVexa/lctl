# LCTL 1.3 Execution-State Catalog

The native fabric preserves the distinction among semantic planning, classical numerical execution, protocol emulation, and physical execution.

- `QUANTUM_COMPILATION`: planning/QCIR-P2 only.
- `CLASSICAL_DISTRIBUTED_STATEVECTOR_SIMULATION`: process-sharded classical amplitudes.
- `CLASSICAL_DISTRIBUTED_DENSITY_SIMULATION`: process-sharded classical density representation.
- `CLASSICAL_STABILIZER_SIMULATION`: tableau simulation.
- `DISTRIBUTED_PROTOCOL_EMULATION`: executable logical protocol state machine.
- `PHYSICAL_DISTRIBUTED_EXECUTION_BLOCKED_EXTERNAL_AUTHORITY`: no qualified hardware evidence.

A classical SSD, RAM map, worker process, or simulator state is never a physical quantum state.
