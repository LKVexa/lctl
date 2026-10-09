# Annotated Example — 20_measurement_feedback

**Unit:** `lctl.example.measurement_feedback`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.measurement_feedback version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.measurement_feedback proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2"
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=H2^2;res="classical_bits=2"
C0010 │ circuit │ X │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
C0012 │ feedback │ IF │ q[1] │ c[0] │ 1›X │ _ │ t=hybrid_control;r=stochastic;asm=measurement_conditioned;err=sampling;res="dynamic=1";p=feedback:x
C0013 │ measure │ MZ │ c[1] │ _ │ q[1] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `bit[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `X` → `X`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0011 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0012 — `IF` → `CLASSICAL_IF`

Lane `feedback`; output `q[1]`; control `c[0]`; input `1›X`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `hybrid_control`, BASIS `computational`, REGIME `stochastic`, ASSUME `measurement_conditioned`, ERROR `sampling`, RESOURCE `dynamic=1`, CONF `1.0`, PROOF `feedback:x`.

### C0013 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.measurement_feedback ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.measurement_feedback ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^2 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ bit[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0011 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0012 ¦ CODE ¦ feedback ¦ H ¦ CLASSICAL_IF ¦ q[1] ¦ c[0] ¦ 1 ¦ X ¦ _ ¦ hybrid_control ¦ computational ¦ stochastic ¦ measurement_conditioned ¦ sampling ¦ dynamic=1 ¦ 1.0 ¦ feedback:x)
(C0013 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:4bd6a5831de6c928ef7348f2370c702ee108eb2d238e3f433b3add4b7c3ad698
END LCTL
@bundle sha256:d8e3d13c3e363307fa02e7f5e455729a4538aa01dcc28f79ba578fd715c1f173
```
