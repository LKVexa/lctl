# Temporal Scheduler Specification

The 1.3 scheduler consumes the causal graph, partition plan, placement, and classical routes. It performs deterministic communication-aware list scheduling with:
- predecessor completion constraints;
- inter-node communication latency;
- per-node serialization in the reference resource model;
- explicit makespan and communication-wait reporting.

Current RC1 emits crosstalk/coherence status honestly. Calibrated crosstalk matrices and full coherence-deadline enforcement require target data and remain partial. The scheduler does not claim global optimality.
