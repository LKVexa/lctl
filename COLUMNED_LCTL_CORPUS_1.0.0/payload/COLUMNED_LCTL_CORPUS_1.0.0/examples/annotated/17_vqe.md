# Annotated Example — 17_vqe

**Unit:** `lctl.example.vqe`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.vqe version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.vqe proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2"
C0010 │ circuit │ RY │ q[0] │ _ │ q[0] │ 0.37 │ _
C0011 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0012 │ analysis │ EXPECT │ _ │ _ │ _ │ Z@q[0]*Z@q[1] │ t=expectation;basis=Pauli;asm=normalized_state;res="shots=0";p=vqe:objective
M0013 │ optimization │ OBJECTIVE │ energy │ _ │ theta │ <ZZ> │ f=MODEL;t=real;basis=Pauli;r=variational;asm="parameterized_ansatz;optimizer_external";err=sampling+optimizer;res="iterations=external";p=vqe:loop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `type:qreg`.

### C0010 — `RY` → `RY`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `0.37`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:ry`.

### C0011 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0012 — `EXPECT` → `EXPECT`

Lane `analysis`; output `_`; control `_`; input `no explicit primary input`; argument `Z@q[0]*Z@q[1]`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `expectation`, BASIS `Pauli`, REGIME `exact`, ASSUME `normalized_state`, ERROR `exact`, RESOURCE `shots=0`, CONF `1.0`, PROOF `vqe:objective`.

### M0013 — `OBJECTIVE` → `OBJECTIVE`

Lane `optimization`; output `energy`; control `_`; input `theta`; argument `<ZZ>`. The compiler lowers this row to FACE `MODEL`, QSPACE `H`, TYPE `real`, BASIS `Pauli`, REGIME `variational`, ASSUME `parameterized_ansatz;optimizer_external`, ERROR `sampling+optimizer`, RESOURCE `iterations=external`, CONF `1.0`, PROOF `vqe:loop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.vqe ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.vqe ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ type:qreg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ RY ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ 0.37 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:ry)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0012 ¦ CODE ¦ analysis ¦ H ¦ EXPECT ¦ _ ¦ _ ¦ _ ¦ _ ¦ Z@q[0]*Z@q[1] ¦ expectation ¦ Pauli ¦ exact ¦ normalized_state ¦ exact ¦ shots=0 ¦ 1.0 ¦ vqe:objective)
(M0013 ¦ MODEL ¦ optimization ¦ H ¦ OBJECTIVE ¦ energy ¦ _ ¦ theta ¦ _ ¦ <ZZ> ¦ real ¦ Pauli ¦ variational ¦ parameterized_ansatz;optimizer_external ¦ sampling+optimizer ¦ iterations=external ¦ 1.0 ¦ vqe:loop)
END QTUPLE
END QFRAME
@frame sha256:d6533e98d9f74e9d7b1674cc3621ef0be6cc9ec86157f3b6f9c4cd57213c3b3f
END LCTL
@bundle sha256:3b39d1300cf8eae7d66017ab4beabbe3ef73af22a0097df8166812d182536a89
```
