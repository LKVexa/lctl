# Resource Model

The local estimator emits logical qubits, classical bits, operation counts by opcode, multi-qubit count, non-Clifford count, measurements, a dependency-aware logical depth estimate, and accumulated approximation bound. Physical-qubit, hardware fidelity and wall-clock estimates are target-specific and are not fabricated by the backendless core.

## Parallel-resource extension

Resource reports SHOULD include work, span, maximum exploitable parallelism `W/D`, scheduled depth, lane count/utilization, communication and synchronization counts, routing overhead, target conflicts, memory/locality notes, and reliability assumptions. These are separate from logical qubit/gate/error accounting.
