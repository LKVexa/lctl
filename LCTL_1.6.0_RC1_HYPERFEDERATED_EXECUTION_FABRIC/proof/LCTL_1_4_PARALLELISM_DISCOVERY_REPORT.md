# LCTL 1.4 Parallelism Discovery Report

Reference example: `examples/24_parallel_bell_pair_generation/program.lctl`.

- Executable causal tasks: **5**
- Exact partial-order width: **3**
- Topological levels: **2**
- Width method: `DILWORTH_MAXIMUM_MATCHING_EXACT`
- Runtime with four workers observed maximum concurrency: **3**
- Semantic result hash: `d6e579bc3e66790cf14b1982a705beab43ccbaf085cd755873cff91412cfc929`

The opportunity ledger distinguishes disjoint tensor/task candidates from pairs needing commutation proof. It does not infer physical-QPU simultaneity.
