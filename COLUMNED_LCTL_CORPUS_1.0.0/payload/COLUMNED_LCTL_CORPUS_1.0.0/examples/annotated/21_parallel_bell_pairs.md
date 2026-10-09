# Annotated Example — 21_parallel_bell_pairs

**Unit:** `lctl.example.parallel_bell_pairs`  
**Rows:** 10  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.parallel_bell_pairs version=1.1.0 profile=lctl.quantum.parallel entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8;parallel=logical proof=parallel-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.parallel_bell_pairs proof=parallel:1.1
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 4 │ qs=H2^4;res="logical_qubits=4";p=parallel:1.1
P0001 │ meta │ REG │ _ │ _ │ _ │ bell_pairs;lanes=2 │ t=parallel_region;res="W=4;D=2";p=parallel:1.1
C0010 │ lane0 │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ lane1 │ H │ q[2] │ _ │ q[2] │ _ │ _
@end
@frame id=F0001 parent=F0000 module=lctl.example.parallel_bell_pairs tick=1 prev=F0000 proof=parallel:1.1
ID│LANE│OP│OUT│CTRL│IN│ARG│META
P0010 │ meta │ DEP │ _ │ _ │ _ │ C0010->C0020;C0011->C0021 │ t=dependency;p=work-span:exact
C0020 │ lane0 │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0021 │ lane1 │ CX │ q[3] │ q[2] │ q[3] │ _ │ _
@end
@frame id=F0002 parent=F0001 module=lctl.example.parallel_bell_pairs tick=2 prev=F0001 proof=parallel:1.1
ID│LANE│OP│OUT│CTRL│IN│ARG│META
P0020 │ meta │ CM │ _ │ _ │ _ │ alpha=0;beta=0;local_only=true │ t=communication_model;p=PDC80:communication
P0021 │ meta │ MEMORY_TIER │ _ │ _ │ _ │ statevector=RAM;artifacts=SSD │ t=memory_tier;p=PDC80:memory
C0030 │ sync │ BAR │ _ │ _ │ _ │ _ │ t=barrier;p=parallel:barrier
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `4`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^4`, TYPE `qreg[4]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=4`, CONF `1.0`, PROOF `parallel:1.1`.

### P0001 — `REG` → `REGION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `bell_pairs;lanes=2`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel_region`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `W=4;D=2`, CONF `1.0`, PROOF `parallel:1.1`.

### C0010 — `H` → `H`

Lane `lane0`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `H` → `H`

Lane `lane1`; output `q[2]`; control `_`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### P0010 — `DEP` → `DEPENDENCY`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `C0010->C0020;C0011->C0021`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `dependency`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `work-span:exact`.

### C0020 — `CX` → `CX`

Lane `lane0`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0021 — `CX` → `CX`

Lane `lane1`; output `q[3]`; control `q[2]`; input `q[3]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### P0020 — `CM` → `COMM_MODEL`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `alpha=0;beta=0;local_only=true`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `communication_model`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `PDC80:communication`.

### P0021 — `MEMORY_TIER` → `MEMORY_TIER`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `statevector=RAM;artifacts=SSD`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `memory_tier`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `PDC80:memory`.

### C0030 — `BAR` → `BARRIER`

Lane `sync`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `barrier`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `parallel:barrier`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.parallel_bell_pairs ¦ 1.1.0 ¦ lctl.quantum.parallel ¦ F0000:C0010 ¦ 3 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8;parallel=logical ¦ q1-q6 ¦ sealed ¦ parallel-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.parallel_bell_pairs ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ parallel:1.1)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^4 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 4 ¦ qreg[4] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=4 ¦ 1.0 ¦ parallel:1.1)
(P0001 ¦ PARALLEL ¦ meta ¦ H ¦ REGION ¦ _ ¦ _ ¦ _ ¦ _ ¦ bell_pairs;lanes=2 ¦ parallel_region ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ W=4;D=2 ¦ 1.0 ¦ parallel:1.1)
(C0010 ¦ CODE ¦ lane0 ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ lane1 ¦ H ¦ H ¦ q[2] ¦ _ ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
END QTUPLE
END QFRAME
@frame sha256:2322501242e91ed1ea04ac3a05aa8da741a39d8bea167e7c91ce63a704fe26bf
QFRAME/1.0 BEGIN F0001
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0001 ¦ F0000 ¦ lctl.example.parallel_bell_pairs ¦ H ¦ 1 ¦ F0000 ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ parallel:1.1)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(P0010 ¦ PARALLEL ¦ meta ¦ H ¦ DEPENDENCY ¦ _ ¦ _ ¦ _ ¦ _ ¦ C0010->C0020;C0011->C0021 ¦ dependency ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ work-span:exact)
(C0020 ¦ CODE ¦ lane0 ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0021 ¦ CODE ¦ lane1 ¦ H ¦ CX ¦ q[3] ¦ q[2] ¦ q[3] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
END QTUPLE
END QFRAME
@frame sha256:e8c6f9f411bcceba4a63c3ee553fdbc5515396d452a76af200a42e4c0ce43646
QFRAME/1.0 BEGIN F0002
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0002 ¦ F0001 ¦ lctl.example.parallel_bell_pairs ¦ H ¦ 2 ¦ F0001 ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ parallel:1.1)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(P0020 ¦ PARALLEL ¦ meta ¦ H ¦ COMM_MODEL ¦ _ ¦ _ ¦ _ ¦ _ ¦ alpha=0;beta=0;local_only=true ¦ communication_model ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ PDC80:communication)
(P0021 ¦ PARALLEL ¦ meta ¦ H ¦ MEMORY_TIER ¦ _ ¦ _ ¦ _ ¦ _ ¦ statevector=RAM;artifacts=SSD ¦ memory_tier ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ PDC80:memory)
(C0030 ¦ CODE ¦ sync ¦ H ¦ BARRIER ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ barrier ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ parallel:barrier)
END QTUPLE
END QFRAME
@frame sha256:c6b3039492dfcd8e05bfcce5fe4d57e727dba872cd29612237f78b2e8f5d666e
END LCTL
@bundle sha256:ddf388f41d95c46f3f61c17a1ed6d72aa1720f9048eb9e28a1b6d9e604c5469d
```
