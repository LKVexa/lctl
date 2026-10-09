# Lesson 25: Differentiation

## Objective
Parameter-shift/adjoint/finite-difference regimes

## Normative rule
Every derivative names its method and exactness status.

## Valid example
Use a verified row whose TYPE/REGIME/ASSUME/ERROR/PROOF columns state the mathematical contract.

## Invalid example
Hide a type, approximation, assumption, error, or unsupported lowering in prose or an opaque operand.

## Verification exercise
Identify the semantic type, exactness regime, assumptions, error envelope, resource impact, and proof identifier. Predict whether the reference verifier should accept or reject the row, then run `lctl verify`.

## Evidence boundary
Passing the local verifier proves conformance to the declared LCTL reference semantics. It does not create external hardware evidence, empirical calibration, or a proof beyond the stated assumptions.
