# Annotated Example — 07_basis_measurement

**Unit:** `lctl.example.basis_measurement`  
**Rows:** 4  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.basis_measurement version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.basis_measurement proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 1 │ qs=H2^1;res="logical_qubits=1"
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=H2^1;res="classical_bits=2"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ measure │ MX │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `qreg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=1`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `bit[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `MX` → `MEASURE_X`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `X`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:x`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.basis_measurement ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.basis_measurement ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^1 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 1 ¦ qreg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=1 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^1 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ bit[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ measure ¦ H ¦ MEASURE_X ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ X ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:x)
END QTUPLE
END QFRAME
@frame sha256:e1769e3f5c468123dcd78652fed92fd349e7b37d85cd646c68069ee8aea56ed7
END LCTL
@bundle sha256:3c40b31a2e5e72f462208def046cbf11d25cd97df819953a56f8e1e8e1c1942d
```
