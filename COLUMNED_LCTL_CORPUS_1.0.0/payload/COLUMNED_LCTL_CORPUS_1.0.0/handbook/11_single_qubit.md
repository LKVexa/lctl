# Single-Qubit Operations


The CODE family includes `I X Y Z H S SDG T TDG RX RY RZ PHASE` and preparation/reset operators. A normal single-qubit gate places the target in OUT and IN.

```text
C1│q0│H│q[0]│_│q[0]│_│_
C2│q0│RZ│q[0]│_│q[0]│theta│_
```

ARG carries canonical PARAM. Use symbolic parameters when the surrounding program and execution profile provide them; do not hide parameters in META when they belong in PARAM.
