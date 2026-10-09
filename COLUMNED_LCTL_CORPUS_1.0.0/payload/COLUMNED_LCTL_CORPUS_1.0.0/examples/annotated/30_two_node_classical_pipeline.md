# Annotated Example — 30_two_node_classical_pipeline

**Unit:** `lctl.example.two_node_classical_pipeline`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.two_node_classical_pipeline version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.two_node_classical_pipeline proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
T0001 │ meta │ NODE │ _ │ _ │ _ │ id=N0;class=emulated_cpu │ asm=finite_dimension;p=lctl-dist:1.2
T0002 │ meta │ NODE │ _ │ _ │ _ │ id=N1;class=emulated_cpu │ asm=finite_dimension;p=lctl-dist:1.2
T0003 │ meta │ LK │ _ │ _ │ _ │ src=N0;dst=N1;type=classical;latency=2;bandwidth=1000 │ asm=finite_dimension;p=lctl-dist:1.2
X0004 │ meta │ MSG │ _ │ _ │ _ │ src=N0;dst=N1;bytes=256;latency=2.256 │ asm=finite_dimension;p=lctl-dist:1.2
P0005 │ meta │ REG │ _ │ _ │ _ │ pipeline=2;stages=producer,consumer │ p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### T0001 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N0;class=emulated_cpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0002 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N1;class=emulated_cpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0003 — `LK` → `LINK`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=N0;dst=N1;type=classical;latency=2;bandwidth=1000`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0004 — `MSG` → `MESSAGE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=N0;dst=N1;bytes=256;latency=2.256`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### P0005 — `REG` → `REGION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `pipeline=2;stages=producer,consumer`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.two_node_classical_pipeline ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.two_node_classical_pipeline ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(T0001 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N0;class=emulated_cpu ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0002 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N1;class=emulated_cpu ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0003 ¦ TOPOLOGY ¦ meta ¦ H ¦ LINK ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=N0;dst=N1;type=classical;latency=2;bandwidth=1000 ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0004 ¦ DISTRIBUTED ¦ meta ¦ H ¦ MESSAGE ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=N0;dst=N1;bytes=256;latency=2.256 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(P0005 ¦ PARALLEL ¦ meta ¦ H ¦ REGION ¦ _ ¦ _ ¦ _ ¦ _ ¦ pipeline=2;stages=producer,consumer ¦ parallel ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:7310e404aa82ccca950e6d1090e7d6fff7f37c98ddcaaae6eb7d22bae7fe6a6f
END LCTL
@bundle sha256:1c8b331231820f9405e39a2bc9fd957606001261f210ab3b21f9c3e931a8bd67
```
