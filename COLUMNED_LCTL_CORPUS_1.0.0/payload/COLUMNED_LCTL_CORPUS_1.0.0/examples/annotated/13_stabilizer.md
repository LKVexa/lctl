# Annotated Example — 13_stabilizer

**Unit:** `lctl.example.stabilizer`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.stabilizer version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.stabilizer proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 3 │ qs=H2^3;res="logical_qubits=3"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0012 │ circuit │ CX │ q[2] │ q[0] │ q[2] │ _ │ _
M0013 │ stabilizer │ STABILIZER │ ghz │ _ │ _ │ XXX;ZZI;IZZ │ f=MODEL;t=stabilizer;basis=Pauli;asm=commuting_generators;res="rank=3";p=stabilizer:ghz
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `qreg[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=3`, CONF `1.0`, PROOF `type:qreg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0012 — `CX` → `CX`

Lane `circuit`; output `q[2]`; control `q[0]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### M0013 — `STABILIZER` → `STABILIZER`

Lane `stabilizer`; output `ghz`; control `_`; input `no explicit primary input`; argument `XXX;ZZI;IZZ`. The compiler lowers this row to FACE `MODEL`, QSPACE `H`, TYPE `stabilizer`, BASIS `Pauli`, REGIME `exact`, ASSUME `commuting_generators`, ERROR `exact`, RESOURCE `rank=3`, CONF `1.0`, PROOF `stabilizer:ghz`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.stabilizer ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.stabilizer ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^3 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 3 ¦ qreg[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=3 ¦ 1.0 ¦ type:qreg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[2] ¦ q[0] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(M0013 ¦ MODEL ¦ stabilizer ¦ H ¦ STABILIZER ¦ ghz ¦ _ ¦ _ ¦ _ ¦ XXX;ZZI;IZZ ¦ stabilizer ¦ Pauli ¦ exact ¦ commuting_generators ¦ exact ¦ rank=3 ¦ 1.0 ¦ stabilizer:ghz)
END QTUPLE
END QFRAME
@frame sha256:a4499e05697ebaa38f545de061c1034a72bb7ee5c0f7f20c54511d01906224eb
END LCTL
@bundle sha256:a08b159b166370693e2737543f8f163d1cb378cfc55496412faa30d2ff4943e5
```
