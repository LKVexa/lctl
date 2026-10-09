# LCTL Execution / Evidence Class Catalog

| Class | What occurred | Physical QPU? | Boundary |
|---|---|---:|---|
| `SYMBOLIC` | Parse/type/proof/static verification | No | NOT_CROSSED |
| `CLASSICAL_SIMULATION` | Classical numeric emulation of quantum semantics | No | NOT_CROSSED |
| `QUANTUM_COMPILATION` | QCIR/optimization/lowering/artifact generation | No | NOT_CROSSED |
| `HARDWARE_ESTIMATE` | Analytical/compiler estimate for target resources/noise | No | NOT_CROSSED |
| `PHYSICAL_QPU_EXECUTION` | Qualified adapter caused physical QPU execution and produced authenticated evidence | Yes | CROSSED |

The words *quantum program* and *quantum language* describe semantics and intended target regime; they do not imply that the host storage medium or reference runtime is itself quantum hardware.
