# Quantum Ownership / No-Cloning Model

Unknown quantum values are affine resources. `CLONE` and `ALIAS_Q` are invalid. Gate controls and targets must be disjoint where required. Measurement consumes the logical quantum value in the reference type model; subsequent quantum use requires explicit `RESET`/repreparation. Classical measurement results may be duplicated. This is a language-level ownership discipline and does not assert that physical hardware literally destroys a measured qubit.

## Distributed and parallel ownership (1.1)

Quantum ownership is global across all lanes and partitions. Scheduling the same unknown quantum value on two lanes is not duplication permission. Remote transfer must be an ownership move or a target-qualified quantum protocol. Classical post-measurement values may be copied according to their classical type.
