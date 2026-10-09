# LCTL 1.4 Architecture Report

LCTL 1.4.0-RC1 layers an adaptive execution mesh over the preserved 1.3 native fabric. The new mesh adds exact bounded partial-order analysis, semantic parallel-opportunity discovery, hyperedge-aware partitioning, Pareto partition candidates, multi-route classical/logical-quantum routing, a richer temporal scheduler, Java task/BSP/asynchronous execution, executable classical collectives, circuit farming, process-sharded noisy density evolution, SSD-backed out-of-core simulation, protocol compilation, arbitrary-qubit ideal teleportation equivalence, live recovery reference actions, consistency checks, and event reconstruction.

The architecture deliberately separates semantic parallelism from physical hardware parallelism. The reference profile remains local/offline and does not claim a QPU or quantum network. Quantum payload ownership is never duplicated by the task runtime.

Canonical reference path:

```text
LCTL source
 → 1.3 SES / ownership graph
 → partial order + parallel opportunity discovery
 → commutation authority 2.0
 → hypergraph / Pareto partitioning
 → hierarchical placement
 → classical / logical-quantum routing
 → temporal scheduling
 → task / BSP / async / farm or numerical backend
 → protocol / recovery / event reconstruction
 → resource / error / provenance evidence
```
