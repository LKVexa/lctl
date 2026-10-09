# Annotated Example — 53_native_distributed_bell_fabric

**Unit:** `lctl.example.native_distributed_bell_fabric`  
**Rows:** 14  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.native_distributed_bell_fabric version=1.3.0-rc1 profile=lctl.quantum.parallel.distributed.native entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-native:1.3
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.native_distributed_bell_fabric proof=lctl-native:1.3
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 4 │ qs=H2^4;res="logical_qubits=4";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
P0001 │ meta │ REG │ _ │ _ │ _ │ name=bell_pairs;lanes=2 │ p=lctl-native:1.3
C0010 │ lane0 │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ lane1 │ H │ q[2] │ _ │ q[2] │ _ │ _
C0020 │ lane0 │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0021 │ lane1 │ CX │ q[3] │ q[2] │ q[3] │ _ │ _
X1001 │ meta │ PART │ _ │ _ │ _ │ id=P0;lane=lane0;class=logical │ t=partition;asm=finite_dimension;p=lctl-native:1.3
X1002 │ meta │ PART │ _ │ _ │ _ │ id=P1;lane=lane1;class=logical │ t=partition;asm=finite_dimension;p=lctl-native:1.3
T1001 │ meta │ NODE │ _ │ _ │ _ │ id=N0;class=emulated_cpu │ t=topology_node;p=lctl-native:1.3
T1002 │ meta │ NODE │ _ │ _ │ _ │ id=N1;class=emulated_cpu │ t=topology_node;p=lctl-native:1.3
T1003 │ meta │ LK │ _ │ _ │ _ │ src=N0;dst=N1;type=classical;latency=1;bandwidth=1000000 │ t=topology_link;p=lctl-native:1.3
M1001 │ meta │ PERSISTENT │ _ │ _ │ _ │ tier=ssd;role=classical_numeric_spill │ p=lctl-native:1.3
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `4`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^4`, TYPE `qreg[4]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=4`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### P0001 — `REG` → `REGION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `name=bell_pairs;lanes=2`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-native:1.3`.

### C0010 — `H` → `H`

Lane `lane0`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `H` → `H`

Lane `lane1`; output `q[2]`; control `_`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0020 — `CX` → `CX`

Lane `lane0`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0021 — `CX` → `CX`

Lane `lane1`; output `q[3]`; control `q[2]`; input `q[3]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### X1001 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P0;lane=lane0;class=logical`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `partition`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-native:1.3`.

### X1002 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P1;lane=lane1;class=logical`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `partition`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-native:1.3`.

### T1001 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N0;class=emulated_cpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology_node`, BASIS `computational`, REGIME `exact`, ASSUME `local_reference`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-native:1.3`.

### T1002 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N1;class=emulated_cpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology_node`, BASIS `computational`, REGIME `exact`, ASSUME `local_reference`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-native:1.3`.

### T1003 — `LK` → `LINK`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=N0;dst=N1;type=classical;latency=1;bandwidth=1000000`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology_link`, BASIS `computational`, REGIME `exact`, ASSUME `local_reference`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-native:1.3`.

### M1001 — `PERSISTENT` → `PERSISTENT`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `tier=ssd;role=classical_numeric_spill`. The compiler lowers this row to FACE `MEMORY`, QSPACE `H`, TYPE `memory`, BASIS `computational`, REGIME `exact`, ASSUME `classical_storage_only`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-native:1.3`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.native_distributed_bell_fabric ¦ 1.3.0-rc1 ¦ lctl.quantum.parallel.distributed.native ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-native:1.3)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.native_distributed_bell_fabric ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-native:1.3)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^4 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 4 ¦ qreg[4] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=4 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(P0001 ¦ PARALLEL ¦ meta ¦ H ¦ REGION ¦ _ ¦ _ ¦ _ ¦ _ ¦ name=bell_pairs;lanes=2 ¦ parallel ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-native:1.3)
(C0010 ¦ CODE ¦ lane0 ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ lane1 ¦ H ¦ H ¦ q[2] ¦ _ ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0020 ¦ CODE ¦ lane0 ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0021 ¦ CODE ¦ lane1 ¦ H ¦ CX ¦ q[3] ¦ q[2] ¦ q[3] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(X1001 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P0;lane=lane0;class=logical ¦ partition ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-native:1.3)
(X1002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P1;lane=lane1;class=logical ¦ partition ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-native:1.3)
(T1001 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N0;class=emulated_cpu ¦ topology_node ¦ computational ¦ exact ¦ local_reference ¦ exact ¦ _ ¦ 1.0 ¦ lctl-native:1.3)
(T1002 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N1;class=emulated_cpu ¦ topology_node ¦ computational ¦ exact ¦ local_reference ¦ exact ¦ _ ¦ 1.0 ¦ lctl-native:1.3)
(T1003 ¦ TOPOLOGY ¦ meta ¦ H ¦ LINK ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=N0;dst=N1;type=classical;latency=1;bandwidth=1000000 ¦ topology_link ¦ computational ¦ exact ¦ local_reference ¦ exact ¦ _ ¦ 1.0 ¦ lctl-native:1.3)
(M1001 ¦ MEMORY ¦ meta ¦ H ¦ PERSISTENT ¦ _ ¦ _ ¦ _ ¦ _ ¦ tier=ssd;role=classical_numeric_spill ¦ memory ¦ computational ¦ exact ¦ classical_storage_only ¦ exact ¦ _ ¦ 1.0 ¦ lctl-native:1.3)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:9798bbcb41bfbd007d06e24c70fc4628027efa162dfa65ae888c81d19dd910aa
END LCTL
@bundle sha256:018f4e7fb79643785872d1057a6ec874961a98ec6b9ddf10ce1a66c09ecff81d
```
