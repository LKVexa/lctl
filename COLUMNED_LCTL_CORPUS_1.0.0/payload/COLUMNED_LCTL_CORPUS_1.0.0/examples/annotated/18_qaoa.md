# Annotated Example — 18_qaoa

**Unit:** `lctl.example.qaoa`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.qaoa version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.qaoa proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ H │ q[1] │ _ │ q[1] │ _ │ _
C0012 │ circuit │ CPHASE │ q[1] │ q[0] │ q[1] │ 0.8 │ _
C0013 │ circuit │ RX │ q[0] │ _ │ q[0] │ 0.4 │ _
C0014 │ circuit │ RX │ q[1] │ _ │ q[1] │ 0.4 │ _
C0015 │ analysis │ EXPECT │ _ │ _ │ _ │ Z@q[0]*Z@q[1] │ t=expectation;basis=Pauli;asm=normalized_state;res="shots=0";p=qaoa:cost
M0016 │ optimization │ OBJECTIVE │ cut_cost │ _ │ gamma,beta │ <ZZ> │ f=MODEL;t=real;basis=Pauli;r=variational;asm=classical_outer_loop;err=sampling+optimizer;res="iterations=external";p=qaoa:loop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `type:qreg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `H` → `H`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0012 — `CPHASE` → `CPHASE`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `0.8`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cphase`.

### C0013 — `RX` → `RX`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `0.4`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:rx`.

### C0014 — `RX` → `RX`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `0.4`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:rx`.

### C0015 — `EXPECT` → `EXPECT`

Lane `analysis`; output `_`; control `_`; input `no explicit primary input`; argument `Z@q[0]*Z@q[1]`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `expectation`, BASIS `Pauli`, REGIME `exact`, ASSUME `normalized_state`, ERROR `exact`, RESOURCE `shots=0`, CONF `1.0`, PROOF `qaoa:cost`.

### M0016 — `OBJECTIVE` → `OBJECTIVE`

Lane `optimization`; output `cut_cost`; control `_`; input `gamma,beta`; argument `<ZZ>`. The compiler lowers this row to FACE `MODEL`, QSPACE `H`, TYPE `real`, BASIS `Pauli`, REGIME `variational`, ASSUME `classical_outer_loop`, ERROR `sampling+optimizer`, RESOURCE `iterations=external`, CONF `1.0`, PROOF `qaoa:loop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.qaoa ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.qaoa ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ type:qreg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CPHASE ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ 0.8 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cphase)
(C0013 ¦ CODE ¦ circuit ¦ H ¦ RX ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ 0.4 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:rx)
(C0014 ¦ CODE ¦ circuit ¦ H ¦ RX ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ 0.4 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:rx)
(C0015 ¦ CODE ¦ analysis ¦ H ¦ EXPECT ¦ _ ¦ _ ¦ _ ¦ _ ¦ Z@q[0]*Z@q[1] ¦ expectation ¦ Pauli ¦ exact ¦ normalized_state ¦ exact ¦ shots=0 ¦ 1.0 ¦ qaoa:cost)
(M0016 ¦ MODEL ¦ optimization ¦ H ¦ OBJECTIVE ¦ cut_cost ¦ _ ¦ gamma,beta ¦ _ ¦ <ZZ> ¦ real ¦ Pauli ¦ variational ¦ classical_outer_loop ¦ sampling+optimizer ¦ iterations=external ¦ 1.0 ¦ qaoa:loop)
END QTUPLE
END QFRAME
@frame sha256:fc1ad588632a719190ae5eb3bad91bc9e557a97a33d73aceba5f78948f0d2236
END LCTL
@bundle sha256:c75b63855f6daca686b03029ceb42c91885559d54addafbb3ea01967abf278b1
```
