# Lesson 4: Qubit Ownership

## Objective
Affine ownership and no cloning

## Normative rule
Unknown quantum state cannot be copied or aliased.

## Valid example
Use a verified row whose TYPE/REGIME/ASSUME/ERROR/PROOF columns state the mathematical contract.

## Invalid example
Hide a type, approximation, assumption, error, or unsupported lowering in prose or an opaque operand.

## Verification exercise
Identify the semantic type, exactness regime, assumptions, error envelope, resource impact, and proof identifier. Predict whether the reference verifier should accept or reject the row, then run `lctl verify`.

## Evidence boundary
Passing the local verifier proves conformance to the declared LCTL reference semantics. It does not create external hardware evidence, empirical calibration, or a proof beyond the stated assumptions.
