# Annotated Example — 05_single_qubit_rotations

**Unit:** `lctl.example.single_qubit_rotations`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.single_qubit_rotations version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.single_qubit_rotations proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 1 │ qs=H2^1;res="logical_qubits=1"
D0002 │ alloc │ C │ c │ _ │ _ │ 1 │ qs=H2^1;res="classical_bits=1"
C0010 │ circuit │ RX │ q[0] │ _ │ q[0] │ 0.4 │ _
C0011 │ circuit │ RY │ q[0] │ _ │ q[0] │ 0.7 │ _
C0012 │ circuit │ RZ │ q[0] │ _ │ q[0] │ -0.2 │ _
C0013 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `qreg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=1`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `bit[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=1`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `RX` → `RX`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `0.4`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:rx`.

### C0011 — `RY` → `RY`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `0.7`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:ry`.

### C0012 — `RZ` → `RZ`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `-0.2`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:rz`.

### C0013 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.single_qubit_rotations ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.single_qubit_rotations ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^1 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 1 ¦ qreg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=1 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^1 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 1 ¦ bit[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=1 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ RX ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ 0.4 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:rx)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ RY ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ 0.7 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:ry)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ RZ ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ -0.2 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:rz)
(C0013 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:edf26bb0a1db49a7d30c1a8ab99467f58e44b6395db6a6d013c0378773414b6c
END LCTL
@bundle sha256:3145aca5233dacef0c5ad0994c0473919a8c54d900436ca91c3c11cbfa21f2a8
```
