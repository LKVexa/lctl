# LCTL Distributed Execution Model
## Derived design authority: Compendium Field 80

LCTL parallel programs use an explicit dependency graph and resource model. The distributed execution model distinguishes computation, communication, synchronization, memory/locality, reliability, and energy.

### Required analyzable quantities
- work, span, critical path, lane count, utilization;
- serial fraction / Amdahl-style bound where defined;
- communication messages, bytes/quantum-links, latency and bandwidth parameters;
- synchronization/barrier count;
- topology diameter/min-cut or target routing constraints where available;
- memory/locality tier for classical simulation/compiler data;
- failure/retry/checkpoint assumptions;
- physical-QPU parallel-capability evidence separately from logical scheduling.

### Communication semantics
Classical SEND/RECV/COLLECTIVE metadata may be modeled with latency/bandwidth costs. Quantum communication requires target-qualified entanglement distribution/teleportation/move semantics and cannot be reduced to ordinary byte copying.

### Memory hierarchy
Classical simulator/compiler performance may depend on cache, NUMA, SSD, RAM, and accelerator locality. These are host-performance semantics and do not alter the quantum meaning of the LCTL program.

### Reliability
Failure rates, checkpoint intervals, retries, and quorum conditions are explicit assumptions. A replayable result SHALL identify whether it came from deterministic replay, randomized scheduling, recovered execution, or physical target evidence.

### Consensus and impossibility
CAP/FLP-like constraints are represented as `impossible`, `combinatorial`, `structural_analogue`, or another honest regime. The compiler must preserve the reason for the obstruction.
