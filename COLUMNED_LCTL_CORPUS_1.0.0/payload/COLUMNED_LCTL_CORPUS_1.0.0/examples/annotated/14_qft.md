# Annotated Example — 14_qft

**Unit:** `lctl.example.qft`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.qft version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.qft proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 3 │ qs=H2^3;res="logical_qubits=3"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ CPHASE │ q[0] │ q[1] │ q[0] │ 1.5707963267948966 │ _
C0012 │ circuit │ CPHASE │ q[0] │ q[2] │ q[0] │ 0.7853981633974483 │ _
C0013 │ circuit │ H │ q[1] │ _ │ q[1] │ _ │ _
C0014 │ circuit │ CPHASE │ q[1] │ q[2] │ q[1] │ 1.5707963267948966 │ _
C0015 │ circuit │ H │ q[2] │ _ │ q[2] │ _ │ _
C0016 │ circuit │ SWAP │ q[2] │ q[0] │ q[2] │ _ │ _
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `qreg[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=3`, CONF `1.0`, PROOF `type:qreg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `CPHASE` → `CPHASE`

Lane `circuit`; output `q[0]`; control `q[1]`; input `q[0]`; argument `1.5707963267948966`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cphase`.

### C0012 — `CPHASE` → `CPHASE`

Lane `circuit`; output `q[0]`; control `q[2]`; input `q[0]`; argument `0.7853981633974483`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cphase`.

### C0013 — `H` → `H`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0014 — `CPHASE` → `CPHASE`

Lane `circuit`; output `q[1]`; control `q[2]`; input `q[1]`; argument `1.5707963267948966`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cphase`.

### C0015 — `H` → `H`

Lane `circuit`; output `q[2]`; control `_`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0016 — `SWAP` → `SWAP`

Lane `circuit`; output `q[2]`; control `q[0]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:swap`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.qft ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.qft ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^3 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 3 ¦ qreg[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=3 ¦ 1.0 ¦ type:qreg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ CPHASE ¦ q[0] ¦ q[1] ¦ q[0] ¦ _ ¦ 1.5707963267948966 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cphase)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CPHASE ¦ q[0] ¦ q[2] ¦ q[0] ¦ _ ¦ 0.7853981633974483 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cphase)
(C0013 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0014 ¦ CODE ¦ circuit ¦ H ¦ CPHASE ¦ q[1] ¦ q[2] ¦ q[1] ¦ _ ¦ 1.5707963267948966 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cphase)
(C0015 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[2] ¦ _ ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0016 ¦ CODE ¦ circuit ¦ H ¦ SWAP ¦ q[2] ¦ q[0] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:swap)
END QTUPLE
END QFRAME
@frame sha256:5c42c1e479a78783a9950c022bd56e7b8f08056bd9c58a50153a0ddaca9b1b97
END LCTL
@bundle sha256:8cf762173469422d6bd4cdc51882661e83a2bde87e9ed4e15f3700f11c4ace9b
```
