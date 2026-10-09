# Epistemic Type System

MSSL-S separates **data type**, **mathematical class**, **regime**, **assumptions**,
**error envelope**, and **confidence**.

## TYPE examples

`bitvec scalar vector matrix tensor operator distribution simplex graph state policy belief interval proof capability symbolic`

## CLASS examples

`arithmetic spectral operator variational probabilistic informational dynamical coupled graph learning control parallel combinatorial geometric exact_numeric`

Multiple class tags may be joined with `+`.

## REGIME

- `exact`
- `exact_linear`
- `linearized`
- `approximate`
- `stochastic`
- `structural_analogue`
- `nonlinear`
- `combinatorial`
- `no_faithful_form`
- `impossibility`
- `mixed`

## ASSUME

A compact conjunction of named assumptions, e.g.

`convex&smooth&psd`  
`normalised&independent`  
`symmetric&positive_definite`  
`boundary=neumann`

`none` means no extra assumption is asserted.

## ERROR

Examples:

`exact`  
`ulp<=1`  
`rel<=1e-12`  
`abs<=1e-6`  
`prob<=1e-4`  
`O(h^2)`  
`unknown`

## CONF

A decimal in `[0,1]`.

Confidence does not override regime or proof. A confidence of `1.0` on a
`no_faithful_form` record means confidence in the obstruction, not confidence in an
imaginary exact reduction.
