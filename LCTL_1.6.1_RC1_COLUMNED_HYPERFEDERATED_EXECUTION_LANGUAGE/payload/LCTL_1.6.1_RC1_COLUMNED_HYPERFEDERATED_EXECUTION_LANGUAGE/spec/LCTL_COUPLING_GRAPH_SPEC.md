# LCTL Coupling Graph Specification

LCTL preserves the block-system interpretation

`[ A  B ; C  D ]`

with diagonal blocks describing local subsystems and off-diagonal blocks describing coupling. Partitioning may treat zero/weak coupling as a separability candidate, while strong coupling can require monolithic execution.

Supported classifications include `DECOUPLED`, `WEAKLY_COUPLED`, `MODERATELY_COUPLED`, `STRONGLY_COUPLED`, `MONOLITHIC_REQUIRED`, and `COUPLING_UNRESOLVED`.

`COUPLING_RECIPROCITY` numerically checks the declared relation `B = C^T` in the reference verifier. Coupling strength is one partitioning input, never the sole criterion.
