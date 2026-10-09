# LCTL Coupling Catalog

LCTL coupling rows preserve subsystem identity plus explicit cross edges. Recommended annotations include coupling block IDs, source/target subsystems, reciprocity relation, normalized strength, regime (`exact`, `linearized`, `perturbative`, etc.), interface condition, and solver/scheduler strategy (`monolithic` or `staggered`).

The reference verifier includes `ASSERT/COUPLING_RECIPROCITY`, whose parameter is `B||C` and verifies `B = C^T` numerically for square matrix literals.
