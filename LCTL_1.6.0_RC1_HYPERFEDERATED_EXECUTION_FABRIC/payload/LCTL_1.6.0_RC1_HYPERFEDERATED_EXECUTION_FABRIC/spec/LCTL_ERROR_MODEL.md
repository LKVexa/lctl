# Error Model

Exact/exact-linear executable rows must carry `ERROR=exact` (or zero). Approximate/linearized/perturbative/asymptotic rows require a numeric `abs<=...` or `rel<=...` envelope. Approximate executable rows additionally require `ASSUME` to include `approx_allowed`. Per-row approximation bounds accumulate against the bundle error budget. Noise probabilities and statistical sampling uncertainty remain distinct from deterministic approximation error.
