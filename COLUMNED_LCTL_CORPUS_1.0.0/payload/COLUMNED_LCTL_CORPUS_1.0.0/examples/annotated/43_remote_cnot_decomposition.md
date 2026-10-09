# Annotated Example — 43_remote_cnot_decomposition

**Unit:** `lctl.example.remote_cnot_decomposition`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.remote_cnot_decomposition version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.remote_cnot_decomposition proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
X0001 │ meta │ ROUTE │ _ │ _ │ _ │ id=RQ0;src=Q0;dst=Q1;kind=quantum │ asm=finite_dimension;p=lctl-dist:1.2
Q0002 │ meta │ RCX │ _ │ _ │ _ │ route=RQ0;target_supported=true;control=q[0];target=q[1] │ p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### X0001 — `ROUTE` → `ROUTE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=RQ0;src=Q0;dst=Q1;kind=quantum`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### Q0002 — `RCX` → `REMOTE_CNOT`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `route=RQ0;target_supported=true;control=q[0];target=q[1]`. The compiler lowers this row to FACE `PROTOCOL`, QSPACE `H`, TYPE `protocol`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.remote_cnot_decomposition ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.remote_cnot_decomposition ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(X0001 ¦ DISTRIBUTED ¦ meta ¦ H ¦ ROUTE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=RQ0;src=Q0;dst=Q1;kind=quantum ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(Q0002 ¦ PROTOCOL ¦ meta ¦ H ¦ REMOTE_CNOT ¦ _ ¦ _ ¦ _ ¦ _ ¦ route=RQ0;target_supported=true;control=q[0];target=q[1] ¦ protocol ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:80f58f5394d01ccd4d24cf4b903444eb88c3f1d46f6b96cae5eca7b3d07df289
END LCTL
@bundle sha256:e9162d009c8e0a892bf3e5801801184fb5ac1380601aec432f5d6913e0ebeed5
```
