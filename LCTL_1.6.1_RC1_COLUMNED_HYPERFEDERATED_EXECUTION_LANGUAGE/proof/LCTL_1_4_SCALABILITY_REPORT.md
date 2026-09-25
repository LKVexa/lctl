# LCTL 1.4 Scalability Report

## Task-runtime concurrency

| Workers | Max observed concurrency | Wall seconds |
|---:|---:|---:|
| 1 | 1 | 0.016744 |
| 2 | 2 | 0.016517 |
| 4 | 3 | 0.020142 |
| 8 | 3 | 0.017052 |

## Tiny statevector reference workload

| Workers | Speedup | Efficiency | Semantic equivalence |
|---:|---:|---:|---|
| 1 | 1.0138 | 1.0138 | True |
| 2 | 0.8856 | 0.4428 | True |
| 4 | 0.6494 | 0.1624 | True |
| 8 | 0.3578 | 0.0447 | True |

The tiny four-qubit process-sharded workload slows down as worker count rises because process launch/synchronization dominates. This is preserved as measured evidence; no QPU or parallel-speedup claim is inferred from simulator worker count.
