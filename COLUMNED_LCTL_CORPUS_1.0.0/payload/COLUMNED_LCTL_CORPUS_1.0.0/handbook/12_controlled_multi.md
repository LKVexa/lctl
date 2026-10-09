# Controlled and Multi-Qubit Operations


For controlled operations, CTRL is the explicit control and OUT/IN identify the target. Examples in the verified corpus use `CX`, `CZ`, `CPHASE`, `CCX`, and `SWAP`.

```text
C2│lane0│CX│q[1]│q[0]│q[1]│_│_
```

When semantics need two primary non-control operands, use `A›B` in IN. Keep control semantics in CTRL rather than encoding them into ARG.
