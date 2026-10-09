# LCTL 1.4 Adaptive Execution Mesh — Normative Reference Specification

## Status
Development-candidate reference profile. LCTL/1.0–1.3 source compatibility is preserved by the retained runtimes. The 1.4 mesh is an execution/planning layer rather than a claim of physical QPU authority.

## Semantic rules

1. Parallelism is admitted only when causal, ownership, measurement, commutation, resource, and target constraints permit it.
2. Unknown quantum state has one authoritative ownership lineage and is never replicated for load balancing or resilience.
3. Classical task replication/consistency is a separate semantic domain.
4. Numerical state sharding is a classical representation optimization.
5. Logical quantum routes/protocols do not cross the physical quantum boundary.
6. Unsupported or unresolved transformations serialize or fail closed.
7. Deterministic modes emit replayable hashes/traces.

## Reference execution layers

`source → SES → partial order → opportunity/commutation analysis → partition → placement → routing → schedule → task/numerical/protocol executor → evidence`.

## Exactness
The partial-order width algorithm is exact for the parsed DAG via Dilworth maximum matching. Other optimizer/planner components explicitly identify reference/partial status where no global optimality proof exists.
