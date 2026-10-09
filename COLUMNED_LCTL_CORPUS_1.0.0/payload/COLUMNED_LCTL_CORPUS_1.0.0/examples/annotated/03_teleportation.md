# Annotated Example — 03_teleportation

**Unit:** `lctl.example.teleportation`  
**Rows:** 12  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.teleportation version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.teleportation proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 3 │ qs=H2^3;res="logical_qubits=3"
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=H2^3;res="classical_bits=2"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ H │ q[1] │ _ │ q[1] │ _ │ _
C0012 │ circuit │ CX │ q[2] │ q[1] │ q[2] │ _ │ _
C0013 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0014 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0015 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
C0016 │ measure │ MZ │ c[1] │ _ │ q[1] │ _ │ res="shots=runtime"
C0017 │ feedback │ IF │ q[2] │ c[1] │ 1›X │ _ │ t=hybrid_control;r=stochastic;asm=measurement_conditioned;err=sampling;res="dynamic=1";p=teleport:x
C0018 │ feedback │ IF │ q[2] │ c[0] │ 1›Z │ _ │ t=hybrid_control;r=stochastic;asm=measurement_conditioned;err=sampling;res="dynamic=1";p=teleport:z
C0019 │ analysis │ EXPECT │ _ │ _ │ _ │ X@q[2] │ t=expectation;basis=X;p=teleport:fidelity-proxy
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `qreg[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=3`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `bit[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `H` → `H`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0012 — `CX` → `CX`

Lane `circuit`; output `q[2]`; control `q[1]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0013 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0014 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0015 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0016 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0017 — `IF` → `CLASSICAL_IF`

Lane `feedback`; output `q[2]`; control `c[1]`; input `1›X`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `hybrid_control`, BASIS `computational`, REGIME `stochastic`, ASSUME `measurement_conditioned`, ERROR `sampling`, RESOURCE `dynamic=1`, CONF `1.0`, PROOF `teleport:x`.

### C0018 — `IF` → `CLASSICAL_IF`

Lane `feedback`; output `q[2]`; control `c[0]`; input `1›Z`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `hybrid_control`, BASIS `computational`, REGIME `stochastic`, ASSUME `measurement_conditioned`, ERROR `sampling`, RESOURCE `dynamic=1`, CONF `1.0`, PROOF `teleport:z`.

### C0019 — `EXPECT` → `EXPECT`

Lane `analysis`; output `_`; control `_`; input `no explicit primary input`; argument `X@q[2]`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `expectation`, BASIS `X`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `teleport:fidelity-proxy`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.teleportation ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.teleportation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^3 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 3 ¦ qreg[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=3 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^3 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ bit[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[2] ¦ q[1] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0013 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0014 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0015 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0016 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0017 ¦ CODE ¦ feedback ¦ H ¦ CLASSICAL_IF ¦ q[2] ¦ c[1] ¦ 1 ¦ X ¦ _ ¦ hybrid_control ¦ computational ¦ stochastic ¦ measurement_conditioned ¦ sampling ¦ dynamic=1 ¦ 1.0 ¦ teleport:x)
(C0018 ¦ CODE ¦ feedback ¦ H ¦ CLASSICAL_IF ¦ q[2] ¦ c[0] ¦ 1 ¦ Z ¦ _ ¦ hybrid_control ¦ computational ¦ stochastic ¦ measurement_conditioned ¦ sampling ¦ dynamic=1 ¦ 1.0 ¦ teleport:z)
(C0019 ¦ CODE ¦ analysis ¦ H ¦ EXPECT ¦ _ ¦ _ ¦ _ ¦ _ ¦ X@q[2] ¦ expectation ¦ X ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ teleport:fidelity-proxy)
END QTUPLE
END QFRAME
@frame sha256:08fcadf019fb2b264913a532495815a0e00c516b9a9c2e57b8ead79bc0537102
END LCTL
@bundle sha256:e95444fc9aa7e4a02f531cacebe6c67499422708f6c8dc555e6739137b298bde
```
