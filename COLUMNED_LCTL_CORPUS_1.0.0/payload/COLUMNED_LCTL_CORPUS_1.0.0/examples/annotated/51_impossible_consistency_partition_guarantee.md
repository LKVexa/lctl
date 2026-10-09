# Annotated Example — 51_impossible_consistency_partition_guarantee

**Unit:** `lctl.example.impossible_consistency_partition_guarantee`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.impossible_consistency_partition_guarantee version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.impossible_consistency_partition_guarantee proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
P0001 │ meta │ CONSENSUS_BOUNDARY │ _ │ _ │ _ │ result=IMPOSSIBLE;reason=partition_tradeoff │ r=no_faithful_form;asm=distributed_partition;err=exact;p=lctl-dist:1.2
X0002 │ meta │ CONSISTENCY_SCOPE │ _ │ _ │ _ │ partitioned=true;exact_all=false;cap_forbidden=true │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### P0001 — `CONSENSUS_BOUNDARY` → `CONSENSUS_BOUNDARY`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `result=IMPOSSIBLE;reason=partition_tradeoff`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `no_faithful_form`, ASSUME `distributed_partition`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `CONSISTENCY_SCOPE` → `CONSISTENCY_SCOPE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `partitioned=true;exact_all=false;cap_forbidden=true`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.impossible_consistency_partition_guarantee ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.impossible_consistency_partition_guarantee ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(P0001 ¦ PARALLEL ¦ meta ¦ H ¦ CONSENSUS_BOUNDARY ¦ _ ¦ _ ¦ _ ¦ _ ¦ result=IMPOSSIBLE;reason=partition_tradeoff ¦ parallel ¦ computational ¦ no_faithful_form ¦ distributed_partition ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ CONSISTENCY_SCOPE ¦ _ ¦ _ ¦ _ ¦ _ ¦ partitioned=true;exact_all=false;cap_forbidden=true ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:dc4a58871c637d17ddbf8f89abc2fd578ecde3750c3f2ba56b35be796775fe39
END LCTL
@bundle sha256:55d40375a50801973000f26a6759b76cb62f3f0b9f92a51ad436e2a2e90d7ceb
```
