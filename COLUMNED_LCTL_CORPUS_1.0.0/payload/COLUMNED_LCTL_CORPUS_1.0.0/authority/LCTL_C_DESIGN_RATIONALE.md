# Why LCTL-C Is Leaner and Meaner

The previous source format made every row restate up to eighteen semantic cells even when ten of those cells were predictable from the operation or stable across an entire frame. That was rigorous but noisy.

LCTL-C preserves the rigor and removes the repetition.

## Leaner

- 8 source columns instead of 18 canonical row cells.
- Common quantum semantics are inferred from the operation.
- Stable policy is inherited from `@defaults`.
- Short aliases remove recurring long operator names.
- Bundle and frame declarations become compact directives.
- The 53-example corpus is about 52% smaller in source bytes after columnization.

## Meaner

- Row IDs stay mandatory.
- Ownership and no-cloning checks remain canonical.
- Measurement still lowers to explicit stochastic/Born semantics.
- Approximation/error semantics still lower into canonical cells.
- Distributed and physical claims still pass through the evidence firewall.
- A compact row cannot silently weaken a canonical invariant.
- Every compact unit can be materialized into a sealed canonical artifact for audit and replay.

The result is less syntax, not less semantics.
