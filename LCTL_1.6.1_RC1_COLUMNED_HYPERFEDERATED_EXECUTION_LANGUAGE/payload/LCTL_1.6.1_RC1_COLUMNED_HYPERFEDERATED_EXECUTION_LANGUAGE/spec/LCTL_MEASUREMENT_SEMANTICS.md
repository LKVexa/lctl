# Measurement Semantics

`MEASURE_Z`, `MEASURE_X`, and `MEASURE_Y` use Born probabilities, seeded sampling, basis-correct post-measurement update, and binding into a declared classical bit. Measurement is stochastic but mathematically exact at the probability-law level. Repeated program shots replay from the initial state with a deterministic seed schedule. `CLASSICAL_IF` provides measurement-conditioned unary quantum control without silently duplicating quantum state.
