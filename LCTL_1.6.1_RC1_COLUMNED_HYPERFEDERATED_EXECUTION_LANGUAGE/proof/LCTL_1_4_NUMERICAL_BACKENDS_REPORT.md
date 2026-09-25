# LCTL 1.4 Numerical Backends Report

Implemented reference backends include the preserved multi-process statevector engine, process-sharded bounded density evolution, stabilizer batch execution, SSD-backed out-of-core state simulation, and a tensor-network contraction planner.

Density 2.0 reference evidence:

- status: **PASS**
- trace: `0.9999999999999996`
- trace error: `4.440892098500626e-16`
- Hermiticity error: `0.0`
- single-worker hash match: `True`

Out-of-core execution is explicitly labeled `SSD_BACKED_CLASSICAL_SIMULATION`. Tensor-network support is a planner only and is not represented as a complete numerical backend.
