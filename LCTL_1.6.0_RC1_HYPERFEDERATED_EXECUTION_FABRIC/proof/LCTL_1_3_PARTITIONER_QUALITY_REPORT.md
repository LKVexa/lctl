# LCTL 1.3 Partitioner Quality

The reference partitioner is automatic and deterministic. For two-way graphs up to 16 tasks it compares its result against exhaustive search.

| Case | Heuristic cost | Exact small cost | Gap |
|---|---:|---:|---:|
| 24_parallel_bell_pair_generation | 1.0 | 1.0 | 0.0 |
| 25_parallel_ghz_preparation | 2.0 | 2.0 | 0.0 |
| 34_work_span_analysis | 0.0 | None | None |
| 53_native_distributed_bell_fabric | 1.0 | 1.0 | 0.0 |

The current cost function is still a reduced reference model (cut penalties + load imbalance), so full multi-objective partition optimization remains partial.
