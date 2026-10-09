# Annotated Example — 11_depolarizing_channel

**Unit:** `lctl.example.depolarizing_channel`  
**Rows:** 4  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.depolarizing_channel version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.depolarizing_channel proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 1 │ qs=H2^1;res="logical_qubits=1"
D0002 │ alloc │ C │ c │ _ │ _ │ 1 │ qs=H2^1;res="classical_bits=1"
C0010 │ noise │ DEPOLARIZE │ q[0] │ _ │ q[0] │ 0.30 │ t=channel;asm=CPTP;err="channel:p=0.30";res="density=1";p=kraus:depolarize
C0011 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `qreg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=1`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `bit[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=1`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `DEPOLARIZE` → `DEPOLARIZE`

Lane `noise`; output `q[0]`; control `_`; input `q[0]`; argument `0.30`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `channel`, BASIS `computational`, REGIME `noisy`, ASSUME `CPTP`, ERROR `channel:p=0.30`, RESOURCE `density=1`, CONF `1.0`, PROOF `kraus:depolarize`.

### C0011 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.depolarizing_channel ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.depolarizing_channel ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^1 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 1 ¦ qreg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=1 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^1 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 1 ¦ bit[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=1 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ noise ¦ H ¦ DEPOLARIZE ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ 0.30 ¦ channel ¦ computational ¦ noisy ¦ CPTP ¦ channel:p=0.30 ¦ density=1 ¦ 1.0 ¦ kraus:depolarize)
(C0011 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:256de7a72af9d5f2478c8e0aeae6d281498d33ad5077ce10168e3e9ff5e4ebb0
END LCTL
@bundle sha256:2b8eef68177740b36a04a3f8df23375752785a9bf21f4919e1adf2161a2b0262
```
