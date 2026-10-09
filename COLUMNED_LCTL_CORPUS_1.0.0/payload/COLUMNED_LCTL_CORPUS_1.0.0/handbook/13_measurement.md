# Measurement and Classical Feedback


`MZ`, `MX`, and `MY` expand to basis-specific measurements. The compiler automatically infers measurement result type, basis, stochastic regime, Born-rule/destructive-value assumptions, and sampling error semantics.

```text
M1│measure│MZ│c[0]│_│q[0]│_│res="shots=runtime"
```

`IF`/`CLASSICAL_IF` represents classical conditional control. Cross-node feedback belongs to the PROTOCOL family via `CLASSICAL_FEEDBACK` when the program is distributed.
