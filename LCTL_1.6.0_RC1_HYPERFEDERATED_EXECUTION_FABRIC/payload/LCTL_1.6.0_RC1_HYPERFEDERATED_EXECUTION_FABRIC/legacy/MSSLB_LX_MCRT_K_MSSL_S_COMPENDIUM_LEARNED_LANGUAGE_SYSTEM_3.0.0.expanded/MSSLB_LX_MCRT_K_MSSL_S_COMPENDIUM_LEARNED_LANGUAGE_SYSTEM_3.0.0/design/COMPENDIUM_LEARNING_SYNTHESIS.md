# Compendium Learning Synthesis

The 20 chapters contributed a common design lesson: **representation must preserve
the mathematical regime rather than pretending every model has the same kind of
truth**.

Across the extracted 1205 semantic records, the recurring status families
are:

| Regime | Count |
|---|---:|
| exact_linear | 1062 |
| linearized | 50 |
| structural_analogue | 55 |
| no_faithful_form | 38 |
| other exact/mixed | 0 |

That observation becomes a language rule:

> An implementation must not silently promote an approximation, analogy,
> combinatorial obstruction or no-faithful-form result into an exact executable
> claim.

## Cross-field semantic lessons

1. **Operators over values.** Matrices, linear operators, Jacobians, tensors,
   projections, graph operators and transforms are first-class semantic types.
2. **Regime is part of type.** Exact, linearized, stochastic, structural,
   nonlinear/combinatorial and rejected regimes are visible.
3. **Assumptions travel with claims.** Convexity, smoothness, symmetry, PSD,
   normalisation, boundary conditions, independence and locality belong in source.
4. **Error is semantic.** Numerical conditioning and approximation error are not
   comments; they are contracts.
5. **Learning requires evidence.** A learned relation is admitted through Q1-Q6.
6. **Coupling is explicit.** Cross-subsystem influence is represented as a relation,
   not hidden side effects.
7. **Information has structure.** Entropy, divergence, confidence and channel
   capacity inform knowledge state.
8. **Dynamics are state transitions with invariants.** Conservation, stability,
   equilibrium and control constraints remain inspectable.
9. **Parallelism has cost and impossibility boundaries.** Communication and
   consensus limitations are modelled without creating a network backend.
10. **Retraction/replay matters.** A high-level semantic claim must be able to state
    how it lowers, retracts or deliberately refuses lowering.
