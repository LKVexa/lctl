# LCTL 1.5 Safety Invariants

The 1.5 runtime and qualification layer must fail closed on: unknown-qstate cloning, double ownership, use-after-move, use-after-destructive-measure, unsatisfied classical feedback, expired/double-consumed ebits, dependency violations, invalid routes, stale material schedules, hidden consistency downgrade, and false physical claims.

Classical recovery, CRDT merge, checkpointing and replica operations do not relax quantum ownership semantics.
