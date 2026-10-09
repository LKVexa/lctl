# Registers and Declarations


Use `Q`/`QREG` for quantum registers, `C`/`CREG` for classical registers, and `QB`/`QUBIT` for an individual qubit declaration.

Typical declarations put the register name in OUT and the size in ARG. QSPACE and resource annotations may be supplied in META when they are meaningful to the unit.

```text
D1│alloc│Q│q│_│_│4│qs=H2^4;res="logical_qubits=4"
D2│alloc│C│c│_│_│4│qs=C;res="classical_bits=4"
```

The declaration FACE determines a type default such as `qreg[4]` or `bit[4]`.
