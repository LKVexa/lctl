# LCTL 1.6 — Parallelism

The runtime now distinguishes semantic/task/pipeline/circuit/shot/tensor/shard/process/domain widths and selects a profitable local worker width. Work stealing is implemented with per-worker deques and actual cross-queue task movement.

## Evidence

See the corresponding JSON ledger in `proof/` and `LCTL_1_6_GAP_MATRIX.json`.
