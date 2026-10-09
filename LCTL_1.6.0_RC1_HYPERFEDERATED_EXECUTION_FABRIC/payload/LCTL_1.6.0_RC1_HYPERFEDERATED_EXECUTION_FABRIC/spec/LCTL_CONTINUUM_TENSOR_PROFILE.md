# LCTL Continuum and Tensor Profile
## Derived design authority: Compendium Field 22

LCTL adopts the following language lessons from continuum mechanics:

1. **Tensors are semantic objects.** State, control, calibration, noise, field, and coupling models may carry tensor rank, symmetry, definiteness, invariants, frame/configuration, and units.
2. **Decomposition is first-class.** Symmetric/skew, volumetric/deviatoric, spectral, block, and reference/current decompositions must preserve reconstruction identities and assumptions.
3. **Linearization is explicit.** Small-perturbation or tangent models are typed `linearized` and carry a validity region/remainder. Finite/nonlinear structure may not be relabeled exact-linear.
4. **Balance laws are constraints.** Conservation-style model rows carry domain, flux/source terms, and boundary/interface assumptions; a scheduler may partition a domain only if interface/reduction semantics preserve the declared balance.
5. **Admissibility is semantic.** Positivity, positive definiteness, normalization, orientation/non-inversion, and energy/dissipation conditions are verifier-visible where applicable.
6. **Moving-domain semantics are explicit.** A field over a changing domain must declare transport/mapping semantics rather than silently treating coordinates as fixed.
7. **Energy/variation is proof material.** Gradient/Hessian/tangent structure can justify stable local solvers, while the underlying nonlinear model remains distinct from its tangent.
8. **Memory kernels remain nonlocal.** Convolution/history-dependent models cannot be compiled as memoryless local updates without an admitted approximation.

These constructs are primarily model/proof semantics. They do not change the exact Hilbert/operator core of quantum computation.
