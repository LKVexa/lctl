# LCTL Parallel & Distributed Normative Specification
## Profile `lctl.quantum.parallel.distributed` — 1.2.0-rc1

### Status
This profile is an implementation-backed development candidate. It qualifies logical parallel/distributed compilation and local classical distributed emulation only. It does not claim physical multi-QPU execution.

### Core invariant
A distributed LCTL program is a causally constrained transformation graph. Concurrency is admitted only when ownership, causality, coupling, topology, communication, synchronization, locality, target capability, and error constraints permit it.

### Required execution labels
Every distributed output must preserve three independent axes:

- parallel state: `SERIAL`, `LOGICAL_PARALLEL`, `SCHEDULED_PARALLEL`, `PARALLEL_EMULATION`, `PHYSICAL_PARALLEL_CANDIDATE`, `PHYSICAL_PARALLEL_EXECUTION`;
- distributed state: `LOCAL_ONLY`, `LOGICAL_DISTRIBUTED`, `DISTRIBUTED_SCHEDULED`, `DISTRIBUTED_CLASSICAL_EMULATION`, `DISTRIBUTED_COMPILED`, `PHYSICAL_DISTRIBUTED_CANDIDATE`, `PHYSICAL_DISTRIBUTED_EXECUTION`;
- quantum boundary: `NOT_CROSSED`, `CANDIDATE`, `CROSSED`.

The reference runtime never emits a crossed physical boundary.

### New semantic faces
`DISTRIBUTED`, `TOPOLOGY`, `PROTOCOL`, `FAILURE`, and `MEMORY` are first-class metadata faces in LCTL/1.2. They are seal-protected and proof/provenance-bearing.

### Compiler pipeline
`source → types/ownership → causal DAG → commutation/coupling → work/span → partition → topology → placement → routing → communication → schedule → QCIR-P → resource/error/proof ledgers`.

### Fail-closed rule
If an operation cannot be placed, routed, owned, proven, or represented faithfully, compilation returns a typed blocker rather than silently changing semantics.
