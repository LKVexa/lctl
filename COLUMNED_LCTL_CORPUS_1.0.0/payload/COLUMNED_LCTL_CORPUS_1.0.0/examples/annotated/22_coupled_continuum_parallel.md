# Annotated Example — 22_coupled_continuum_parallel

**Unit:** `lctl.example.coupled_continuum_parallel`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.coupled_continuum_parallel version=1.1.0 profile=lctl.quantum.parallel entry=F1000:C1001 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8;parallel=logical proof=parallel-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F1000 parent=ROOT module=lctl.example.coupled_parallel_model proof=parallel:1.1
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D1001 │ alloc │ Q │ q │ _ │ _ │ 1 │ qs=H2;res="logical_qubits=1";p=parallel:1.1
F1001 │ field0 │ TENSOR │ _ │ _ │ _ │ rank=2;symmetry=symmetric;domain=Omega │ t=tensor[2];p=CM22:tensor
F1002 │ field0 │ LINEARIZATION │ _ │ _ │ _ │ finite_model->tangent;validity=declared │ t=linearization;r=linearized;asm="approx_allowed;small_perturbation";err="abs<=0.001";p=CM22:linearization
K1001 │ couple │ BLOCK_COUPLING │ _ │ _ │ _ │ A,B;C,D │ t=coupled_system;p=CP23:block
K1002 │ couple │ RECIPROCITY │ _ │ _ │ _ │ B=C^T │ t=coupling_block;p=CP23:onsager
A1001 │ proof │ COUPLING_RECIPROCITY │ _ │ _ │ _ │ 0,1;2,0||0,2;1,0 │ t=assertion;p=CP23:reciprocity
P1001 │ meta │ DOMAIN_DECOMP │ _ │ _ │ _ │ subdomains=2;interface=explicit │ t=parallel_region;p="CM22:balance;PDC80:surface-volume"
C1001 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=parallel:nop
@end
```

## Row-by-row semantics

### D1001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2`, TYPE `qreg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=1`, CONF `1.0`, PROOF `parallel:1.1`.

### F1001 — `TENSOR` → `TENSOR`

Lane `field0`; output `_`; control `_`; input `no explicit primary input`; argument `rank=2;symmetry=symmetric;domain=Omega`. The compiler lowers this row to FACE `FIELD`, QSPACE `H`, TYPE `tensor[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `CM22:tensor`.

### F1002 — `LINEARIZATION` → `LINEARIZATION`

Lane `field0`; output `_`; control `_`; input `no explicit primary input`; argument `finite_model->tangent;validity=declared`. The compiler lowers this row to FACE `FIELD`, QSPACE `H`, TYPE `linearization`, BASIS `computational`, REGIME `linearized`, ASSUME `approx_allowed;small_perturbation`, ERROR `abs<=0.001`, RESOURCE `_`, CONF `1.0`, PROOF `CM22:linearization`.

### K1001 — `BLOCK_COUPLING` → `BLOCK_COUPLING`

Lane `couple`; output `_`; control `_`; input `no explicit primary input`; argument `A,B;C,D`. The compiler lowers this row to FACE `COUPLING`, QSPACE `H`, TYPE `coupled_system`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `CP23:block`.

### K1002 — `RECIPROCITY` → `RECIPROCITY`

Lane `couple`; output `_`; control `_`; input `no explicit primary input`; argument `B=C^T`. The compiler lowers this row to FACE `COUPLING`, QSPACE `H`, TYPE `coupling_block`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `CP23:onsager`.

### A1001 — `COUPLING_RECIPROCITY` → `COUPLING_RECIPROCITY`

Lane `proof`; output `_`; control `_`; input `no explicit primary input`; argument `0,1;2,0||0,2;1,0`. The compiler lowers this row to FACE `ASSERT`, QSPACE `H`, TYPE `assertion`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `CP23:reciprocity`.

### P1001 — `DOMAIN_DECOMP` → `DOMAIN_DECOMP`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `subdomains=2;interface=explicit`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel_region`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `CM22:balance;PDC80:surface-volume`.

### C1001 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `parallel:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.coupled_continuum_parallel ¦ 1.1.0 ¦ lctl.quantum.parallel ¦ F1000:C1001 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8;parallel=logical ¦ q1-q6 ¦ sealed ¦ parallel-proof)
QFRAME/1.0 BEGIN F1000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F1000 ¦ ROOT ¦ lctl.example.coupled_parallel_model ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ parallel:1.1)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D1001 ¦ DECL ¦ alloc ¦ H2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 1 ¦ qreg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=1 ¦ 1.0 ¦ parallel:1.1)
(F1001 ¦ FIELD ¦ field0 ¦ H ¦ TENSOR ¦ _ ¦ _ ¦ _ ¦ _ ¦ rank=2;symmetry=symmetric;domain=Omega ¦ tensor[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ CM22:tensor)
(F1002 ¦ FIELD ¦ field0 ¦ H ¦ LINEARIZATION ¦ _ ¦ _ ¦ _ ¦ _ ¦ finite_model->tangent;validity=declared ¦ linearization ¦ computational ¦ linearized ¦ approx_allowed;small_perturbation ¦ abs<=0.001 ¦ _ ¦ 1.0 ¦ CM22:linearization)
(K1001 ¦ COUPLING ¦ couple ¦ H ¦ BLOCK_COUPLING ¦ _ ¦ _ ¦ _ ¦ _ ¦ A,B;C,D ¦ coupled_system ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ CP23:block)
(K1002 ¦ COUPLING ¦ couple ¦ H ¦ RECIPROCITY ¦ _ ¦ _ ¦ _ ¦ _ ¦ B=C^T ¦ coupling_block ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ CP23:onsager)
(A1001 ¦ ASSERT ¦ proof ¦ H ¦ COUPLING_RECIPROCITY ¦ _ ¦ _ ¦ _ ¦ _ ¦ 0,1;2,0||0,2;1,0 ¦ assertion ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ CP23:reciprocity)
(P1001 ¦ PARALLEL ¦ meta ¦ H ¦ DOMAIN_DECOMP ¦ _ ¦ _ ¦ _ ¦ _ ¦ subdomains=2;interface=explicit ¦ parallel_region ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ CM22:balance;PDC80:surface-volume)
(C1001 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ parallel:nop)
END QTUPLE
END QFRAME
@frame sha256:1cfc64f4bc3015c863949ba8b984ca5e1bae3ff246cd2b5469a5eb2a950b3395
END LCTL
@bundle sha256:36745501ca6b8702d45d0650d36438ee5882e41061c5ae8f05c6718ac50b9a87
```
