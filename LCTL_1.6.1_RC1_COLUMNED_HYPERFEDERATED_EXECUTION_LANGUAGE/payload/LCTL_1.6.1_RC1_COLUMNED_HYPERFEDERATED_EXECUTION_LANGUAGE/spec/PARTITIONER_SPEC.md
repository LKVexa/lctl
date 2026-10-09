# Hierarchical Partitioner Specification

The reference 1.3 partitioner is executable and deterministic.

Current algorithm:
- ordered greedy seed assignment;
- dependency-cut penalty;
- lane-affinity bias;
- deterministic local refinement;
- exact exhaustive two-way validator for graphs up to 16 tasks.

Cost currently includes cut-edge penalties and load imbalance. The exact validator reports heuristic cost, exact small-instance cost, and optimality gap.

Required future expansion:
- multilevel coarsening;
- KL/FM refinement;
- hyperedge-aware entanglement/coupling penalties;
- dimensioned multi-objective vector including communication, ebit cost, memory, error, coherence, and failure exposure.
