# LCTL 1.6 — Work stealing

The 1.6 worker runtime uses per-worker concurrent deques. Test work is initially skewed and peer workers remove tasks from another queue; the proof ledger records a nonzero actual-cross-queue-steal count and a trace hash.

## Evidence

See the corresponding JSON ledger in `proof/` and `LCTL_1_6_GAP_MATRIX.json`.
