# LCTL 1.6 — Checkpoint and replay

Checkpoint 4.0 writes classically representable checkpoint metadata plus a commit manifest and hash. Inherited deterministic event replay remains verified. Complete reconstruction of every queue/shard/resource state is still partial.

## Evidence

See the corresponding JSON ledger in `proof/` and `LCTL_1_6_GAP_MATRIX.json`.
