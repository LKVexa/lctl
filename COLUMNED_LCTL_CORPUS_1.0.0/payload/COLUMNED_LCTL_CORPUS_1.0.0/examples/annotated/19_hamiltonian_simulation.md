# Annotated Example — 19_hamiltonian_simulation

**Unit:** `lctl.example.hamiltonian_simulation`  
**Rows:** 4  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.hamiltonian_simulation version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.hamiltonian_simulation proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 1 │ qs=H2^1;res="logical_qubits=1"
C0010 │ circuit │ RZ │ q[0] │ _ │ q[0] │ 0.6 │ _
M0011 │ hamiltonian │ HAMILTONIAN │ H_z │ _ │ _ │ 0.3*Z │ f=MODEL;t=Hamiltonian;basis=Pauli;asm=time_independent;res="terms=1";p=ham:single-z
T0012 │ continuum │ D13_FRECHET_LINEARIZE │ control_model │ _ │ discrete_control │ DN+R │ t=operator;basis=custom;r=linearized;asm="twice_Frechet_differentiable;local_validity";err="abs<=0.001";res=local_neighborhood;c=0.99;p=dlemma:D13
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `qreg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=1`, CONF `1.0`, PROOF `type:qreg`.

### C0010 — `RZ` → `RZ`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `0.6`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:rz`.

### M0011 — `HAMILTONIAN` → `HAMILTONIAN`

Lane `hamiltonian`; output `H_z`; control `_`; input `no explicit primary input`; argument `0.3*Z`. The compiler lowers this row to FACE `MODEL`, QSPACE `H`, TYPE `Hamiltonian`, BASIS `Pauli`, REGIME `exact`, ASSUME `time_independent`, ERROR `exact`, RESOURCE `terms=1`, CONF `1.0`, PROOF `ham:single-z`.

### T0012 — `D13_FRECHET_LINEARIZE` → `D13_FRECHET_LINEARIZE`

Lane `continuum`; output `control_model`; control `_`; input `discrete_control`; argument `DN+R`. The compiler lowers this row to FACE `TRANSFORM`, QSPACE `H`, TYPE `operator`, BASIS `custom`, REGIME `linearized`, ASSUME `twice_Frechet_differentiable;local_validity`, ERROR `abs<=0.001`, RESOURCE `local_neighborhood`, CONF `0.99`, PROOF `dlemma:D13`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.hamiltonian_simulation ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.hamiltonian_simulation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^1 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 1 ¦ qreg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=1 ¦ 1.0 ¦ type:qreg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ RZ ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ 0.6 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:rz)
(M0011 ¦ MODEL ¦ hamiltonian ¦ H ¦ HAMILTONIAN ¦ H_z ¦ _ ¦ _ ¦ _ ¦ 0.3*Z ¦ Hamiltonian ¦ Pauli ¦ exact ¦ time_independent ¦ exact ¦ terms=1 ¦ 1.0 ¦ ham:single-z)
(T0012 ¦ TRANSFORM ¦ continuum ¦ H ¦ D13_FRECHET_LINEARIZE ¦ control_model ¦ _ ¦ discrete_control ¦ _ ¦ DN+R ¦ operator ¦ custom ¦ linearized ¦ twice_Frechet_differentiable;local_validity ¦ abs<=0.001 ¦ local_neighborhood ¦ 0.99 ¦ dlemma:D13)
END QTUPLE
END QFRAME
@frame sha256:c804a19b2e7f48c9c01e4b9a938d7df1bf9f48861932db9b80e3b4216270d64c
END LCTL
@bundle sha256:8d32486fb638b1bde4865f756fa831518ee38bd702c3e8a26249521a951504e6
```
