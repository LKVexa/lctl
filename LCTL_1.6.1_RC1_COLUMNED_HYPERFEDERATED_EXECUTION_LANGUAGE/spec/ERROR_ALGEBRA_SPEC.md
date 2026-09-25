# Distributed Error Algebra Specification

LCTL 1.3 keeps error dimensions explicit. The RC1 executable error-algebra command recognizes declared `abs<=` / `rel<=` scalar bounds and keeps sampling or otherwise incompatible dimensions unresolved rather than folding them into a false scalar.

Normative rule: errors may combine only when their domains, units, and composition law are compatible and their independence/correlation assumptions are declared.

Planned expansion includes probability-product laws, covariance-aware statistical composition, calibrated link/gate/readout models, purification-model uncertainty, and sensitivity intervals.
