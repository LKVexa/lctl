# Annotated Example — 02_ghz_state

**Unit:** `lctl.example.ghz_state`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.ghz_state version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.ghz_state proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 3 │ qs=H2^3;res="logical_qubits=3"
D0002 │ alloc │ C │ c │ _ │ _ │ 3 │ qs=H2^3;res="classical_bits=3"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0012 │ circuit │ CX │ q[2] │ q[0] │ q[2] │ _ │ _
C0013 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
C0014 │ measure │ MZ │ c[1] │ _ │ q[1] │ _ │ res="shots=runtime"
C0015 │ measure │ MZ │ c[2] │ _ │ q[2] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `qreg[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=3`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `bit[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=3`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0012 — `CX` → `CX`

Lane `circuit`; output `q[2]`; control `q[0]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0013 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0014 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0015 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[2]`; control `_`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.ghz_state ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.ghz_state ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^3 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 3 ¦ qreg[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=3 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^3 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 3 ¦ bit[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=3 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[2] ¦ q[0] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0013 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0014 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0015 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[2] ¦ _ ¦ q[2] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:e7de09d87fb20f1426425910a07bf505ac8707a366a04063ef200018207845a8
END LCTL
@bundle sha256:1ef717bca7ea1be65024d568b3439d50ae3a5ab28fa0f49f1aa1f475d95b7a9e
```
