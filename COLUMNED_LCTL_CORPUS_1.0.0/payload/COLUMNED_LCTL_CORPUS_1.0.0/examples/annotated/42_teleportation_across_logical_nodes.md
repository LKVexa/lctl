# Annotated Example — 42_teleportation_across_logical_nodes

**Unit:** `lctl.example.teleportation_across_logical_nodes`  
**Rows:** 9  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.teleportation_across_logical_nodes version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.teleportation_across_logical_nodes proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
T0001 │ meta │ NODE │ _ │ _ │ _ │ id=Q0;class=qpu_partition │ asm=finite_dimension;p=lctl-dist:1.2
T0002 │ meta │ NODE │ _ │ _ │ _ │ id=Q1;class=qpu_partition │ asm=finite_dimension;p=lctl-dist:1.2
T0003 │ meta │ LK │ _ │ _ │ _ │ src=Q0;dst=Q1;type=quantum;latency=5;fidelity=0.98;setup=4;herald=1;success=0.8 │ asm=finite_dimension;p=lctl-dist:1.2
T0004 │ meta │ LK │ _ │ _ │ _ │ src=Q0;dst=Q1;type=classical;latency=1;bandwidth=1000 │ asm=finite_dimension;p=lctl-dist:1.2
Q0005 │ meta │ EPR │ _ │ _ │ _ │ count=1;link=QL0 │ p=lctl-dist:1.2
Q0006 │ meta │ TEL │ _ │ _ │ _ │ src=Q0;dst=Q1;ebit=E0;ebit_count=1;classical=CL0 │ p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### T0001 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=Q0;class=qpu_partition`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0002 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=Q1;class=qpu_partition`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0003 — `LK` → `LINK`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=Q0;dst=Q1;type=quantum;latency=5;fidelity=0.98;setup=4;herald=1;success=0.8`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0004 — `LK` → `LINK`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=Q0;dst=Q1;type=classical;latency=1;bandwidth=1000`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### Q0005 — `EPR` → `EPR_RESERVE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `count=1;link=QL0`. The compiler lowers this row to FACE `PROTOCOL`, QSPACE `H`, TYPE `protocol`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### Q0006 — `TEL` → `TELEPORT`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=Q0;dst=Q1;ebit=E0;ebit_count=1;classical=CL0`. The compiler lowers this row to FACE `PROTOCOL`, QSPACE `H`, TYPE `protocol`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.teleportation_across_logical_nodes ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.teleportation_across_logical_nodes ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(T0001 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=Q0;class=qpu_partition ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0002 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=Q1;class=qpu_partition ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0003 ¦ TOPOLOGY ¦ meta ¦ H ¦ LINK ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=Q0;dst=Q1;type=quantum;latency=5;fidelity=0.98;setup=4;herald=1;success=0.8 ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0004 ¦ TOPOLOGY ¦ meta ¦ H ¦ LINK ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=Q0;dst=Q1;type=classical;latency=1;bandwidth=1000 ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(Q0005 ¦ PROTOCOL ¦ meta ¦ H ¦ EPR_RESERVE ¦ _ ¦ _ ¦ _ ¦ _ ¦ count=1;link=QL0 ¦ protocol ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(Q0006 ¦ PROTOCOL ¦ meta ¦ H ¦ TELEPORT ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=Q0;dst=Q1;ebit=E0;ebit_count=1;classical=CL0 ¦ protocol ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:105111c509bc0e44c09d58a0408e50a3ff661c0ec339fcc16cec275f651c204f
END LCTL
@bundle sha256:4a696e3f91d55d0bb08857229de3fab4e6e45dc8da370b3762b7ed04f5cff6b0
```
