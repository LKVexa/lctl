# LCTL 1.3 Scheduler Quality

The scheduler is executable and deterministic, with predecessor, communication, partition, placement, and per-node resource constraints. It does **not** claim global optimality in RC1.

- `24_parallel_bell_pair_generation`: makespan=3.0, communication wait=0.0.
- `25_parallel_ghz_preparation`: makespan=3.0, communication wait=0.0.
- `53_native_distributed_bell_fabric`: makespan=3.0, communication wait=0.0.

Exact bounded schedule search and calibrated crosstalk/coherence models remain incomplete; the release ledger therefore marks the full scheduler target partial.
