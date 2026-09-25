# QUORUM LCTL 1.5.x
# FEDERATED PARALLEL & DISTRIBUTED RUNTIME MESH
## Comprehensive Prompt + Workflow for a Deeper, Wider, More Adaptive LCTL Execution System

**Authoritative input:** `LCTL_PARALLEL_DISTRIBUTED_1.4.0_RC1_ADAPTIVE_EXECUTION_MESH`  
**Target output family:** `LCTL_PARALLEL_DISTRIBUTED_1.5.x_FEDERATED_RUNTIME_MESH`  
**Primary objective:** evolve LCTL 1.4.x from an adaptive local/distributed execution mesh into a **federated, hierarchical, self-balancing, failure-aware parallel/distributed runtime architecture** capable of coordinating multiple execution domains, workers, processes, NUMA regions, devices, logical nodes, simulation shards, classical replicas, protocol engines, and qualified external adapters without weakening quantum ownership, causality, or evidence requirements.

---

# 0. GOVERNING INTENT

The 1.5.x work SHALL NOT be measured by:

- number of new keywords;
- number of workers launched;
- number of topology nodes modeled;
- number of metadata records;
- number of adapters stubbed.

It SHALL be measured by whether LCTL can:

1. discover useful parallelism automatically;
2. choose the correct layer of parallelism;
3. partition work hierarchically;
4. place work intelligently;
5. route communication efficiently;
6. adapt schedules while executing;
7. rebalance safely under skew;
8. survive admissible failures;
9. replay execution deterministically;
10. preserve exact quantum ownership and measurement causality;
11. scale from one process to many local and remote execution domains;
12. prove whether a claimed parallel/distributed execution actually occurred.

A capability that cannot execute, be measured, and produce evidence is not operational.

---

# 1. 1.4.0-RC1 BASELINE TO PRESERVE

The existing 1.4.0-RC1 package already provides substantial working authority and SHALL remain regression-protected:

- preserved 1.3 runtime and 189-case baseline;
- 320-case 1.4 conformance;
- 500-case property campaign;
- 500-case metamorphic campaign;
- 120-case recovery campaign;
- Semantic Execution Supergraph;
- exact bounded partial-order width;
- parallel-opportunity discovery;
- stronger commutation planning;
- hyperedge-aware deterministic partitioning;
- Pareto partition candidates;
- logical topology placement;
- multiple classical/logical-quantum route candidates;
- richer temporal scheduling;
- task/dataflow/BSP/async reference execution;
- executable classical collectives;
- circuit farming;
- process-sharded density evolution;
- stabilizer batch execution;
- SSD-backed out-of-core classical simulation;
- numerical backend planning;
- protocol compilation;
- arbitrary-qubit ideal teleportation verification;
- reference failure recovery actions;
- event reconstruction;
- typed error handling;
- fail-closed physical target boundary.

No 1.5.x change may remove a passing 1.4 behavior without an explicit, evidence-backed replacement.

---

# 2. EXACT 1.4 GAPS TO CLOSE

The 1.5.x implementation SHALL explicitly track and remediate:

1. incomplete full Clifford/channel commutation authority;
2. incomplete multilevel coarsening and canonical KL/FM hypergraph partitioning;
3. incomplete full Pareto vector over latency/ebit/fidelity/energy/failure;
4. incomplete portable NUMA/device/QPU hierarchical placement;
5. incomplete ebit-inventory min-cost-flow/time-expanded/purification-aware quantum routing;
6. incomplete calibrated crosstalk/deadline/energy-aware scheduler;
7. incomplete dynamic scheduling, work stealing, live reoptimization, and straggler mitigation;
8. incomplete explicit dataflow channels/backpressure and actor capability model;
9. missing executable CRDT and distributed merge runtime;
10. incomplete stabilizer reset/measurement breadth;
11. tensor-network planner without full numerical contraction engine;
12. incomplete arbitrary-state numerical remote-CNOT equivalence;
13. incomplete transactional live recovery across modeled failure classes;
14. incomplete event-sourced reconstruction of numerical shards and resource inventories;
15. incomplete covariance/calibration-aware error algebra;
16. incomplete streaming/dynamic Adapter ABI 2.0;
17. incomplete long-duration soak qualification;
18. absence of authenticated physical multi-QPU authority.

Each item SHALL end in exactly one state:

- `QUALIFIED`
- `OPERATIONAL`
- `IMPLEMENTED_PARTIAL`
- `BLOCKED_EXTERNAL_AUTHORITY`
- `REJECTED_NO_FAITHFUL_FORM`

---

# 3. 1.5.x CENTRAL THESIS

LCTL 1.5.x SHALL become a **federated execution mesh**.

A federated mesh is a hierarchy of execution domains in which each domain may contain workers, devices, memory regions, simulators, logical quantum nodes, and protocol resources.

The language/runtime SHALL reason over:

```text
PROGRAM
  ↓
SEMANTIC REGION
  ↓
TASK / DATAFLOW REGION
  ↓
PARALLEL FAMILY
  ↓
EXECUTION DOMAIN
  ↓
WORKER GROUP
  ↓
PROCESS / THREAD / DEVICE
  ↓
NUMERICAL SHARD / CLASSICAL REPLICA / QUANTUM OWNER
```

Execution domains MAY be:

- local thread groups;
- local process groups;
- NUMA regions;
- local device groups;
- loopback test clusters;
- remote adapter clusters;
- logical QPU groups;
- qualified physical QPU groups.

A logical domain SHALL never be mislabeled physical.

---

# 4. FEDERATION LANGUAGE OBJECTS

Add first-class semantic objects:

```text
FEDERATION
EXECUTION_DOMAIN
DOMAIN_GROUP
WORKER_GROUP
PLACEMENT_SET
ROUTE_SET
FAILURE_DOMAIN
TRUST_DOMAIN
CONSISTENCY_DOMAIN
CALIBRATION_DOMAIN
RESOURCE_POOL
EBIT_POOL
MEMORY_POOL
TASK_POOL
SHOT_POOL
PARAMETER_POOL
CIRCUIT_POOL
```

Every object SHALL define:

- identity;
- ownership scope;
- membership;
- capabilities;
- resource limits;
- trust;
- failure boundaries;
- migration rules;
- replication rules;
- consistency rules;
- provenance.

---

# 5. PARALLEL FAMILY MODEL

LCTL SHALL classify parallel work into explicit families:

- `INSTRUCTION_PARALLEL`
- `TENSOR_FACTOR_PARALLEL`
- `CIRCUIT_PARALLEL`
- `SHOT_PARALLEL`
- `PARAMETER_PARALLEL`
- `OBSERVABLE_PARALLEL`
- `TRAJECTORY_PARALLEL`
- `TASK_PARALLEL`
- `PIPELINE_PARALLEL`
- `DATA_PARALLEL`
- `NUMERICAL_SHARD_PARALLEL`
- `STABILIZER_BATCH_PARALLEL`
- `TENSOR_CONTRACTION_PARALLEL`
- `CLASSICAL_REPLICA_PARALLEL`
- `PROTOCOL_PARALLEL`
- `QPU_PARTITION_PARALLEL`
- `QNETWORK_PARALLEL`

The planner SHALL select the cheapest admissible family or combination of families.

---

# 6. HIERARCHICAL PARALLELISM SEARCH

Implement a search over parallelism layers.

For every region, evaluate:

```text
semantic_width
task_width
circuit_width
shot_width
parameter_width
observable_width
trajectory_width
state_shard_width
device_width
node_width
```

Estimate:

- useful concurrency;
- communication cost;
- synchronization cost;
- memory cost;
- failure exposure;
- error impact.

Choose the layer with the lowest predicted total execution cost subject to semantic validity.

---

# 7. HYBRID PARALLEL COMPOSITION

Allow combinations such as:

```text
circuit farm
  × shot batches
  × per-circuit task parallelism
  × local state sharding
```

but the planner SHALL bound combinatorial expansion.

Each combined plan SHALL expose:

- hierarchy;
- worker count;
- oversubscription risk;
- resource partition;
- predicted speedup;
- predicted overhead.

---

# 8. FULL COMMUTATION AUTHORITY 3.0

Close the remaining commutation gap.

Required authority:

## 8.1 Pauli / symplectic
Exact binary symplectic commute/anticommute.

## 8.2 Clifford
Exact conjugation for:
- H;
- S/SDG;
- CX;
- CZ;
- SWAP;
- Pauli gates.

## 8.3 Rotation families
- same-axis fusion;
- inverse cancellation;
- parameter normalization;
- disjoint support commutation;
- admitted controlled-rotation rules.

## 8.4 Diagonal families
Exact diagonal commutation.

## 8.5 Measurement
Explicit basis/order barriers.

## 8.6 Noise channels
For bounded supported channels, classify:
- exact commuting;
- conditionally commuting;
- noncommuting;
- unresolved.

## 8.7 Bounded matrix oracle
Compute commutator norms for small operators.

Every scheduling rewrite SHALL contain a proof record.

---

# 9. COMMUTATION-DRIVEN REWRITING

Use admitted commutation to:

- expose larger antichains;
- reduce critical path;
- cluster local gates;
- reduce remote communication;
- align measurement groups;
- reduce routing pressure.

Never reorder solely for performance without semantic admission.

---

# 10. MULTILEVEL HYPERGRAPH PARTITIONER 3.0

Implement a complete reference multilevel partitioner:

1. build weighted graph/hypergraph;
2. coarsen;
3. partition coarse graph;
4. uncoarsen;
5. refine;
6. run KL/FM-style improvement;
7. validate constraints;
8. compare small instances against exact oracle.

Weights SHALL include:

- data dependencies;
- quantum ownership;
- entanglement;
- communication bytes;
- message count;
- memory;
- expected compute;
- remote quantum operation cost;
- ebit consumption;
- fidelity penalty;
- failure exposure.

---

# 11. DYNAMIC REPARTITIONING

Permit runtime repartitioning when:

- worker capacity changes;
- workload estimate changes;
- memory pressure rises;
- route congestion changes;
- node fails;
- calibration expires.

Repartitioning may move only admissible work.

Unknown quantum state migration requires ownership transfer or an admitted quantum protocol.

---

# 12. PARETO PLANNER 3.0

Maintain a Pareto frontier across:

```text
makespan
communication_time
bytes
messages
memory
energy
remote_quantum_ops
ebit_consumption
fidelity_loss
coherence_exposure
failure_exposure
recovery_cost
```

No single scalar shall be privileged without an explicit policy.

Policies:

- `FASTEST`
- `LOWEST_COMMUNICATION`
- `LOWEST_MEMORY`
- `HIGHEST_FIDELITY`
- `LOWEST_FAILURE_EXPOSURE`
- `BALANCED`
- user-defined explicit weights.

---

# 13. FEDERATED PLACER

Implement placement over multiple nested domains.

Example:

```text
FEDERATION
  ├── DOMAIN_A
  │    ├── NUMA_0
  │    │    ├── CPU_WORKERS
  │    │    └── MEMORY_POOL
  │    └── SIMULATOR_POOL
  └── DOMAIN_B
       ├── DEVICE_GROUP
       └── LOGICAL_QPU_GROUP
```

Placement SHALL honor:

- capacity;
- memory;
- affinity;
- trust;
- failure domain;
- target support;
- calibration domain;
- communication cost.

---

# 14. PLACEMENT MIGRATION

Runtime migration SHALL be separated into:

- `CLASSICAL_TASK_MIGRATION`
- `CLASSICAL_STATE_REPLICA_REBALANCE`
- `NUMERICAL_SHARD_MIGRATION`
- `QUANTUM_OWNERSHIP_MIGRATION`
- `PHYSICAL_QSTATE_MIGRATION`

The final category requires admitted physical mechanism and evidence.

---

# 15. CLASSICAL ROUTER 3.0

Implement:

- k-shortest routes;
- congestion-aware routes;
- bandwidth reservation;
- deadline-aware routes;
- multicast trees;
- multipath striping for classical payloads;
- route failover;
- route hysteresis to prevent thrashing.

---

# 16. QUANTUM ROUTER 3.0

Implement logical/reference quantum network planning with:

- ebit inventory;
- generation queues;
- expiry windows;
- purification models;
- swapping;
- fidelity targets;
- success probability;
- expected retries;
- min-cost flow;
- time-expanded paths;
- alternate paths;
- reservation rollback.

Quantum routing SHALL optimize an explicit metric.

---

# 17. EBITS AS SCHEDULABLE RESOURCES

Treat ebits as time-bounded resources.

A remote protocol task cannot become ready until its required ebit is:

- generated;
- heralded;
- reserved;
- unexpired.

The scheduler SHALL account for entanglement setup on the critical path.

---

# 18. TEMPORAL SCHEDULER 3.0

Implement:

- ASAP;
- ALAP;
- critical-path;
- communication-aware;
- deadline-aware;
- energy-aware;
- fidelity-aware;
- coherence-aware;
- crosstalk-aware;
- failure-aware.

Small instances SHALL use an exact bounded oracle for comparison.

---

# 19. CALIBRATION-AWARE SCHEDULING

A physical-candidate schedule SHALL bind to:

```text
calibration_snapshot
valid_from
valid_until
gate_durations
error_rates
readout_rates
crosstalk_constraints
coherence_limits
```

If calibration expires before execution:

`SCHEDULE_REVALIDATION_REQUIRED`

---

# 20. DYNAMIC SCHEDULER 3.0

A live scheduler SHALL react to:

- queue depth;
- measured throughput;
- stragglers;
- node/worker failure;
- route changes;
- memory pressure;
- ebit expiration;
- target calibration change.

All reoptimization decisions SHALL be event-logged.

---

# 21. WORK STEALING 2.0

Implement real work stealing.

Classical movable tasks:
- stealable.

Unknown quantum-owned tasks:
- not clonable;
- migratable only via ownership move/admitted protocol.

Deterministic mode:
- deterministic victim order;
- deterministic task choice.

Throughput mode:
- free stealing with complete trace.

---

# 22. STRAGGLER ENGINE

Detect worker stragglers by:

- moving average throughput;
- queue delay;
- task duration deviation;
- heartbeat latency.

Mitigation:

Classical:
- move queued work;
- speculative duplicates where side-effect safe.

Quantum:
- redistribute future tasks only;
- no speculative qstate duplication.

---

# 23. ACTOR RUNTIME 2.0

Implement stateful actors for classical state.

Each actor SHALL have:

- mailbox;
- capability set;
- ownership;
- consistency profile;
- failure policy;
- supervision policy.

Quantum state may be referenced by ownership handle but not replicated as actor state.

---

# 24. CHANNEL / BACKPRESSURE RUNTIME

Implement bounded channels with:

- capacity;
- blocking send;
- blocking receive;
- timeout;
- select;
- close;
- cancellation;
- backpressure.

Track queue depth and wait times.

---

# 25. PIPELINE SCHEDULER

Build pipeline-stage scheduling with:

- bounded buffers;
- backpressure;
- stage parallelism;
- microbatching;
- stage replication for classical stages;
- ownership transfer for quantum payload stages.

---

# 26. CRDT RUNTIME

Implement reference classical CRDTs:

- G-Counter;
- PN-Counter;
- G-Set;
- OR-Set;
- LWW Register with explicit clock policy.

Properties:

- deterministic merge;
- commutative merge;
- associative merge;
- idempotent merge where required.

Do not apply CRDT semantics to quantum state.

---

# 27. CONSISTENCY CONTRACT ENGINE

Support:

- eventual;
- causal;
- sequential;
- linearizable;
- immutable replica.

The runtime SHALL verify whether the execution mode can meet the contract.

If not:

`CONSISTENCY_GUARANTEE_BLOCKED`

---

# 28. CLASSICAL QUORUM SEMANTICS

For classical replicated services, support explicit read/write quorum parameters:

```text
N
R
W
```

Do not equate this with quantum measurement or quantum quorum semantics.

Consistency implications SHALL be explained.

---

# 29. DISTRIBUTED CONSENSUS BOUNDARY

LCTL MAY provide abstract classical consensus interfaces, but:

- no fake consensus under impossible assumptions;
- FLP-like limits remain explicit;
- network partitions remain modeled;
- timeouts do not prove failure.

Reference implementation MAY use deterministic simulated consensus for testing, labeled as simulation.

---

# 30. BSP 3.0

Implement measured multi-domain BSP.

Per superstep:

- compute;
- communication;
- synchronization;
- imbalance;
- failure/recovery.

Support hierarchical barriers:
- worker-group;
- domain;
- federation.

---

# 31. ASYNC 3.0

Implement event-driven execution across domains.

Required:

- completion queues;
- timer wheel or equivalent;
- remote-event abstraction;
- retry events;
- route-change events;
- failure events;
- measurement/control events.

Global barriers SHALL be avoided unless semantically necessary.

---

# 32. DEADLOCK / LIVELOCK / STARVATION 2.0

Detect:

- wait-for cycles;
- resource lock cycles;
- unresolved futures;
- channel cycles;
- retry storms;
- starvation.

Output minimal cycle/witness where possible.

---

# 33. COLLECTIVES 2.0

Implement algorithms:

- tree broadcast;
- ring all-reduce;
- recursive doubling;
- pairwise all-to-all;
- tree reduction;
- scan.

Planner SHALL choose based on:
- worker count;
- message size;
- topology;
- latency/bandwidth.

---

# 34. FEDERATED FARM EXECUTOR

Generalize farm execution across:

- circuits;
- shots;
- parameters;
- trajectories;
- Hamiltonian terms;
- optimizer populations;
- independent proofs/tests.

Support nested farm parallelism with oversubscription protection.

---

# 35. ADAPTIVE SHOT EXECUTOR

Features:

- deterministic seed splitting;
- dynamic batch sizing;
- failed-batch retry;
- confidence interval monitoring;
- target/calibration compatibility;
- early stop when statistical precision target reached.

---

# 36. VARIATIONAL ORCHESTRATOR 3.0

Parallelize:

- parameter-shift terms;
- grouped observables;
- candidate parameter populations.

Track:

- optimizer state;
- objective uncertainty;
- convergence;
- failed evaluations;
- restart checkpoints.

---

# 37. NUMERICAL BACKEND PLANNER 3.0

Choose among:

- local statevector;
- distributed statevector;
- local density;
- distributed density;
- stabilizer;
- tensor network;
- trajectory farm;
- shot farm;
- circuit farm;
- SSD-backed out-of-core.

Model predicted:
- memory;
- compute;
- communication;
- error/tolerance;
- worker count.

---

# 38. DISTRIBUTED STATEVECTOR 3.0

Improve:

- shard layout;
- exchange planning;
- gate localization;
- distributed permutation;
- asynchronous exchange where safe;
- block checksums;
- restart from classical shard checkpoint.

Measure:
- local gates;
- exchange gates;
- bytes;
- syncs;
- I/O.

---

# 39. DISTRIBUTED DENSITY 3.0

Extend bounded reference support to:

- general admitted Kraus sets;
- two-qubit noise channels;
- correlated channel metadata;
- partial trace;
- expectation values.

Verify:
- trace;
- Hermiticity;
- positivity;
- complete positivity assumptions where applicable.

---

# 40. STABILIZER 3.0

Implement broader Clifford execution:

- H;
- S;
- SDG;
- X/Y/Z;
- CX;
- CZ;
- SWAP;
- Pauli measurement;
- reset/reprepare.

Benchmark:
- one tableau;
- batch tableaux;
- shot parallelism.

---

# 41. NUMERICAL TENSOR-NETWORK ENGINE

Convert the existing planner into an actual bounded execution backend.

Implement:

- tensor construction;
- gate application;
- contraction;
- contraction-order execution;
- normalization;
- result extraction.

Validate against statevector for small circuits.

---

# 42. TENSOR-NETWORK PARALLELISM

Parallelize contraction subtrees when independent.

Track:
- FLOPs;
- peak tensor size;
- temporary memory;
- communication;
- contraction-tree critical path.

---

# 43. OUT-OF-CORE 3.0

Enhance SSD-backed simulation:

- deterministic chunk format;
- checksums;
- cache;
- prefetch;
- write-behind;
- I/O queue;
- crash-resume ledger;
- partial shard recovery.

Always label:

`SSD_BACKED_CLASSICAL_SIMULATION`

---

# 44. NUMERICAL SHARD MIGRATION

Allow moving classical numerical shards between workers/domains.

Required:

- freeze point;
- checksum;
- transfer;
- verification;
- resume.

This is not physical quantum-state transfer.

---

# 45. PROTOCOL COMPILER 2.0

Compile high-level distributed quantum operations into schedulable protocol DAGs.

Support:

- EPR generation;
- teleport;
- remote CNOT;
- remote control;
- entanglement swap;
- purification;
- classical feedback.

---

# 46. NUMERICAL REMOTE-CNOT QUALIFICATION

Complete arbitrary-state numerical equivalence.

For bounded two-node simulation:

- generate arbitrary normalized input;
- execute remote-CNOT protocol;
- compare with ideal local CNOT;
- compute fidelity/error;
- verify ownership.

Required:

`REMOTE_CNOT_REFERENCE_EQUIVALENCE_PASS`

---

# 47. NOISY TELEPORTATION / REMOTE GATE PROFILE

Add explicit noise models for reference emulation.

Track:
- ebit fidelity;
- gate noise;
- measurement noise;
- classical control assumptions.

Never generalize beyond the chosen model.

---

# 48. LIVE RECOVERY 2.0

Execute recovery rather than merely classify.

Supported reference actions:

- worker restart;
- task resubmit;
- shard reconstruction;
- route failover;
- ebit regeneration;
- shot restart;
- circuit restart;
- optimizer resume;
- classical replica promotion.

---

# 49. RECOVERY SUPERVISION TREE

Introduce hierarchical supervisors:

```text
TASK SUPERVISOR
  ↓
WORKER SUPERVISOR
  ↓
DOMAIN SUPERVISOR
  ↓
FEDERATION SUPERVISOR
```

Each decides whether to:
- retry;
- restart;
- reroute;
- escalate;
- abort.

---

# 50. FAILURE DOMAINS

Model correlated failures:

- process;
- host;
- NUMA;
- device;
- network link;
- rack/logical domain;
- QPU;
- quantum link.

Classical replicas SHOULD avoid shared failure domains when requested.

Quantum state SHALL not be replicated for resilience.

---

# 51. EVENT SOURCING 3.0

All runtime mutations SHALL be event-sourced.

Event classes:

- scheduling;
- task lifecycle;
- ownership;
- route;
- message;
- ebit;
- shard;
- failure;
- recovery;
- topology;
- calibration;
- adapter;
- result.

---

# 52. COMPLETE STATE RECONSTRUCTION

Given event log + checkpoints, reconstruct:

- worker states;
- task queues;
- channel contents;
- ownership;
- classical replicas;
- CRDT state;
- shard locations;
- ebit inventory;
- routes;
- schedules;
- final outputs.

Replay mismatch SHALL fail qualification.

---

# 53. DISTRIBUTED CHECKPOINT SYSTEM

Checkpoint only what is classically representable:

- compiler state;
- task graph;
- actor state;
- CRDT state;
- optimizer state;
- simulator shards;
- measurement results;
- route/ebit metadata.

Do not checkpoint arbitrary unknown physical qstate as bytes.

---

# 54. ERROR ALGEBRA 3.0

Implement:

- probability composition;
- infidelity composition;
- variance;
- bias;
- covariance matrices;
- correlated bounds;
- numerical tolerance;
- calibration uncertainty;
- timing uncertainty.

Each composition SHALL declare assumptions.

---

# 55. MONTE CARLO UNCERTAINTY PROPAGATION

For supported uncertain parameters, optionally run deterministic-seeded Monte Carlo propagation.

Report:
- distribution;
- mean;
- variance;
- quantiles;
- sample count;
- seed;
- assumptions.

This is classical uncertainty analysis.

---

# 56. ERROR-AWARE PARETO PLANNING

Add error dimensions into:
- partitioning;
- routing;
- scheduling;
- protocol choice.

Produce multiple alternatives where no single plan dominates.

---

# 57. RESOURCE ACCOUNTING 3.0

Track:

Classical:
- CPU time;
- worker time;
- memory;
- NUMA movement;
- bytes;
- messages;
- collectives;
- SSD I/O;
- retries;
- checkpoints.

Quantum:
- logical qubits;
- gate depth;
- measurements;
- remote gates;
- ebits generated/consumed;
- swaps;
- purification rounds;
- fidelity;
- coherence exposure.

---

# 58. ENERGY / COST ACCOUNTING

Optional/reference:
- CPU energy estimate;
- I/O estimate;
- communication energy estimate;
- target-reported hardware cost.

Do not invent precise physical energy without measurement/model authority.

---

# 59. FEDERATED CLUSTER CONTROL PROTOCOL

Define control messages:

```text
HELLO
CAPABILITIES
JOIN
LEAVE
HEARTBEAT
ASSIGN
MIGRATE
START
PAUSE
RESUME
CHECKPOINT
RESULT
FAIL
ROUTE_UPDATE
TOPOLOGY_UPDATE
CALIBRATION_UPDATE
CANCEL
SHUTDOWN
```

All messages SHALL have sequence numbers and hashes.

---

# 60. MEMBERSHIP SERVICE

Track domain membership.

States:
- JOINING;
- ACTIVE;
- SUSPECT;
- FAILED;
- LEAVING;
- REMOVED.

Failure suspicion is not proof of failure.

---

# 61. LOGICAL CLOCKS

Use:
- Lamport clocks;
- optional vector clocks for classical event causality.

Wall clocks are evidence only.

Quantum causal ordering remains semantic/protocol-driven.

---

# 62. ADAPTER ABI 3.0

Expand target adapters to support:

- dynamic capabilities;
- live topology;
- calibration stream/poll;
- reservation;
- submit;
- status;
- cancel;
- failure notification;
- measurement delivery;
- receipt retrieval;
- route/link capability;
- authentication evidence.

---

# 63. ADAPTER SANDBOX

Adapters SHALL run behind a capability boundary.

Reference runtime defaults remain:

```text
BACKEND=none
NETWORK=deny
```

External adapters require explicit profile selection.

---

# 64. PHYSICAL EVIDENCE FIREWALL 3.0

Physical claims require:

- authenticated endpoint;
- live capability;
- calibration validity;
- target topology;
- execution receipt;
- measurement receipt;
- source hash;
- lowering hash;
- schedule hash;
- target timestamp/sequence;
- provenance hash.

Without all required evidence:

`BLOCKED_EXTERNAL_AUTHORITY`

---

# 65. SAFETY INVARIANTS

Continuously verify:

1. no unknown-qstate clone;
2. no ownership double-holder;
3. no use-after-move;
4. no use-after-destructive-measure;
5. no unsatisfied classical feedback;
6. no expired ebit consume;
7. no double ebit consume;
8. no task execution before dependencies;
9. no route use after invalidation;
10. no stale schedule after material topology/calibration change;
11. no hidden consistency downgrade;
12. no false physical claim.

---

# 66. FORMAL / MODEL CHECKING

For bounded state machines, model-check:

- ebit lifecycle;
- task lifecycle;
- ownership transfer;
- recovery supervisor;
- membership transitions;
- checkpoint/replay.

Generate counterexamples on violation.

---

# 67. PROPERTY-BASED TESTING 2.0

Generate deterministic random:

- DAGs;
- hypergraphs;
- task programs;
- circuits;
- topologies;
- failures;
- channel schedules;
- ebit lifecycles.

Run thousands of cases.

---

# 68. DIFFERENTIAL TESTING 2.0

Compare:

- 1.4 runtime;
- 1.5 runtime;
- single-process simulator;
- distributed simulator;
- tensor backend where valid.

---

# 69. METAMORPHIC TESTING 2.0

Properties:

- worker-count invariance;
- legal partition invariance;
- proven-commutation invariance;
- retry/recovery invariance;
- route alternative semantic invariance;
- classical replica merge convergence.

---

# 70. FUZZ / CORRUPTION TESTS

Fuzz:

- parser;
- topology;
- event log;
- schedule ledger;
- shard files;
- adapter receipts;
- ebit ledger.

Corruption must fail closed.

---

# 71. REQUIRED NEW POSITIVE CONFORMANCE

Create at least **256 new positive tests**.

Suggested groups:

- 32 commutation/rewrites;
- 32 partition/Pareto/placement;
- 32 routing/scheduling;
- 32 actor/channel/CRDT/consistency;
- 32 numerical backends;
- 32 protocols;
- 32 live recovery/replay;
- 32 federation/adapters/provenance.

---

# 72. REQUIRED NEW NEGATIVE CONFORMANCE

Create at least **256 rejection tests**.

Cover:

- ownership;
- channel misuse;
- consistency violation;
- CRDT invalid merge;
- partition capacity;
- route invalidation;
- ebit expiry;
- schedule staleness;
- work-steal violation;
- recovery violation;
- replay corruption;
- adapter forgery;
- false physical claim.

---

# 73. PERFORMANCE TEST MATRIX

Run:

```text
workers = 1,2,4,8,16,32 where host permits
domains = 1,2,4 where reference environment permits
```

Benchmark:

- task DAG;
- BSP;
- async;
- pipeline;
- collectives;
- circuit farm;
- shot farm;
- statevector;
- density;
- stabilizer;
- tensor network;
- out-of-core;
- protocol DAG;
- recovery.

---

# 74. STRONG / WEAK SCALING

Report:

- speedup;
- efficiency;
- communication;
- idle;
- synchronization;
- load imbalance;
- retries;
- I/O.

Do not fabricate scalability beyond host capacity.

---

# 75. AUTO-SCALING POLICY

For reference clusters, allow worker group size changes according to measured load.

Rules:
- scale up only when estimated benefit exceeds overhead;
- scale down only at safe task boundaries;
- preserve deterministic evidence.

---

# 76. COMMUNICATION AVOIDANCE

Compiler/runtime SHOULD prefer:

1. independent circuit/shot/parameter parallelism;
2. local task parallelism;
3. tensor factor partition;
4. numerical state sharding;
5. remote distribution;

in that approximate order when semantic and cost models support it.

This is a policy heuristic, not a semantic law.

---

# 77. OVERHEAD BUDGET

Every parallel plan SHALL estimate:

```text
scheduler_overhead
worker_startup
communication
synchronization
migration
checkpoint
recovery
```

If predicted overhead exceeds predicted benefit, planner SHOULD choose a less distributed plan.

---

# 78. ADAPTIVE FALLBACK

If a distributed plan fails qualification at runtime, fallback MAY occur to:

- fewer workers;
- local execution;
- circuit farm;
- shot farm;
- serial execution;

only if semantics are preserved.

Fallback SHALL be logged.

---

# 79. LONG SOAK QUALIFICATION

Required before native 1.5 qualification:

- 8-hour minimum development qualification;
- 24-hour recommended release qualification;
- failure injection during soak;
- worker churn;
- routing changes;
- checkpoint/replay;
- memory monitoring;
- deadlock monitoring;
- deterministic sample replay.

---

# 80. RELEASE STATUS VOCABULARY

Use only:

- `BLOCKED`
- `SPECIFIED`
- `SCAFFOLDED`
- `IMPLEMENTED`
- `VERIFIED`
- `OPERATIONAL`
- `QUALIFIED`

---

# 81. QUALIFICATION RULE

A feature reaches `QUALIFIED` only if:

1. implementation exists;
2. valid tests pass;
3. invalid tests reject;
4. evidence is deterministic/replayable where applicable;
5. numerical equivalence passes where applicable;
6. stress/soak passes;
7. resource/error/provenance ledgers exist;
8. known internal blockers are closed.

External physical hardware may remain separately blocked.

---

# PART II — QUORUM EXECUTION WORKFLOW

## PHASE 0 — FREEZE 1.4 BASELINE
- hash archive;
- rerun 1.3 regressions;
- rerun 1.4 320-case conformance;
- rerun property/metamorphic/recovery campaigns;
- verify short-soak evidence;
- freeze behavior.

**Gate:** `LCTL_1_4_BASELINE_VERIFIED`

---

## PHASE 1 — GAP MATRIX 1.5
Map each 1.4 gap to implementation, tests, benchmarks, evidence, exit gate.

**Gate:** `LCTL_1_5_GAP_MATRIX_PASS`

---

## PHASE 2 — FEDERATION TYPE SYSTEM
Implement execution-domain/federation objects.

**Gate:** `FEDERATION_TYPES_OPERATIONAL`

---

## PHASE 3 — PARALLEL FAMILY DISCOVERY
Implement hierarchical parallelism search.

**Gate:** `PARALLEL_FAMILY_DISCOVERY_OPERATIONAL`

---

## PHASE 4 — COMMUTATION 3.0
Close Clifford/channel gaps.

**Gate:** `COMMUTATION_3_QUALIFIED`

---

## PHASE 5 — REWRITE ENGINE
Use admitted rewrites to expose safe concurrency.

**Gate:** `PARALLEL_REWRITE_ENGINE_OPERATIONAL`

---

## PHASE 6 — MULTILEVEL HYPERGRAPH PARTITIONER
Implement coarsen/partition/uncoarsen/KL-FM.

**Gate:** `HYPERGRAPH_3_OPERATIONAL`

---

## PHASE 7 — DYNAMIC REPARTITIONER
Implement safe live repartition.

**Gate:** `DYNAMIC_REPARTITION_OPERATIONAL`

---

## PHASE 8 — PARETO 3.0
Full multidimensional frontier.

**Gate:** `PARETO_3_OPERATIONAL`

---

## PHASE 9 — FEDERATED PLACEMENT
Implement hierarchical placement.

**Gate:** `FEDERATED_PLACEMENT_OPERATIONAL`

---

## PHASE 10 — CLASSICAL ROUTER 3.0
Implement reservation/failover/multipath.

**Gate:** `CLASSICAL_ROUTER_3_OPERATIONAL`

---

## PHASE 11 — QUANTUM ROUTER 3.0
Implement inventory/flow/time-expanded routing.

**Gate:** `QUANTUM_ROUTER_3_OPERATIONAL_REFERENCE`

---

## PHASE 12 — TEMPORAL SCHEDULER 3.0
Implement all reference scheduling modes + exact small oracle.

**Gate:** `TEMPORAL_SCHEDULER_3_OPERATIONAL`

---

## PHASE 13 — DYNAMIC SCHEDULER
Implement live reoptimization.

**Gate:** `DYNAMIC_SCHEDULER_QUALIFIED`

---

## PHASE 14 — WORK STEALING / STRAGGLERS
Implement safe work stealing and mitigation.

**Gate:** `WORK_STEALING_OPERATIONAL`

---

## PHASE 15 — ACTOR / CHANNEL RUNTIME
Implement actors, mailboxes, channels, backpressure.

**Gate:** `ACTOR_CHANNEL_RUNTIME_OPERATIONAL`

---

## PHASE 16 — CRDT / CONSISTENCY
Implement reference replicated classical state.

**Gate:** `CLASSICAL_REPLICATION_QUALIFIED`

---

## PHASE 17 — BSP / ASYNC / PIPELINE
Upgrade hierarchical execution modes.

**Gate:** `HIERARCHICAL_RUNTIME_OPERATIONAL`

---

## PHASE 18 — DEADLOCK / LIVELOCK 2.0
Implement distributed wait analysis.

**Gate:** `LIVENESS_DETECTOR_OPERATIONAL`

---

## PHASE 19 — COLLECTIVES 2.0
Implement algorithm selection.

**Gate:** `COLLECTIVES_2_OPERATIONAL`

---

## PHASE 20 — FEDERATED FARM
Generalize circuit/shot/parameter/trajectory execution.

**Gate:** `FEDERATED_FARM_OPERATIONAL`

---

## PHASE 21 — ADAPTIVE SHOTS
Implement dynamic batching/statistical early-stop.

**Gate:** `ADAPTIVE_SHOTS_OPERATIONAL`

---

## PHASE 22 — VARIATIONAL 3.0
Implement adaptive orchestration.

**Gate:** `VARIATIONAL_3_OPERATIONAL`

---

## PHASE 23 — STATEVECTOR 3.0
Optimize sharding/exchange/checkpoint.

**Gate:** `STATEVECTOR_3_QUALIFIED`

---

## PHASE 24 — DENSITY 3.0
Broaden Kraus/correlated support.

**Gate:** `DENSITY_3_QUALIFIED`

---

## PHASE 25 — STABILIZER 3.0
Broaden Clifford/measurement/reset.

**Gate:** `STABILIZER_3_QUALIFIED`

---

## PHASE 26 — TENSOR NETWORK NUMERICAL ENGINE
Execute bounded contractions and compare to statevector.

**Gate:** `TENSOR_NETWORK_OPERATIONAL`

---

## PHASE 27 — OUT-OF-CORE 3.0
Implement cache/prefetch/crash-resume.

**Gate:** `OUT_OF_CORE_3_QUALIFIED`

---

## PHASE 28 — NUMERICAL PLANNER 3.0
Auto-select backend and parallel family.

**Gate:** `NUMERICAL_PLANNER_3_QUALIFIED`

---

## PHASE 29 — PROTOCOL COMPILER 2.0
Compile all supported remote primitives.

**Gate:** `PROTOCOL_COMPILER_2_OPERATIONAL`

---

## PHASE 30 — REMOTE-CNOT NUMERICAL EQUIVALENCE
Complete arbitrary-state test.

**Gate:** `REMOTE_CNOT_REFERENCE_EQUIVALENCE_PASS`

---

## PHASE 31 — NOISY PROTOCOL PROFILE
Implement explicit noisy reference protocol models.

**Gate:** `NOISY_PROTOCOL_REFERENCE_OPERATIONAL`

---

## PHASE 32 — LIVE RECOVERY 2.0
Execute transactional recovery paths.

**Gate:** `LIVE_RECOVERY_2_QUALIFIED`

---

## PHASE 33 — SUPERVISION TREE
Implement hierarchical recovery supervisors.

**Gate:** `RECOVERY_SUPERVISION_OPERATIONAL`

---

## PHASE 34 — EVENT SOURCING 3.0
Log all state transitions.

**Gate:** `EVENT_SOURCING_3_OPERATIONAL`

---

## PHASE 35 — COMPLETE RECONSTRUCTION
Reconstruct full classical runtime state.

**Gate:** `STATE_RECONSTRUCTION_QUALIFIED`

---

## PHASE 36 — CHECKPOINT SYSTEM
Implement safe distributed classical checkpoints.

**Gate:** `CHECKPOINT_SYSTEM_OPERATIONAL`

---

## PHASE 37 — ERROR ALGEBRA 3.0
Implement covariance/correlation/calibration.

**Gate:** `ERROR_ALGEBRA_3_QUALIFIED`

---

## PHASE 38 — ERROR-AWARE PARETO
Integrate uncertainty into planning.

**Gate:** `ERROR_AWARE_PARETO_OPERATIONAL`

---

## PHASE 39 — FEDERATION CONTROL PROTOCOL
Implement membership/control messages.

**Gate:** `FEDERATION_CONTROL_OPERATIONAL`

---

## PHASE 40 — MEMBERSHIP / LOGICAL CLOCKS
Implement membership and causal event clocks.

**Gate:** `MEMBERSHIP_CAUSALITY_OPERATIONAL`

---

## PHASE 41 — ADAPTER ABI 3.0
Implement streaming/dynamic adapter contract.

**Gate:** `ADAPTER_ABI_3_OPERATIONAL`

---

## PHASE 42 — ADAPTER MOCK CAMPAIGN
Test dynamic topology/calibration/failure.

**Gate:** `ADAPTER_MOCK_CAMPAIGN_PASS`

---

## PHASE 43 — FORMAL MODEL CHECKS
Model-check bounded lifecycle machines.

**Gate:** `FORMAL_STATE_MACHINE_CHECKS_PASS`

---

## PHASE 44 — PROPERTY CAMPAIGN 2.0
Run thousands of generated cases.

**Gate:** `PROPERTY_CAMPAIGN_2_PASS`

---

## PHASE 45 — DIFFERENTIAL / METAMORPHIC 2.0
Compare 1.4/1.5 and backend equivalence.

**Gate:** `DIFFERENTIAL_METAMORPHIC_2_PASS`

---

## PHASE 46 — 512-CASE NEW CONFORMANCE
Run 256 positive + 256 negative.

**Gate:** `LCTL_1_5_CONFORMANCE_PASS`

---

## PHASE 47 — PERFORMANCE MATRIX
Run worker/domain scaling.

**Gate:** `LCTL_1_5_PERFORMANCE_CHARACTERIZED`

---

## PHASE 48 — FAILURE / CHURN CAMPAIGN
Run node/worker/route/ebit failures.

**Gate:** `FEDERATED_RECOVERY_CAMPAIGN_PASS`

---

## PHASE 49 — 8-HOUR SOAK
Required minimum.

**Gate:** `LCTL_1_5_8H_SOAK_PASS`

---

## PHASE 50 — 24-HOUR RELEASE SOAK
Recommended for full release.

**Gate:** `LCTL_1_5_24H_SOAK_PASS` or explicit `NOT_RUN`

---

## PHASE 51 — RELEASE GAP AUDIT
Every requirement gets exact status.

**Gate:** `LCTL_1_5_GAP_AUDIT_COMPLETE`

---

## PHASE 52 — NATIVE FEDERATED QUALIFICATION

Independently qualify:

```text
NATIVE_PARALLEL_EXECUTION
NATIVE_DISTRIBUTED_EXECUTION
FEDERATED_RUNTIME
ADAPTIVE_SCHEDULING
DISTRIBUTED_NUMERICAL_SIMULATION
DISTRIBUTED_PROTOCOL_EMULATION
DYNAMIC_RECOVERY
CLASSICAL_REPLICATION
EVENT_REPLAY
```

**Gate:** `LCTL_1_5_NATIVE_FEDERATED_QUALIFIED`

---

## PHASE 53 — PHYSICAL QPU QUALIFICATION

Only with real authenticated authority.

Otherwise:

```text
PHYSICAL_PARALLEL_QPU_EXECUTION=BLOCKED_EXTERNAL_AUTHORITY
PHYSICAL_DISTRIBUTED_QPU_EXECUTION=BLOCKED_EXTERNAL_AUTHORITY
```

---

# PART III — REQUIRED RUNTIME COMMANDS

Recommended additions:

```text
federation-plan FILE TOPOLOGY
parallel-family FILE
rewrite-plan FILE
hyperpartition3 FILE TOPOLOGY K
repartition FILE TOPOLOGY
pareto3 FILE TOPOLOGY
place3 FILE TOPOLOGY
route3 FILE TOPOLOGY
qroute3 FILE TOPOLOGY
schedule3 FILE TOPOLOGY
run-federation FILE CONFIG
run-actor FILE WORKERS
run-channel FILE WORKERS
run-pipeline FILE WORKERS
run-crdt TYPE REPLICAS
run-consistency FILE PROFILE
run-collective2 TYPE WORKERS
run-farm2 FILE WORKERS
run-shots2 FILE WORKERS
simulate-statevector3 FILE WORKERS
simulate-density3 FILE WORKERS
simulate-stabilizer3 FILE WORKERS
simulate-tensor2 FILE WORKERS
simulate-ooc3 FILE WORKERS
protocol-compile2 FILE
remote-cnot-check FILE
recover2 FILE SCENARIO
checkpoint FILE
restore CHECKPOINT
reconstruct2 TRACE
adapter-check3 ADAPTER
federation-selftest
soak HOURS
```

---

# PART IV — REQUIRED EVIDENCE LEDGERS

Produce:

```text
FEDERATION_LEDGER.json
PARALLEL_FAMILY_LEDGER.json
REWRITE_PROOF_LEDGER.json
HYPERGRAPH_3_LEDGER.json
REPARTITION_LEDGER.json
PARETO_3_LEDGER.json
PLACEMENT_3_LEDGER.json
CLASSICAL_ROUTE_3_LEDGER.json
QUANTUM_ROUTE_3_LEDGER.json
EBIT_POOL_LEDGER.json
SCHEDULE_3_LEDGER.json
DYNAMIC_SCHEDULE_LEDGER.json
WORK_STEAL_LEDGER.json
STRAGGLER_LEDGER.json
ACTOR_LEDGER.json
CHANNEL_LEDGER.json
CRDT_LEDGER.json
CONSISTENCY_LEDGER.json
COLLECTIVE_2_LEDGER.json
FARM_2_LEDGER.json
NUMERICAL_3_LEDGER.json
PROTOCOL_2_LEDGER.json
RECOVERY_2_LEDGER.json
CHECKPOINT_LEDGER.json
EVENT_3_LEDGER.json
ERROR_3_LEDGER.json
ADAPTER_3_LEDGER.json
SOAK_LEDGER.json
RELEASE_QUALIFICATION.json
```

---

# PART V — REQUIRED REPORTS

Produce:

1. `LCTL_1_5_ARCHITECTURE_REPORT.md`
2. `LCTL_1_5_FEDERATION_REPORT.md`
3. `LCTL_1_5_PARALLELISM_REPORT.md`
4. `LCTL_1_5_PARTITION_PLACEMENT_REPORT.md`
5. `LCTL_1_5_ROUTING_REPORT.md`
6. `LCTL_1_5_SCHEDULER_REPORT.md`
7. `LCTL_1_5_ACTOR_DATAFLOW_REPORT.md`
8. `LCTL_1_5_CLASSICAL_REPLICATION_REPORT.md`
9. `LCTL_1_5_NUMERICAL_BACKENDS_REPORT.md`
10. `LCTL_1_5_PROTOCOL_REPORT.md`
11. `LCTL_1_5_RECOVERY_REPORT.md`
12. `LCTL_1_5_ERROR_ALGEBRA_REPORT.md`
13. `LCTL_1_5_CONFORMANCE_REPORT.md`
14. `LCTL_1_5_PERFORMANCE_REPORT.md`
15. `LCTL_1_5_SOAK_REPORT.md`
16. `LCTL_1_5_GAP_LEDGER.md`
17. `LCTL_1_5_PHYSICAL_TARGET_BLOCKER.md`

---

# PART VI — PACKAGE STRUCTURE

```text
LCTL_PARALLEL_DISTRIBUTED_1.5.x/
├── README_START_HERE.md
├── MANIFEST.json
├── authority/
├── spec/
├── compiler/
│   ├── parallel_family/
│   ├── commutation/
│   ├── rewrite/
│   ├── hyperpartition/
│   ├── repartition/
│   ├── pareto/
│   ├── placement/
│   ├── routing/
│   └── scheduler/
├── runtime/
│   ├── federation/
│   ├── membership/
│   ├── task/
│   ├── actor/
│   ├── channel/
│   ├── dataflow/
│   ├── bsp/
│   ├── async/
│   ├── pipeline/
│   ├── collectives/
│   ├── farm/
│   ├── load_balance/
│   ├── checkpoint/
│   └── replay/
├── consistency/
│   └── crdt/
├── simulator/
│   ├── statevector/
│   ├── density/
│   ├── stabilizer/
│   ├── tensor_network/
│   └── out_of_core/
├── protocols/
├── resilience/
├── adapters/
├── topology/
├── conformance/
├── benchmarks/
├── proof/
├── provenance/
└── tools/
```

---

# PART VII — VERSION PLAN

Recommended progression:

- `1.5.0-alpha1` — federation types + parallel family search + commutation 3;
- `1.5.0-alpha2` — multilevel hypergraph + Pareto 3 + federated placement;
- `1.5.0-beta1` — dynamic scheduler + work stealing + actor/channel runtime;
- `1.5.0-beta2` — CRDT/consistency + federation control protocol;
- `1.5.0-beta3` — statevector/density/stabilizer/tensor/out-of-core 3;
- `1.5.0-beta4` — protocol compiler 2 + remote-CNOT qualification;
- `1.5.0-rc1` — live recovery + event reconstruction + error algebra 3;
- `1.5.0-rc2` — 512-case conformance + performance + 8-hour soak;
- `1.5.0` — native federated parallel/distributed qualification;
- `1.6.x` — expanded real hardware adapters, remote cluster deployments, accelerator-specific backends;
- `2.0.0` — only for a breaking semantic generation or independently qualified physical multi-QPU architecture that requires incompatible language semantics.

---

# FINAL QUORUM EXECUTION INSTRUCTION

Apply this workflow to `LCTL_PARALLEL_DISTRIBUTED_1.4.0_RC1_ADAPTIVE_EXECUTION_MESH`.

The design objective is:

> **Maximize semantically valid useful concurrency while minimizing communication, synchronization, memory movement, error, and failure exposure.**

The runtime SHALL not distribute work simply because multiple workers exist.

It SHALL choose the cheapest valid parallelism layer.

It SHALL prefer:

- circuit/shot/parameter farming when independent;
- local task parallelism when sufficient;
- state sharding only when justified;
- local placement when remote communication dominates;
- explicit ownership transfer over copying;
- safe serialization over invalid concurrency;
- deterministic evidence over unexplained behavior;
- truthful `BLOCKED`, `UNSUPPORTED`, `IMPOSSIBLE`, or `NO_FAITHFUL_FORM` over fabricated success.

The final 1.5.x system SHALL be able to answer:

1. What concurrency exists?
2. What semantic family does it belong to?
3. Why is it safe?
4. Which combination of parallel layers is best?
5. How should the computation be partitioned?
6. Which execution domains should receive each partition?
7. Which routes and resources are required?
8. How does the schedule change under live load?
9. What happens when a worker, route, or resource fails?
10. Can the computation recover without violating quantum semantics?
11. Can every state transition be reconstructed?
12. How do error, fidelity, memory, latency, energy, and failure trade off?
13. What was simulated classically?
14. What was executed by native workers?
15. What, if anything, crossed the physical quantum boundary?

Mandatory final status block:

```text
NATIVE_PARALLEL_EXECUTION=
NATIVE_DISTRIBUTED_EXECUTION=
FEDERATED_RUNTIME=
ADAPTIVE_SCHEDULING=
DISTRIBUTED_NUMERICAL_SIMULATION=
DISTRIBUTED_PROTOCOL_EMULATION=
CLASSICAL_REPLICATION=
DYNAMIC_RECOVERY=
EVENT_REPLAY=
PHYSICAL_PARALLEL_QPU_EXECUTION=
PHYSICAL_DISTRIBUTED_QPU_EXECUTION=
```

No ambiguity is permitted.
