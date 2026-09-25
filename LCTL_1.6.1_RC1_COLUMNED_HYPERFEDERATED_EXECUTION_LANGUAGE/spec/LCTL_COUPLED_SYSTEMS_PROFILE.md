# LCTL Coupled Systems Profile
## Derived design authority: Compendium Field 23

### Block-coupling doctrine
A coupled system is modeled as diagonal subsystem operators plus explicit off-diagonal couplings. For a block operator

`[ A  B ; C  D ]`

`A,D` describe subsystem-local behavior and `B,C` describe cross-system influence. Coupling SHALL NOT be hidden as an undocumented side effect.

### Reciprocity and symmetry
Where a model asserts reciprocity, LCTL may require `B = C^T` (or the appropriate adjoint/dual relation). Symmetric coupling can enable variational structure and symmetric solvers; antisymmetric coupling represents a different semantic class and must not be silently symmetrized.

### Coupling strength
LCTL supports a normalized coupling-strength annotation. Small coupling may justify an admitted perturbative/staggered strategy; strong coupling may require a monolithic solve/schedule. The threshold and norm are part of the model, not universal constants.

### Parallel consequence
A scheduler SHALL distinguish **independent subsystems** from **weakly coupled** and **strongly coupled** subsystems. Off-diagonal coupling induces dependencies even when the state objects occupy different lanes. Staggered execution is an approximation unless exact decoupling has been proved.

### Interface semantics
Field/interface rows may declare continuity, balance, transfer, reciprocity, and target-placement constraints. Cross-lane interfaces become explicit edges in QCIR/PQIR dependency graphs.
