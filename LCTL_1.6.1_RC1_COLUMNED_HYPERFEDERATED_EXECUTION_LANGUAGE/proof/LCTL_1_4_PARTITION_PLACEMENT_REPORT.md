# LCTL 1.4 Partition & Placement Report

The 1.4 reference partitioner consumes causal edges plus hyperedges formed from shared qubits and lane affinity. On the parallel Bell-pair reference it produced:

- edge cut: **0.0**
- hyperedge cut: **0.0**
- imbalance: **0.5**
- hyperedges: **3**

The implementation is a deterministic hyperedge-aware local-refinement reference. Full multilevel coarsening and canonical KL/FM are still partial. Placement binds partitions to logical nodes with capacity/memory/topology/calibration checks; portable NUMA/device affinity remains partial.
