# LCTL Parallel Semantics
## Normative profile: `lctl.quantum.parallel` — Version 1.1.0

### 1. Thesis
LCTL parallelism is a semantic relation, not textual adjacency. Two operations are **logically parallel** only when the dependency, ownership, measurement, classical-feedback, coupling, and commutation rules allow the order between them to be relaxed. They are **physically parallel** only when a qualified target also permits simultaneous execution.

### 2. Parallel execution classes
LCTL distinguishes:

- `LOGICAL_PARALLEL` — independence/commutation is established in the source/QCIR model;
- `SCHEDULED_PARALLEL` — a deterministic scheduler places operations in the same schedule layer;
- `PHYSICAL_PARALLEL_CANDIDATE` — target capability analysis reports no known conflict;
- `PHYSICAL_PARALLEL_EXECUTION` — authenticated target evidence confirms concurrent execution.

The first three remain compiler/reference claims. Only the fourth crosses the target execution boundary.

### 3. Lane and tick semantics
`LANE` is a stable logical execution lane. A QFRAME `TICK` is a causal epoch. Operations in the same frame/tick may be scheduled in the same layer when their quantum/classical resource sets are disjoint or when a proof-admitted commutation rule explicitly permits reordering.

Frame parent/`PREV` relationships define happens-before constraints. A scheduler SHALL NOT move an operation across a measurement/classical-feedback edge, reset, explicit barrier, ownership transfer, or noncommuting dependency without an admitted proof.

### 4. Work and span
Every parallel plan SHOULD report:

- `W` — executable work (operation count under the declared cost model);
- `D` — span/critical-path depth;
- `Pmax = W/D` — maximum exploitable parallelism under the model;
- scheduled layers and lane utilization;
- communication, synchronization, and routing overhead separately.

Work/span is not a promise of speedup. Amdahl limits, memory/communication ceilings, topology, calibration, error correction, and target conflict can reduce physical speedup.

### 5. Barrier and synchronization semantics
`BARRIER`, `SYNC`, and `FENCE` are causal constraints. They do not change a quantum state by themselves. They prohibit schedule motion across the declared scope. Barriers SHALL NOT be inserted or removed when doing so changes measurement feedback, ownership, or target timing semantics.

### 6. Quantum ownership across lanes
No-cloning applies across lanes, processes, nodes, and target partitions. A scheduler may move ownership but may not duplicate unknown quantum state. Classical measurement results may be copied after measurement. Remote quantum transfer must be modeled as a target-qualified move, teleportation protocol, or admitted distributed-quantum primitive—not as a memory copy.

### 7. Communication and locality
LCTL treats communication as a resource with at least latency and transferred information volume. Target profiles MAY use an affine communication model `alpha + beta*m`, LogP-like parameters, BSP supersteps, or a more precise calibrated model. Communication/computation overlap is modeled separately from additive cost.

### 8. Distributed partial order
Distributed classical control SHALL preserve a happens-before relation. Lamport/vector-clock style metadata may be used for replay and provenance. Wall-clock coincidence alone is not a causal proof.

### 9. Impossibility is a valid result
Distributed control claims constrained by CAP-, FLP-, quorum-, or protocol-style impossibility conditions SHALL preserve the obstruction. LCTL must not turn an impossibility theorem into an optimizer warning and continue as if all requested properties were simultaneously guaranteed.

### 10. Fail-closed physical parallelism
A local SSD/CPU plan reports `physical_parallel=false` unless a target adapter supplies qualified timing/topology evidence. The reference scheduler proves only its declared logical model.
