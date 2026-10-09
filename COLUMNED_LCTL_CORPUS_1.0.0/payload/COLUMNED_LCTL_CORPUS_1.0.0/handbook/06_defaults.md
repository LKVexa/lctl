# Defaults and Inheritance

# Default and Inference Rules

The current compiler resolves each compact row in this order:

1. Expand the OP alias.
2. Infer FACE unless META supplies `f=`/`face=`.
3. Split IN into A and optional B at `›`.
4. Use ARG, or an embedded `OP(argument)` payload when ARG is blank.
5. Resolve QSPACE from META, then `@defaults`, then `H`.
6. Infer TYPE from FACE/OP unless META overrides it.
7. Infer BASIS, REGIME, ASSUME, ERROR from OP/face; an operation-specific non-generic default outranks `@defaults`.
8. Resolve RESOURCE from META, default `_`.
9. Resolve CONF from META, then `@defaults`, then `1.0`.
10. Infer PROOF unless META overrides it.

This means measurement rows automatically acquire the measurement basis, stochastic regime, Born-rule/destructive-value assumption, sampling error model, and a measurement proof label.
