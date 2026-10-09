# Minimal Unit Skeleton


A minimum useful unit starts with `LCTLC/1.0`, an `@unit`, optional `@defaults`, one `@frame`, the exact column header, one or more rows, and `@end`.

```text
LCTLC/1.0
@unit id=demo version=1.6.1 profile=native
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=demo
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D1│alloc│Q│q│_│_│1│res=logical_qubits=1
C1│q0│H│q[0]│_│q[0]│_│_
@end
```

Whitespace around separators is optional. The compiler also accepts `¦`, tab, or `|` row separators, but `│` is the corpus style.
