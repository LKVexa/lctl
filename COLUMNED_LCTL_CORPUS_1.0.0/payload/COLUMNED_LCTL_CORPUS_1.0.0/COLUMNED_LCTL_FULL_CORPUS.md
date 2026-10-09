# COLUMNED LCTL FULL CORPUS 1.0.0

This single-file edition concatenates the human-readable corpus. Machine datasets remain separate JSONL files in the package.


---

# Columned LCTL Corpus Orientation


This corpus teaches **LCTL-C 1.0**, the compact eight-column source dialect introduced for LCTL 1.6.1-RC1. Its governing principle is: **write only what changes; infer or inherit what is stable; lower everything before authority is claimed.**

The compact dialect is additive. Canonical LCTL remains the verification and execution authority. Treat `.lctlc` as the human/model authoring surface and the lowered `.lctl` unit as the sealed semantic form.

Recommended path: chapters 01–10 for basic writing, 11–20 for quantum and distributed semantics, 21–27 for proof/recovery/style, then the annotated examples and workbook.


---

# Mental Model: Rows, Columns, Frames, Authority


A Columned LCTL program is a unit containing one or more frames. A frame contains executable or declarative rows. Every row has eight source cells:

`ID │ LANE │ OP │ OUT │ CTRL │ IN │ ARG │ META`

The source row is not the final semantic record. Compilation reconstructs ten additional semantic cells and emits the canonical 18-cell row. This makes compact source readable without discarding explicit semantic authority.

Think of the eight columns as the **changing surface** and the canonical cells as the **semantic closure** of the row.


---

# Minimal Unit Skeleton


A minimum useful unit starts with `LCTLC/1.0`, an `@unit`, optional `@defaults`, one `@frame`, the exact column header, one or more rows, and `@end`.

```text
LCTLC/1.0
@unit id=demo version=1.6.1 profile=native
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=demo
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D1│alloc│Q│q│_│_│1│res=logical_qubits=1
C1│q0│H│q[0]│_│q[0]│_│_
@end
```

Whitespace around separators is optional. The compiler also accepts `¦`, tab, or `|` row separators, but `│` is the corpus style.


---

# The Eight Source Columns

## `ID`

Stable row identity; it is required and is never inferred.

## `LANE`

Semantic or scheduling lane; underscore means no explicit lane.

## `OP`

Operation or compact alias; normally determines canonical FACE.

## `OUT`

Output or destination.

## `CTRL`

Explicit control operand or classical condition source.

## `IN`

Primary canonical A operand, optionally packed with B as A›B.

## `ARG`

Canonical PARAM payload.

## `META`

Semantic overrides not safely inferred or inherited.


---

# IN Packing: A and B


`IN` maps to canonical operand `A`. When a second operand is required, write `A›B`. At most two operands may be packed this way. `_` means no explicit operand.

Examples:

- `q[0]` → `A=q[0]`, `B=_`
- `left›right` → `A=left`, `B=right`
- `_` → both blank

Do not use commas for A/B packing; commas are intentionally left available to mathematical expressions and parameter payloads.


---

# Compact Operation Aliases

Aliases shorten spelling only; semantics come from the expanded operation.

- `Q` → `QREG`
- `C` → `CREG`
- `QB` → `QUBIT`
- `MZ` → `MEASURE_Z`
- `MX` → `MEASURE_X`
- `MY` → `MEASURE_Y`
- `P0` → `PREP0`
- `P1` → `PREP1`
- `P+` → `PREP_PLUS`
- `P-` → `PREP_MINUS`
- `BAR` → `BARRIER`
- `.` → `NOP`
- `IF` → `CLASSICAL_IF`
- `REG` → `REGION`
- `DEP` → `DEPENDENCY`
- `CM` → `COMM_MODEL`
- `PART` → `PARTITION`
- `MSG` → `MESSAGE`
- `QBIND` → `QSTATE_BIND`
- `QMOVE` → `QSTATE_MOVE`
- `LK` → `LINK`
- `EPR` → `EPR_RESERVE`
- `TEL` → `TELEPORT`
- `RCX` → `REMOTE_CNOT`
- `ESWAP` → `ENTANGLEMENT_SWAP`

Canonical names remain legal and are preferable when shortening would reduce clarity.


---

# Defaults and Inheritance

# Default and Inference Rules

The current compiler resolves each compact row in this order:

1. Expand the OP alias.
2. Infer FACE unless META supplies `f=`/`face=`.
3. Split IN into A and optional B at `›`.
4. Use ARG, or an embedded `OP(argument)` payload when ARG is blank.
5. Resolve QSPACE from META, then `@defaults`, then `H`.
6. Infer TYPE from FACE/OP unless META overrides it.
7. Infer BASIS, REGIME, ASSUME, ERROR from OP/face; an operation-specific non-generic default outranks `@defaults`.
8. Resolve RESOURCE from META, default `_`.
9. Resolve CONF from META, then `@defaults`, then `1.0`.
10. Infer PROOF unless META overrides it.

This means measurement rows automatically acquire the measurement basis, stochastic regime, Born-rule/destructive-value assumption, sampling error model, and a measurement proof label.


---

# META Overrides

# META Keys

`META` carries only semantic overrides that cannot be inferred or inherited.

| Compact key | Long key | Canonical target |
|---|---|---|
| `f` | `face` | FACE |
| `qs` | `qspace` | QSPACE |
| `t` | `type` | TYPE |
| `basis` | `basis` | BASIS |
| `r` | `regime` | REGIME |
| `asm` | `assume` | ASSUME |
| `err` | `error` | ERROR |
| `res` | `resource` | RESOURCE |
| `c` | `conf` | CONF |
| `p` | `proof` | PROOF |

Use semicolons between entries. Quote values containing semicolons, spaces, quotes, or embedded `=`.


---

# FACE Inference and Precedence


OP normally determines FACE. The compiler checks families in a fixed order: DECL, CODE, PARALLEL, COUPLING, FIELD, PROTOCOL, FAILURE, MEMORY, TRANSFORM, ASSERT, TOPOLOGY, DISTRIBUTED.

That order matters when a name belongs to more than one catalog. For example, `NODE` appears in both topology and distributed vocabularies; current compiler precedence resolves it to TOPOLOGY. When an operation is ambiguous or outside the known catalog, supply `f=FACE` in META.

Never guess a FACE for an unknown operation in generated source. Make the override explicit.


---

# Canonical 18-Cell Lowering


Each compact row lowers to:

`ROW ¦ FACE ¦ LANE ¦ QSPACE ¦ OP ¦ OUT ¦ CTRL ¦ A ¦ B ¦ PARAM ¦ TYPE ¦ BASIS ¦ REGIME ¦ ASSUME ¦ ERROR ¦ RESOURCE ¦ CONF ¦ PROOF`

IDs and row order are preserved. Frames and bundles receive deterministic SHA-256 seals. The canonical verifier runs after lowering, so compact syntax cannot claim authority merely because it parsed.

Use `column-compile` when you need the sealed canonical artifact and `column-verify` when you need compact-source verification.


---

# Registers and Declarations


Use `Q`/`QREG` for quantum registers, `C`/`CREG` for classical registers, and `QB`/`QUBIT` for an individual qubit declaration.

Typical declarations put the register name in OUT and the size in ARG. QSPACE and resource annotations may be supplied in META when they are meaningful to the unit.

```text
D1│alloc│Q│q│_│_│4│qs=H2^4;res="logical_qubits=4"
D2│alloc│C│c│_│_│4│qs=C;res="classical_bits=4"
```

The declaration FACE determines a type default such as `qreg[4]` or `bit[4]`.


---

# Single-Qubit Operations


The CODE family includes `I X Y Z H S SDG T TDG RX RY RZ PHASE` and preparation/reset operators. A normal single-qubit gate places the target in OUT and IN.

```text
C1│q0│H│q[0]│_│q[0]│_│_
C2│q0│RZ│q[0]│_│q[0]│theta│_
```

ARG carries canonical PARAM. Use symbolic parameters when the surrounding program and execution profile provide them; do not hide parameters in META when they belong in PARAM.


---

# Controlled and Multi-Qubit Operations


For controlled operations, CTRL is the explicit control and OUT/IN identify the target. Examples in the verified corpus use `CX`, `CZ`, `CPHASE`, `CCX`, and `SWAP`.

```text
C2│lane0│CX│q[1]│q[0]│q[1]│_│_
```

When semantics need two primary non-control operands, use `A›B` in IN. Keep control semantics in CTRL rather than encoding them into ARG.


---

# Measurement and Classical Feedback


`MZ`, `MX`, and `MY` expand to basis-specific measurements. The compiler automatically infers measurement result type, basis, stochastic regime, Born-rule/destructive-value assumptions, and sampling error semantics.

```text
M1│measure│MZ│c[0]│_│q[0]│_│res="shots=runtime"
```

`IF`/`CLASSICAL_IF` represents classical conditional control. Cross-node feedback belongs to the PROTOCOL family via `CLASSICAL_FEEDBACK` when the program is distributed.


---

# Noise and Approximation


Noise operators include `DEPOLARIZE`, `DEPHASE`, `BIT_FLIP`, `PHASE_FLIP`, `AMPLITUDE_DAMP`, and `PHASE_DAMP`. They infer the `noisy` regime.

Keep model parameters in ARG and use META only for semantic overrides such as a non-default error envelope, assumption, or proof. A noisy reference model is not physical hardware evidence.


---

# Parallel Regions and Lanes


Parallel structure is expressed with lanes plus PARALLEL-family metadata such as `REGION`, `LANE_BIND`, `DEPENDENCY`, `COMM_MODEL`, `COLLECTIVE`, `DOMAIN_DECOMP`, and `OVERLAP`.

The verified parallel Bell example places independent gates in `lane0` and `lane1` and declares a two-lane region. Lanes expose concurrency; they do not by themselves prove that multiple workers executed concurrently. Runtime evidence must establish actual parallel execution.


---

# Distributed Partitions, Shards, Routes, Messages


Distributed metadata includes `PARTITION`, `SHARD`, `ROUTE`, `MESSAGE`, `EPOCH`, `EVENT`, `FUTURE`, placement, consistency, checkpoint, provenance, and ownership operations.

Use ARG for structured key/value payloads such as `id=P0;lane=lane0;class=logical`. META remains the home for canonical semantic overrides such as type, assumption, or proof.

A process-local or loopback example must remain labeled as such; distribution claims require execution evidence.


---

# Topology Modeling


Topology operations model nodes, links, CPUs, GPUs, QPUs, memory/NUMA domains, concurrency, crosstalk, coherence, and calibration.

```text
T1│meta│NODE│_│_│_│id=N0;class=emulated_cpu│_
T2│meta│LK│_│_│_│src=N0;dst=N1;type=classical;latency=1;bandwidth=1000│_
```

Topology declarations describe a model or discovered topology. Physical-device authority requires separate authenticated evidence.


---

# Logical Quantum Protocols


The PROTOCOL family includes entanglement-link operations, EPR reservation/release, teleportation, remote control/CNOT/measure, classical feedback, entanglement swapping, purification, heralding, synchronized quantum clocks, and quantum-channel send/receive.

Compact aliases include `EPR`, `TEL`, `RCX`, and `ESWAP`.

Protocol rows describe protocol semantics. They do not convert logical/reference execution into authenticated physical QPU execution.


---

# Quantum Ownership Discipline


Columned syntax inherits the canonical ownership rules. Unknown quantum state cannot be cloned for convenience. Use `QBIND`/`QSTATE_BIND` to establish ownership handles and `QMOVE`/`QSTATE_MOVE` for admitted ownership migration.

`QSTATE_COPY` and `SERIALIZE_QSTATE` may exist in the distributed vocabulary so the verifier can identify or reject forbidden actions; their presence in the grammar is not permission to copy an unknown physical state.

Keep ownership claims explicit and fail closed when ownership is ambiguous.


---

# Memory and Data Movement


The MEMORY family contains register/local/cache/NUMA/device/host/remote/persistent/capacity/move concepts. The default assumption for MEMORY rows is `classical_storage_only`.

This distinction is deliberate: classically representable simulator shards and metadata can be stored, checkpointed, replicated, or moved according to policy; arbitrary unknown physical quantum state cannot be serialized as ordinary bytes.


---

# Failure and Recovery Semantics


Failure operators identify node/process/link failure, timeout, packet loss, quantum-link failure, calibration expiry, unavailable QPU, coherence limit, resource exhaustion, target drift, scheduler deadlock, ownership ambiguity, and checkpoints.

Use failure rows to make failure states explicit. Recovery claims require runtime evidence showing replay/restart/reroute/checkpoint restoration, not merely a logged failure label.


---

# Field, Coupling, and Transform Vocabulary


LCTL also carries FIELD, COUPLING, and TRANSFORM faces. FIELD includes `FIELD`, `TENSOR`, `BALANCE`, `CONSTITUTIVE`, `LINEARIZATION`, `CONTINUUM_LIMIT`, and `DOMAIN`. COUPLING includes block coupling, reciprocity, coupling strength, monolithic/staggered forms, interface, and unresolved coupling. TRANSFORM includes the D1–D14 transform family plus theta transforms.

These rows use the same eight-column surface and canonical lowering rules. When specialized mathematical assumptions are necessary, make them explicit in META rather than relying on generic finite-dimensional defaults.


---

# Assertions and Evidence Boundaries


ASSERT operations include mathematical assertions such as unitary, Hermitian, density, POVM, Kraus, probability, tensor dimension, topology/coupling seals, work-span, schedule seals, and physical claims.

An assertion row expresses a condition to be checked. It does not prove itself. The proof/evidence pipeline must validate the assertion before release status is elevated.


---

# Profiles, Targets, and Execution Claims


The compact lowerer maps short profiles `core`, `parallel`, `distributed`, and `native` to canonical profile names; explicit canonical profile names are also accepted.

Unit fields such as target/backend/network/replay/precision/error budget/resource ceiling remain part of the sealed bundle. Keep `network=deny` and nonphysical target declarations when no authenticated external authority exists.


---

# Determinism and Replay


The same compact source and compiler generation produce deterministic inferred cells, preserve row order and IDs, and generate deterministic frame/bundle seals. Runtime determinism still depends on the selected execution mode.

Do not confuse deterministic lowering with deterministic distributed scheduling. The former is a compiler property; the latter requires scheduler/replay evidence.


---

# Columned LCTL Style Guide


Prefer the following style:

- one stable row ID per semantic action;
- short but meaningful lanes (`alloc`, `circuit`, `measure`, `lane0`, `meta`);
- compact aliases for very common operations, canonical names for rare/ambiguous ones;
- `_` for absence rather than empty cells;
- ARG for operation payload, META for semantic overrides;
- quote META values containing semicolons/spaces/quotes/`=`;
- explicit QSPACE where a register lives outside the generic `H` default;
- explicit proof tags for externally sourced or higher-authority claims;
- explicit `f=FACE` whenever inference is not authoritative.

Optimize for semantic density, not cryptic brevity.


---

# Anti-Patterns and Common Errors


Avoid these errors:

1. Wrong number of row cells.
2. Missing row ID.
3. Packing more than A›B into IN.
4. Hiding operation parameters in META.
5. Using an unknown OP without `f=FACE`.
6. Treating lanes as proof of parallel runtime execution.
7. Treating logical nodes as physical hardware.
8. Replicating or serializing unknown quantum state.
9. Overriding inferred measurement semantics without a justified model.
10. Claiming a proof because `p=` contains a label.
11. Using compact syntax to bypass canonical verification.
12. Replacing explicit impossible/blocked states with fabricated success.


---

# Migration from Canonical LCTL


Use `columnize` to convert canonical `.lctl` into compact `.lctlc`, then `column-roundtrip` to check semantic equivalence. Use `column-compile` to reconstruct the sealed canonical unit.

A migration should retain the canonical original until the compact source has passed verification and round-trip checks. The corpus includes 53 paired programs showing both representations.


---

# How to Read the 53 Verified Examples


The example suite progresses from basic quantum circuits through noise/error correction, algorithms, explicit parallelism, distributed classical patterns, work/span and placement, distributed simulators, logical network protocols, failure handling, consistency boundaries, and native distributed fabric metadata.

Use `examples/annotated/` when learning. Use `examples/compact/` for source-only ingestion and `examples/lowered/` when learning the compact-to-canonical mapping.


---

# A Reliable Writing Method


For a new program:

1. State the unit identity/profile/target truthfully.
2. Choose defaults that are truly stable across the unit.
3. Create the frame hierarchy.
4. Declare registers/resources.
5. Write CODE rows with explicit OUT/CTRL/IN/ARG.
6. Add parallel/distributed/topology/protocol rows only where semantically needed.
7. Add META overrides only when inference would be wrong or incomplete.
8. Run column verification.
9. Lower to canonical and inspect the 18-cell result.
10. Run semantic/runtime tests appropriate to the profile.
11. Keep blocked physical authority blocked unless evidence exists.


---

# Using the Machine Corpus


`machine/row_pairs.jsonl` is the highest-density supervised source: every record contains the compact row, the actual lowered canonical row, the alias expansion, and compiler-inferred fields. `program_pairs.jsonl` teaches whole-program conversion. `instruction_corpus.jsonl` turns those verified pairs into instruction tasks. `negative_cases.jsonl` teaches fail-closed behavior.

For model training, keep training/validation splits by **program**, not by row, to reduce leakage between rows from the same example.


---

# Extending the Language Safely


A new alias should change spelling only. A new OP should define FACE inference, canonical type/basis/regime/assumption/error/proof defaults as needed, and positive/negative tests. If inference is not universally correct, require an explicit META override rather than adding a misleading default.

Any extension must preserve deterministic lowering and canonical-verifier authority.


---

# Quick Reference


**Magic:** `LCTLC/1.0`  
**Columns:** `ID│LANE│OP│OUT│CTRL│IN│ARG│META`  
**Two-input separator:** `›`  
**Blank:** `_`  
**Frame terminator:** `@end`  
**Preferred extension:** `.lctlc`  
**Canonical extension:** `.lctl`

Core commands: `columnize`, `column-compile`, `column-verify`, `column-stats`, `column-roundtrip`, `column-selftest`.


---


# Default and Inference Rules

The current compiler resolves each compact row in this order:

1. Expand the OP alias.
2. Infer FACE unless META supplies `f=`/`face=`.
3. Split IN into A and optional B at `›`.
4. Use ARG, or an embedded `OP(argument)` payload when ARG is blank.
5. Resolve QSPACE from META, then `@defaults`, then `H`.
6. Infer TYPE from FACE/OP unless META overrides it.
7. Infer BASIS, REGIME, ASSUME, ERROR from OP/face; an operation-specific non-generic default outranks `@defaults`.
8. Resolve RESOURCE from META, default `_`.
9. Resolve CONF from META, then `@defaults`, then `1.0`.
10. Infer PROOF unless META overrides it.

This means measurement rows automatically acquire the measurement basis, stochastic regime, Born-rule/destructive-value assumption, sampling error model, and a measurement proof label.


---


# META Keys

`META` carries only semantic overrides that cannot be inferred or inherited.

| Compact key | Long key | Canonical target |
|---|---|---|
| `f` | `face` | FACE |
| `qs` | `qspace` | QSPACE |
| `t` | `type` | TYPE |
| `basis` | `basis` | BASIS |
| `r` | `regime` | REGIME |
| `asm` | `assume` | ASSUME |
| `err` | `error` | ERROR |
| `res` | `resource` | RESOURCE |
| `c` | `conf` | CONF |
| `p` | `proof` | PROOF |

Use semicolons between entries. Quote values containing semicolons, spaces, quotes, or embedded `=`.


---


# Directives

## `@unit`
Defines unit-level identity and execution defaults. The current lowerer recognizes `id`, `version`, `profile`, `entry`, `target`, `backend`, `network`, `replay`, `precision`, `error_budget`, `resource_ceiling`, `admission`, `state`, and `proof`.

## `@defaults`
Defines recurring row semantics. Typical keys are `qspace`, `basis`, `regime`, `assume`, `error`, and `conf`.

## `@frame`
Begins a frame. `id` is required by convention and generated if absent. Optional frame fields include parent/module/qspace/tick/prev/registers/basis/state_regime/noise_regime/approx_regime/hardware/conf/error/admit/proof.

## `@end`
Ends the current frame.


---

# FACE Map

## DECL

`QREG`, `CREG`, `QUBIT`

## CODE

`I`, `X`, `Y`, `Z`, `H`, `S`, `SDG`, `T`, `TDG`, `RX`, `RY`, `RZ`, `PHASE`, `CX`, `CNOT`, `CY`, `CZ`, `CH`, `SWAP`, `ISWAP`, `CPHASE`, `CCX`, `PREP0`, `PREP1`, `PREP_PLUS`, `PREP_MINUS`, `RESET`, `MEASURE_Z`, `MEASURE_X`, `MEASURE_Y`, `SAMPLE`, `CLASSICAL_IF`, `DEPOLARIZE`, `DEPHASE`, `BIT_FLIP`, `PHASE_FLIP`, `AMPLITUDE_DAMP`, `PHASE_DAMP`, `EXPECT`, `BARRIER`, `SYNC`, `FENCE`, `NOP`

## PARALLEL

`REGION`, `LANE_BIND`, `DEPENDENCY`, `COMM_MODEL`, `MEMORY_TIER`, `COLLECTIVE`, `RELIABILITY`, `DOMAIN_DECOMP`, `OVERLAP`, `CONSENSUS_BOUNDARY`

## COUPLING

`BLOCK_COUPLING`, `RECIPROCITY`, `COUPLING_STRENGTH`, `MONOLITHIC`, `STAGGERED`, `INTERFACE`, `COUPLING_UNRESOLVED`

## FIELD

`FIELD`, `TENSOR`, `BALANCE`, `CONSTITUTIVE`, `LINEARIZATION`, `CONTINUUM_LIMIT`, `DOMAIN`

## PROTOCOL

`ENTANGLE_LINK`, `EPR_RESERVE`, `EPR_RELEASE`, `TELEPORT`, `REMOTE_CONTROL`, `REMOTE_CNOT`, `REMOTE_MEASURE`, `CLASSICAL_FEEDBACK`, `ENTANGLEMENT_SWAP`, `PURIFY`, `HERALD`, `SYNC_QCLOCK`, `QCHANNEL_SEND`, `QCHANNEL_RECEIVE`

## FAILURE

`NODE_UNAVAILABLE`, `PROCESS_FAILURE`, `LINK_FAILURE`, `TIMEOUT`, `CLASSICAL_PACKET_LOSS`, `QUANTUM_LINK_FAILURE`, `CALIBRATION_EXPIRED`, `QPU_UNAVAILABLE`, `COHERENCE_EXCEEDED`, `RESOURCE_EXHAUSTION`, `TARGET_DRIFT`, `SCHEDULER_DEADLOCK`, `OWNERSHIP_AMBIGUITY`, `CHECKPOINT`

## MEMORY

`REGISTER`, `LOCAL`, `CACHE`, `NUMA`, `DEVICE`, `HOST`, `REMOTE`, `PERSISTENT`, `CAPACITY`, `MOVE`

## TRANSFORM

`D1_FREDHOLM`, `D2_SPECTRAL`, `D3_DIRICHLET`, `D4_PROJECTION`, `D5_KERNEL_INDEX`, `D6_LOG_DIFFERENTIAL`, `D7_BIFURCATION`, `D8_HEAT_SEMIGROUP`, `D9_GEODESIC`, `D10_FOKKER_PLANCK`, `D11_VARIATIONAL_KKT`, `D12_FOURIER_SYMBOL`, `D13_FRECHET_LINEARIZE`, `D14_OBSTRUCTION`, `THETA_J_RETRACT`, `THETA_G_CONTINUUM`

## ASSERT

`UNITARY`, `HERMITIAN`, `PROJECTOR`, `DENSITY`, `POVM`, `KRAUS`, `PROBABILITY`, `PROB_SUM`, `TENSOR_DIM`, `TOPOLOGY`, `COUPLING_RECIPROCITY`, `WORK_SPAN`, `TOPOLOGY_SEAL`, `SCHEDULE_SEAL`, `PHYSICAL_CLAIM`

## TOPOLOGY

`NODE`, `LINK`, `QPU`, `CPU`, `GPU`, `MEMORY_DOMAIN`, `NUMA_DOMAIN`, `TOPOLOGY_BIND`, `CONCURRENCY`, `CROSSTALK`, `COHERENCE`, `CALIBRATION`

## DISTRIBUTED

`NODE`, `CLUSTER`, `PARTITION`, `SHARD`, `ROUTE`, `CLASSICAL_CHANNEL`, `QUANTUM_LINK`, `REMOTE_HANDLE`, `CONSISTENCY_SCOPE`, `FAILURE_DOMAIN`, `CHECKPOINT_SCOPE`, `PLACEMENT`, `MESSAGE`, `EPOCH`, `EVENT`, `FUTURE`, `QSTATE_BIND`, `QSTATE_MOVE`, `QSTATE_COPY`, `SERIALIZE_QSTATE`, `REMOTE_QOP`, `MEASUREMENT_DEPENDENCY`, `PARALLEL_CONFLICT`, `PROVENANCE`


---

# Columned LCTL Operator Catalog

This catalog reflects the current LCTL-C compiler authority. Because FACE inference uses ordered family precedence, duplicate names such as `NODE` are assigned to the first matching family.

| Operation | Alias | FACE | Basis | Regime | Assumption | Proof default | Corpus rows |
|---|---|---|---|---|---|---|---:|
| `QREG` | `Q` | `DECL` | `computational` | `exact` | `finite_dimension` | `type:qreg` | 53 |
| `CREG` | `C` | `DECL` | `computational` | `exact` | `finite_dimension` | `type:creg` | 45 |
| `QUBIT` | `QB` | `DECL` | `computational` | `exact` | `finite_dimension` | `type:qubit` | 0 |
| `I` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:i` | 0 |
| `X` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:x` | 10 |
| `Y` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:y` | 0 |
| `Z` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:z` | 1 |
| `H` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:h` | 34 |
| `S` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:s` | 0 |
| `SDG` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:sdg` | 0 |
| `T` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:t` | 0 |
| `TDG` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:tdg` | 0 |
| `RX` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:rx` | 3 |
| `RY` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:ry` | 2 |
| `RZ` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:rz` | 2 |
| `PHASE` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:phase` | 0 |
| `CX` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:cx` | 23 |
| `CNOT` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:cnot` | 0 |
| `CY` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:cy` | 0 |
| `CZ` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:cz` | 3 |
| `CH` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:ch` | 0 |
| `SWAP` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:swap` | 2 |
| `ISWAP` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:iswap` | 0 |
| `CPHASE` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:cphase` | 5 |
| `CCX` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:ccx` | 1 |
| `PREP0` | `P0` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:prep0` | 0 |
| `PREP1` | `P1` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:prep1` | 0 |
| `PREP_PLUS` | `P+` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:prep_plus` | 0 |
| `PREP_MINUS` | `P-` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:prep_minus` | 0 |
| `RESET` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:reset` | 0 |
| `MEASURE_Z` | `MZ` | `CODE` | `Z` | `stochastic` | `Born_rule;destructive_value_semantics` | `measure:z` | 20 |
| `MEASURE_X` | `MX` | `CODE` | `X` | `stochastic` | `Born_rule;destructive_value_semantics` | `measure:x` | 1 |
| `MEASURE_Y` | `MY` | `CODE` | `Y` | `stochastic` | `Born_rule;destructive_value_semantics` | `measure:y` | 0 |
| `SAMPLE` | `—` | `CODE` | `computational` | `stochastic` | `finite_dimension` | `op:sample` | 0 |
| `CLASSICAL_IF` | `IF` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:classical_if` | 3 |
| `DEPOLARIZE` | `—` | `CODE` | `computational` | `noisy` | `finite_dimension` | `op:depolarize` | 1 |
| `DEPHASE` | `—` | `CODE` | `computational` | `noisy` | `finite_dimension` | `op:dephase` | 1 |
| `BIT_FLIP` | `—` | `CODE` | `computational` | `noisy` | `finite_dimension` | `op:bit_flip` | 0 |
| `PHASE_FLIP` | `—` | `CODE` | `computational` | `noisy` | `finite_dimension` | `op:phase_flip` | 0 |
| `AMPLITUDE_DAMP` | `—` | `CODE` | `computational` | `noisy` | `finite_dimension` | `op:amplitude_damp` | 1 |
| `PHASE_DAMP` | `—` | `CODE` | `computational` | `noisy` | `finite_dimension` | `op:phase_damp` | 0 |
| `EXPECT` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:expect` | 3 |
| `BARRIER` | `BAR` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:barrier` | 2 |
| `SYNC` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:sync` | 0 |
| `FENCE` | `—` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:fence` | 0 |
| `NOP` | `.` | `CODE` | `computational` | `exact` | `finite_dimension` | `op:nop` | 32 |
| `REGION` | `REG` | `PARALLEL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 7 |
| `LANE_BIND` | `—` | `PARALLEL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `DEPENDENCY` | `DEP` | `PARALLEL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `COMM_MODEL` | `CM` | `PARALLEL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 3 |
| `MEMORY_TIER` | `—` | `PARALLEL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `COLLECTIVE` | `—` | `PARALLEL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `RELIABILITY` | `—` | `PARALLEL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `DOMAIN_DECOMP` | `—` | `PARALLEL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 2 |
| `OVERLAP` | `—` | `PARALLEL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `CONSENSUS_BOUNDARY` | `—` | `PARALLEL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `BLOCK_COUPLING` | `—` | `COUPLING` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 3 |
| `RECIPROCITY` | `—` | `COUPLING` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 2 |
| `COUPLING_STRENGTH` | `—` | `COUPLING` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 2 |
| `MONOLITHIC` | `—` | `COUPLING` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `STAGGERED` | `—` | `COUPLING` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `INTERFACE` | `—` | `COUPLING` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `COUPLING_UNRESOLVED` | `—` | `COUPLING` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `FIELD` | `—` | `FIELD` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `TENSOR` | `—` | `FIELD` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `BALANCE` | `—` | `FIELD` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `CONSTITUTIVE` | `—` | `FIELD` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `LINEARIZATION` | `—` | `FIELD` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `CONTINUUM_LIMIT` | `—` | `FIELD` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `DOMAIN` | `—` | `FIELD` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `ENTANGLE_LINK` | `—` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `EPR_RESERVE` | `EPR` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 3 |
| `EPR_RELEASE` | `—` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `TELEPORT` | `TEL` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `REMOTE_CONTROL` | `—` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `REMOTE_CNOT` | `RCX` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `REMOTE_MEASURE` | `—` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `CLASSICAL_FEEDBACK` | `—` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `ENTANGLEMENT_SWAP` | `ESWAP` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `PURIFY` | `—` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `HERALD` | `—` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `SYNC_QCLOCK` | `—` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `QCHANNEL_SEND` | `—` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `QCHANNEL_RECEIVE` | `—` | `PROTOCOL` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `NODE_UNAVAILABLE` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `PROCESS_FAILURE` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `LINK_FAILURE` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `TIMEOUT` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `CLASSICAL_PACKET_LOSS` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `QUANTUM_LINK_FAILURE` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `CALIBRATION_EXPIRED` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `QPU_UNAVAILABLE` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `COHERENCE_EXCEEDED` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `RESOURCE_EXHAUSTION` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `TARGET_DRIFT` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `SCHEDULER_DEADLOCK` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `OWNERSHIP_AMBIGUITY` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `CHECKPOINT` | `—` | `FAILURE` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `REGISTER` | `—` | `MEMORY` | `computational` | `exact` | `classical_storage_only` | `lctl-column:1.0` | 0 |
| `LOCAL` | `—` | `MEMORY` | `computational` | `exact` | `classical_storage_only` | `lctl-column:1.0` | 0 |
| `CACHE` | `—` | `MEMORY` | `computational` | `exact` | `classical_storage_only` | `lctl-column:1.0` | 0 |
| `NUMA` | `—` | `MEMORY` | `computational` | `exact` | `classical_storage_only` | `lctl-column:1.0` | 0 |
| `DEVICE` | `—` | `MEMORY` | `computational` | `exact` | `classical_storage_only` | `lctl-column:1.0` | 0 |
| `HOST` | `—` | `MEMORY` | `computational` | `exact` | `classical_storage_only` | `lctl-column:1.0` | 0 |
| `REMOTE` | `—` | `MEMORY` | `computational` | `exact` | `classical_storage_only` | `lctl-column:1.0` | 0 |
| `PERSISTENT` | `—` | `MEMORY` | `computational` | `exact` | `classical_storage_only` | `lctl-column:1.0` | 1 |
| `CAPACITY` | `—` | `MEMORY` | `computational` | `exact` | `classical_storage_only` | `lctl-column:1.0` | 1 |
| `MOVE` | `—` | `MEMORY` | `computational` | `exact` | `classical_storage_only` | `lctl-column:1.0` | 0 |
| `D1_FREDHOLM` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D2_SPECTRAL` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D3_DIRICHLET` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D4_PROJECTION` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D5_KERNEL_INDEX` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D6_LOG_DIFFERENTIAL` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D7_BIFURCATION` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D8_HEAT_SEMIGROUP` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D9_GEODESIC` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D10_FOKKER_PLANCK` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D11_VARIATIONAL_KKT` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D12_FOURIER_SYMBOL` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `D13_FRECHET_LINEARIZE` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `D14_OBSTRUCTION` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `THETA_J_RETRACT` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `THETA_G_CONTINUUM` | `—` | `TRANSFORM` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `UNITARY` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `HERMITIAN` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `PROJECTOR` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `DENSITY` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `POVM` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `KRAUS` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `PROBABILITY` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `PROB_SUM` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `TENSOR_DIM` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `TOPOLOGY` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `COUPLING_RECIPROCITY` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 2 |
| `WORK_SPAN` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 1 |
| `TOPOLOGY_SEAL` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `SCHEDULE_SEAL` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `PHYSICAL_CLAIM` | `—` | `ASSERT` | `computational` | `exact` | `finite_dimension` | `lctl-column:1.0` | 0 |
| `NODE` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 17 |
| `LINK` | `LK` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 7 |
| `QPU` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `CPU` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `GPU` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `MEMORY_DOMAIN` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `NUMA_DOMAIN` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 2 |
| `TOPOLOGY_BIND` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `CONCURRENCY` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `CROSSTALK` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `COHERENCE` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `CALIBRATION` | `—` | `TOPOLOGY` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `CLUSTER` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `PARTITION` | `PART` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 11 |
| `SHARD` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 7 |
| `ROUTE` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 3 |
| `CLASSICAL_CHANNEL` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `QUANTUM_LINK` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `REMOTE_HANDLE` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `CONSISTENCY_SCOPE` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 1 |
| `FAILURE_DOMAIN` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `CHECKPOINT_SCOPE` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `PLACEMENT` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 3 |
| `MESSAGE` | `MSG` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 3 |
| `EPOCH` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 1 |
| `EVENT` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 1 |
| `FUTURE` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 1 |
| `QSTATE_BIND` | `QBIND` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 2 |
| `QSTATE_MOVE` | `QMOVE` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `QSTATE_COPY` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `SERIALIZE_QSTATE` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `REMOTE_QOP` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `MEASUREMENT_DEPENDENCY` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 2 |
| `PARALLEL_CONFLICT` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 0 |
| `PROVENANCE` | `—` | `DISTRIBUTED` | `computational` | `exact` | `local_reference` | `lctl-column:1.0` | 7 |


---

# Verified Example Index

| # | Example | Rows | Compact bytes | Canonical bytes |
|---:|---|---:|---:|---:|
| 1 | `01_bell_state` | 6 | 863 | 2101 |
| 2 | `02_ghz_state` | 8 | 1004 | 2466 |
| 3 | `03_teleportation` | 12 | 1492 | 3151 |
| 4 | `04_superdense_coding` | 10 | 1121 | 2740 |
| 5 | `05_single_qubit_rotations` | 6 | 874 | 2081 |
| 6 | `06_controlled_operations` | 5 | 776 | 1862 |
| 7 | `07_basis_measurement` | 4 | 734 | 1748 |
| 8 | `08_povm` | 3 | 713 | 1539 |
| 9 | `09_density_matrix_noise` | 5 | 877 | 1929 |
| 10 | `10_amplitude_damping` | 5 | 885 | 1937 |
| 11 | `11_depolarizing_channel` | 4 | 823 | 1780 |
| 12 | `12_repetition_error_correction` | 11 | 1305 | 2914 |
| 13 | `13_stabilizer` | 5 | 852 | 1863 |
| 14 | `14_qft` | 8 | 987 | 2371 |
| 15 | `15_grover` | 16 | 1459 | 3648 |
| 16 | `16_phase_estimation` | 7 | 936 | 2240 |
| 17 | `17_vqe` | 5 | 985 | 1911 |
| 18 | `18_qaoa` | 8 | 1158 | 2376 |
| 19 | `19_hamiltonian_simulation` | 4 | 994 | 1823 |
| 20 | `20_measurement_feedback` | 6 | 983 | 2163 |
| 21 | `21_parallel_bell_pairs` | 10 | 1652 | 4006 |
| 22 | `22_coupled_continuum_parallel` | 8 | 1417 | 2643 |
| 23 | `23_independent_quantum_lanes` | 9 | 1295 | 2651 |
| 24 | `24_parallel_bell_pair_generation` | 8 | 1086 | 2415 |
| 25 | `25_parallel_ghz_preparation` | 7 | 1018 | 2252 |
| 26 | `26_parallel_independent_shots` | 6 | 1092 | 2189 |
| 27 | `27_distributed_parameter_sweep` | 7 | 1178 | 2344 |
| 28 | `28_parallel_parameter_shift_gradient` | 6 | 1076 | 2181 |
| 29 | `29_measurement_conditioned_branch` | 6 | 1135 | 2160 |
| 30 | `30_two_node_classical_pipeline` | 8 | 1341 | 2592 |
| 31 | `31_domain_decomposition_halo_exchange` | 6 | 1089 | 2204 |
| 32 | `32_bsp_supersteps` | 6 | 1024 | 2138 |
| 33 | `33_asynchronous_future_event_execution` | 6 | 1112 | 2199 |
| 34 | `34_work_span_analysis` | 5 | 912 | 1940 |
| 35 | `35_topology_aware_placement` | 8 | 1332 | 2566 |
| 36 | `36_communication_aware_scheduling` | 6 | 1085 | 2199 |
| 37 | `37_numa_aware_classical_placement` | 8 | 1343 | 2560 |
| 38 | `38_distributed_statevector_simulation` | 6 | 1144 | 2241 |
| 39 | `39_distributed_density_matrix_simulation` | 6 | 1147 | 2244 |
| 40 | `40_distributed_stabilizer_simulation` | 6 | 1137 | 2224 |
| 41 | `41_epr_generation` | 8 | 1298 | 2565 |
| 42 | `42_teleportation_across_logical_nodes` | 9 | 1493 | 2840 |
| 43 | `43_remote_cnot_decomposition` | 5 | 993 | 2024 |
| 44 | `44_entanglement_swapping` | 5 | 911 | 1969 |
| 45 | `45_classical_feedback_across_nodes` | 8 | 1384 | 2628 |
| 46 | `46_target_rerouting_after_node_failure` | 5 | 1006 | 2027 |
| 47 | `47_shot_retry_after_target_failure` | 5 | 1006 | 2027 |
| 48 | `48_weakly_coupled_partition` | 8 | 1246 | 2546 |
| 49 | `49_strongly_coupled_monolithic_schedule` | 6 | 1062 | 2194 |
| 50 | `50_reciprocal_coupling_validation` | 5 | 953 | 1978 |
| 51 | `51_impossible_consistency_partition_guarantee` | 5 | 1114 | 2098 |
| 52 | `52_physical_distributed_execution_blocked` | 6 | 1121 | 2196 |
| 53 | `53_native_distributed_bell_fabric` | 14 | 1834 | 3647 |


---

# Annotated Example — 01_bell_state

**Unit:** `lctl.example.bell_state`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.bell_state version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.bell_state proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2"
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=H2^2;res="classical_bits=2"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0012 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
C0013 │ measure │ MZ │ c[1] │ _ │ q[1] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `bit[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0012 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0013 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.bell_state ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.bell_state ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^2 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ bit[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0012 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0013 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:0d0a1941cf7a9f1f6c19d87bc129d5f6281471761c6ff07cdcb7f68d65e833a5
END LCTL
@bundle sha256:d99d9e67d03ed54538ffe7aeb0ee4df660da35c90f6330868dd334705df326a2
```


---

# Annotated Example — 02_ghz_state

**Unit:** `lctl.example.ghz_state`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.ghz_state version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.ghz_state proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 3 │ qs=H2^3;res="logical_qubits=3"
D0002 │ alloc │ C │ c │ _ │ _ │ 3 │ qs=H2^3;res="classical_bits=3"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0012 │ circuit │ CX │ q[2] │ q[0] │ q[2] │ _ │ _
C0013 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
C0014 │ measure │ MZ │ c[1] │ _ │ q[1] │ _ │ res="shots=runtime"
C0015 │ measure │ MZ │ c[2] │ _ │ q[2] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `qreg[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=3`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `bit[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=3`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0012 — `CX` → `CX`

Lane `circuit`; output `q[2]`; control `q[0]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0013 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0014 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0015 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[2]`; control `_`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.ghz_state ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.ghz_state ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^3 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 3 ¦ qreg[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=3 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^3 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 3 ¦ bit[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=3 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[2] ¦ q[0] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0013 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0014 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0015 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[2] ¦ _ ¦ q[2] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:e7de09d87fb20f1426425910a07bf505ac8707a366a04063ef200018207845a8
END LCTL
@bundle sha256:1ef717bca7ea1be65024d568b3439d50ae3a5ab28fa0f49f1aa1f475d95b7a9e
```


---

# Annotated Example — 03_teleportation

**Unit:** `lctl.example.teleportation`  
**Rows:** 12  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.teleportation version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.teleportation proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 3 │ qs=H2^3;res="logical_qubits=3"
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=H2^3;res="classical_bits=2"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ H │ q[1] │ _ │ q[1] │ _ │ _
C0012 │ circuit │ CX │ q[2] │ q[1] │ q[2] │ _ │ _
C0013 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0014 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0015 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
C0016 │ measure │ MZ │ c[1] │ _ │ q[1] │ _ │ res="shots=runtime"
C0017 │ feedback │ IF │ q[2] │ c[1] │ 1›X │ _ │ t=hybrid_control;r=stochastic;asm=measurement_conditioned;err=sampling;res="dynamic=1";p=teleport:x
C0018 │ feedback │ IF │ q[2] │ c[0] │ 1›Z │ _ │ t=hybrid_control;r=stochastic;asm=measurement_conditioned;err=sampling;res="dynamic=1";p=teleport:z
C0019 │ analysis │ EXPECT │ _ │ _ │ _ │ X@q[2] │ t=expectation;basis=X;p=teleport:fidelity-proxy
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `qreg[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=3`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `bit[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `H` → `H`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0012 — `CX` → `CX`

Lane `circuit`; output `q[2]`; control `q[1]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0013 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0014 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0015 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0016 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0017 — `IF` → `CLASSICAL_IF`

Lane `feedback`; output `q[2]`; control `c[1]`; input `1›X`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `hybrid_control`, BASIS `computational`, REGIME `stochastic`, ASSUME `measurement_conditioned`, ERROR `sampling`, RESOURCE `dynamic=1`, CONF `1.0`, PROOF `teleport:x`.

### C0018 — `IF` → `CLASSICAL_IF`

Lane `feedback`; output `q[2]`; control `c[0]`; input `1›Z`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `hybrid_control`, BASIS `computational`, REGIME `stochastic`, ASSUME `measurement_conditioned`, ERROR `sampling`, RESOURCE `dynamic=1`, CONF `1.0`, PROOF `teleport:z`.

### C0019 — `EXPECT` → `EXPECT`

Lane `analysis`; output `_`; control `_`; input `no explicit primary input`; argument `X@q[2]`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `expectation`, BASIS `X`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `teleport:fidelity-proxy`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.teleportation ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.teleportation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^3 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 3 ¦ qreg[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=3 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^3 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ bit[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[2] ¦ q[1] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0013 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0014 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0015 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0016 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0017 ¦ CODE ¦ feedback ¦ H ¦ CLASSICAL_IF ¦ q[2] ¦ c[1] ¦ 1 ¦ X ¦ _ ¦ hybrid_control ¦ computational ¦ stochastic ¦ measurement_conditioned ¦ sampling ¦ dynamic=1 ¦ 1.0 ¦ teleport:x)
(C0018 ¦ CODE ¦ feedback ¦ H ¦ CLASSICAL_IF ¦ q[2] ¦ c[0] ¦ 1 ¦ Z ¦ _ ¦ hybrid_control ¦ computational ¦ stochastic ¦ measurement_conditioned ¦ sampling ¦ dynamic=1 ¦ 1.0 ¦ teleport:z)
(C0019 ¦ CODE ¦ analysis ¦ H ¦ EXPECT ¦ _ ¦ _ ¦ _ ¦ _ ¦ X@q[2] ¦ expectation ¦ X ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ teleport:fidelity-proxy)
END QTUPLE
END QFRAME
@frame sha256:08fcadf019fb2b264913a532495815a0e00c516b9a9c2e57b8ead79bc0537102
END LCTL
@bundle sha256:e95444fc9aa7e4a02f531cacebe6c67499422708f6c8dc555e6739137b298bde
```


---

# Annotated Example — 04_superdense_coding

**Unit:** `lctl.example.superdense_coding`  
**Rows:** 10  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.superdense_coding version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.superdense_coding proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2"
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=H2^2;res="classical_bits=2"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0012 │ circuit │ X │ q[0] │ _ │ q[0] │ _ │ _
C0013 │ circuit │ Z │ q[0] │ _ │ q[0] │ _ │ _
C0014 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0015 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0016 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
C0017 │ measure │ MZ │ c[1] │ _ │ q[1] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `bit[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0012 — `X` → `X`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0013 — `Z` → `Z`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:z`.

### C0014 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0015 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0016 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0017 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.superdense_coding ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.superdense_coding ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^2 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ bit[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0013 ¦ CODE ¦ circuit ¦ H ¦ Z ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:z)
(C0014 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0015 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0016 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0017 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:7f73bb3cfacf3891d7c3424a66b3cbfdccff95a75b18667410894d5fa2f782b0
END LCTL
@bundle sha256:2e9e4125196ecc9f60191811ec10a4be40b2d408af60e306925ca0c9cba6e9b1
```


---

# Annotated Example — 05_single_qubit_rotations

**Unit:** `lctl.example.single_qubit_rotations`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.single_qubit_rotations version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.single_qubit_rotations proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 1 │ qs=H2^1;res="logical_qubits=1"
D0002 │ alloc │ C │ c │ _ │ _ │ 1 │ qs=H2^1;res="classical_bits=1"
C0010 │ circuit │ RX │ q[0] │ _ │ q[0] │ 0.4 │ _
C0011 │ circuit │ RY │ q[0] │ _ │ q[0] │ 0.7 │ _
C0012 │ circuit │ RZ │ q[0] │ _ │ q[0] │ -0.2 │ _
C0013 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `qreg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=1`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `bit[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=1`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `RX` → `RX`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `0.4`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:rx`.

### C0011 — `RY` → `RY`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `0.7`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:ry`.

### C0012 — `RZ` → `RZ`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `-0.2`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:rz`.

### C0013 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.single_qubit_rotations ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.single_qubit_rotations ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^1 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 1 ¦ qreg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=1 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^1 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 1 ¦ bit[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=1 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ RX ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ 0.4 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:rx)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ RY ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ 0.7 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:ry)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ RZ ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ -0.2 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:rz)
(C0013 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:edf26bb0a1db49a7d30c1a8ab99467f58e44b6395db6a6d013c0378773414b6c
END LCTL
@bundle sha256:3145aca5233dacef0c5ad0994c0473919a8c54d900436ca91c3c11cbfa21f2a8
```


---

# Annotated Example — 06_controlled_operations

**Unit:** `lctl.example.controlled_operations`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.controlled_operations version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.controlled_operations proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 3 │ qs=H2^3;res="logical_qubits=3"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0012 │ circuit │ CZ │ q[2] │ q[1] │ q[2] │ _ │ _
C0013 │ circuit │ SWAP │ q[2] │ q[0] │ q[2] │ _ │ _
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `qreg[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=3`, CONF `1.0`, PROOF `type:qreg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0012 — `CZ` → `CZ`

Lane `circuit`; output `q[2]`; control `q[1]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cz`.

### C0013 — `SWAP` → `SWAP`

Lane `circuit`; output `q[2]`; control `q[0]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:swap`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.controlled_operations ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.controlled_operations ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^3 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 3 ¦ qreg[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=3 ¦ 1.0 ¦ type:qreg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CZ ¦ q[2] ¦ q[1] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cz)
(C0013 ¦ CODE ¦ circuit ¦ H ¦ SWAP ¦ q[2] ¦ q[0] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:swap)
END QTUPLE
END QFRAME
@frame sha256:624eb2715c25b2af50d6f3e74356231c9066e0a75045231a83add31b827eec44
END LCTL
@bundle sha256:b2754b918fe89c3e865f2a75669663d731f40a830f43f83eb4f1d11d1d04e6cd
```


---

# Annotated Example — 07_basis_measurement

**Unit:** `lctl.example.basis_measurement`  
**Rows:** 4  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.basis_measurement version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.basis_measurement proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 1 │ qs=H2^1;res="logical_qubits=1"
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=H2^1;res="classical_bits=2"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ measure │ MX │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `qreg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=1`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `bit[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `MX` → `MEASURE_X`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `X`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:x`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.basis_measurement ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.basis_measurement ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^1 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 1 ¦ qreg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=1 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^1 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ bit[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ measure ¦ H ¦ MEASURE_X ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ X ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:x)
END QTUPLE
END QFRAME
@frame sha256:e1769e3f5c468123dcd78652fed92fd349e7b37d85cd646c68069ee8aea56ed7
END LCTL
@bundle sha256:3c40b31a2e5e72f462208def046cbf11d25cd97df819953a56f8e1e8e1c1942d
```


---

# Annotated Example — 08_povm

**Unit:** `lctl.example.povm`  
**Rows:** 3  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.povm version=1.0.0 profile=lctl.quantum.core entry=F0000:C0011 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.povm proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 1 │ qs=H2^1;res="logical_qubits=1"
A0010 │ proof │ POVM │ povm_z │ _ │ _ │ 1,0;0,0||0,0;0,1 │ t=POVM;basis=Z;asm="positive_semidefinite;completeness";res="effects=2";p=povm:projective-z
C0011 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `qreg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=1`, CONF `1.0`, PROOF `type:qreg`.

### A0010 — `POVM` → `POVM`

Lane `proof`; output `povm_z`; control `_`; input `no explicit primary input`; argument `1,0;0,0||0,0;0,1`. The compiler lowers this row to FACE `ASSERT`, QSPACE `H`, TYPE `POVM`, BASIS `Z`, REGIME `exact`, ASSUME `positive_semidefinite;completeness`, ERROR `exact`, RESOURCE `effects=2`, CONF `1.0`, PROOF `povm:projective-z`.

### C0011 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.povm ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0011 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.povm ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^1 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 1 ¦ qreg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=1 ¦ 1.0 ¦ type:qreg)
(A0010 ¦ ASSERT ¦ proof ¦ H ¦ POVM ¦ povm_z ¦ _ ¦ _ ¦ _ ¦ 1,0;0,0||0,0;0,1 ¦ POVM ¦ Z ¦ exact ¦ positive_semidefinite;completeness ¦ exact ¦ effects=2 ¦ 1.0 ¦ povm:projective-z)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
END QTUPLE
END QFRAME
@frame sha256:5a118203e1578a106b2b23d7d061de59456f727e148d39f673a5be23b19367c7
END LCTL
@bundle sha256:413e78860a63f8275eb8b670db0d70ff8d68fe8a40bdfb7c7df51a393763646c
```


---

# Annotated Example — 09_density_matrix_noise

**Unit:** `lctl.example.density_matrix_noise`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.density_matrix_noise version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.density_matrix_noise proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 1 │ qs=H2^1;res="logical_qubits=1"
D0002 │ alloc │ C │ c │ _ │ _ │ 1 │ qs=H2^1;res="classical_bits=1"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ noise │ DEPHASE │ q[0] │ _ │ q[0] │ 0.25 │ t=channel;asm=CPTP;err="channel:p=0.25";res="density=1";p=kraus:dephase
C0012 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `qreg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=1`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `bit[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=1`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `DEPHASE` → `DEPHASE`

Lane `noise`; output `q[0]`; control `_`; input `q[0]`; argument `0.25`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `channel`, BASIS `computational`, REGIME `noisy`, ASSUME `CPTP`, ERROR `channel:p=0.25`, RESOURCE `density=1`, CONF `1.0`, PROOF `kraus:dephase`.

### C0012 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.density_matrix_noise ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.density_matrix_noise ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^1 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 1 ¦ qreg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=1 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^1 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 1 ¦ bit[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=1 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ noise ¦ H ¦ DEPHASE ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ 0.25 ¦ channel ¦ computational ¦ noisy ¦ CPTP ¦ channel:p=0.25 ¦ density=1 ¦ 1.0 ¦ kraus:dephase)
(C0012 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:ae27c508e692dfd26423907cc6efc3baf31d380b2c7026a8cb93519683256955
END LCTL
@bundle sha256:1599dfa1415fa8538612673c418ba706295d802718bfd3ce4cc47158575f4dd8
```


---

# Annotated Example — 10_amplitude_damping

**Unit:** `lctl.example.amplitude_damping`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.amplitude_damping version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.amplitude_damping proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 1 │ qs=H2^1;res="logical_qubits=1"
D0002 │ alloc │ C │ c │ _ │ _ │ 1 │ qs=H2^1;res="classical_bits=1"
C0010 │ circuit │ X │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ noise │ AMPLITUDE_DAMP │ q[0] │ _ │ q[0] │ 0.25 │ t=channel;asm=CPTP;err="channel:p=0.25";res="density=1";p=kraus:amplitude-damp
C0012 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `qreg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=1`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^1`, TYPE `bit[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=1`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `X` → `X`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0011 — `AMPLITUDE_DAMP` → `AMPLITUDE_DAMP`

Lane `noise`; output `q[0]`; control `_`; input `q[0]`; argument `0.25`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `channel`, BASIS `computational`, REGIME `noisy`, ASSUME `CPTP`, ERROR `channel:p=0.25`, RESOURCE `density=1`, CONF `1.0`, PROOF `kraus:amplitude-damp`.

### C0012 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.amplitude_damping ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.amplitude_damping ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^1 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 1 ¦ qreg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=1 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^1 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 1 ¦ bit[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=1 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0011 ¦ CODE ¦ noise ¦ H ¦ AMPLITUDE_DAMP ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ 0.25 ¦ channel ¦ computational ¦ noisy ¦ CPTP ¦ channel:p=0.25 ¦ density=1 ¦ 1.0 ¦ kraus:amplitude-damp)
(C0012 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:584834939908398448e034a6e2f4620586bbe37dccecbca779e8920457465266
END LCTL
@bundle sha256:78de9f9e8bb4b2af26268bf6e39b90a427168a5d4c0e52b1e621b030eb9b0159
```


---

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


---

# Annotated Example — 12_repetition_error_correction

**Unit:** `lctl.example.repetition_error_correction`  
**Rows:** 11  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.repetition_error_correction version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.repetition_error_correction proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 3 │ qs=H2^3;res="logical_qubits=3"
D0002 │ alloc │ C │ c │ _ │ _ │ 1 │ qs=H2^3;res="classical_bits=1"
C0010 │ circuit │ X │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0012 │ circuit │ CX │ q[2] │ q[0] │ q[2] │ _ │ _
C0013 │ circuit │ X │ q[1] │ _ │ q[1] │ _ │ _
C0014 │ circuit │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0015 │ circuit │ CX │ q[2] │ q[0] │ q[2] │ _ │ _
C0016 │ circuit │ CCX │ q[0] │ q[2] │ q[1]›q[0] │ _ │ _
C0017 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
M0018 │ qec │ STABILIZER │ repetition3 │ _ │ _ │ ZZI;IZZ │ f=MODEL;t=stabilizer;basis=Z;asm=single_bit_flip_model;res="distance=3";p=qec:repetition3
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `qreg[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=3`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `bit[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=1`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `X` → `X`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0011 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0012 — `CX` → `CX`

Lane `circuit`; output `q[2]`; control `q[0]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0013 — `X` → `X`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0014 — `CX` → `CX`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0015 — `CX` → `CX`

Lane `circuit`; output `q[2]`; control `q[0]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0016 — `CCX` → `CCX`

Lane `circuit`; output `q[0]`; control `q[2]`; input `q[1]›q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:ccx`.

### C0017 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### M0018 — `STABILIZER` → `STABILIZER`

Lane `qec`; output `repetition3`; control `_`; input `no explicit primary input`; argument `ZZI;IZZ`. The compiler lowers this row to FACE `MODEL`, QSPACE `H`, TYPE `stabilizer`, BASIS `Z`, REGIME `exact`, ASSUME `single_bit_flip_model`, ERROR `exact`, RESOURCE `distance=3`, CONF `1.0`, PROOF `qec:repetition3`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.repetition_error_correction ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.repetition_error_correction ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^3 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 3 ¦ qreg[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=3 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^3 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 1 ¦ bit[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=1 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[2] ¦ q[0] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0013 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0014 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0015 ¦ CODE ¦ circuit ¦ H ¦ CX ¦ q[2] ¦ q[0] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0016 ¦ CODE ¦ circuit ¦ H ¦ CCX ¦ q[0] ¦ q[2] ¦ q[1] ¦ q[0] ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:ccx)
(C0017 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(M0018 ¦ MODEL ¦ qec ¦ H ¦ STABILIZER ¦ repetition3 ¦ _ ¦ _ ¦ _ ¦ ZZI;IZZ ¦ stabilizer ¦ Z ¦ exact ¦ single_bit_flip_model ¦ exact ¦ distance=3 ¦ 1.0 ¦ qec:repetition3)
END QTUPLE
END QFRAME
@frame sha256:02cc40009ec45cf6beb067681a23c90c08d5082b8d84b5c4e025bf070cf573fe
END LCTL
@bundle sha256:387c05c477b3d7d80526ff1b3565a3a88b4d6231436707c86113943f7a94f7f2
```


---

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


---

# Annotated Example — 14_qft

**Unit:** `lctl.example.qft`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.qft version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.qft proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 3 │ qs=H2^3;res="logical_qubits=3"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ CPHASE │ q[0] │ q[1] │ q[0] │ 1.5707963267948966 │ _
C0012 │ circuit │ CPHASE │ q[0] │ q[2] │ q[0] │ 0.7853981633974483 │ _
C0013 │ circuit │ H │ q[1] │ _ │ q[1] │ _ │ _
C0014 │ circuit │ CPHASE │ q[1] │ q[2] │ q[1] │ 1.5707963267948966 │ _
C0015 │ circuit │ H │ q[2] │ _ │ q[2] │ _ │ _
C0016 │ circuit │ SWAP │ q[2] │ q[0] │ q[2] │ _ │ _
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `qreg[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=3`, CONF `1.0`, PROOF `type:qreg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `CPHASE` → `CPHASE`

Lane `circuit`; output `q[0]`; control `q[1]`; input `q[0]`; argument `1.5707963267948966`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cphase`.

### C0012 — `CPHASE` → `CPHASE`

Lane `circuit`; output `q[0]`; control `q[2]`; input `q[0]`; argument `0.7853981633974483`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cphase`.

### C0013 — `H` → `H`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0014 — `CPHASE` → `CPHASE`

Lane `circuit`; output `q[1]`; control `q[2]`; input `q[1]`; argument `1.5707963267948966`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cphase`.

### C0015 — `H` → `H`

Lane `circuit`; output `q[2]`; control `_`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0016 — `SWAP` → `SWAP`

Lane `circuit`; output `q[2]`; control `q[0]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:swap`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.qft ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.qft ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^3 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 3 ¦ qreg[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=3 ¦ 1.0 ¦ type:qreg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ CPHASE ¦ q[0] ¦ q[1] ¦ q[0] ¦ _ ¦ 1.5707963267948966 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cphase)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CPHASE ¦ q[0] ¦ q[2] ¦ q[0] ¦ _ ¦ 0.7853981633974483 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cphase)
(C0013 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0014 ¦ CODE ¦ circuit ¦ H ¦ CPHASE ¦ q[1] ¦ q[2] ¦ q[1] ¦ _ ¦ 1.5707963267948966 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cphase)
(C0015 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[2] ¦ _ ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0016 ¦ CODE ¦ circuit ¦ H ¦ SWAP ¦ q[2] ¦ q[0] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:swap)
END QTUPLE
END QFRAME
@frame sha256:5c42c1e479a78783a9950c022bd56e7b8f08056bd9c58a50153a0ddaca9b1b97
END LCTL
@bundle sha256:8cf762173469422d6bd4cdc51882661e83a2bde87e9ed4e15f3700f11c4ace9b
```


---

# Annotated Example — 15_grover

**Unit:** `lctl.example.grover`  
**Rows:** 16  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.grover version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.grover proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2"
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=H2^2;res="classical_bits=2"
C0010 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ circuit │ H │ q[1] │ _ │ q[1] │ _ │ _
C0012 │ circuit │ CZ │ q[1] │ q[0] │ q[1] │ _ │ _
C0013 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0014 │ circuit │ H │ q[1] │ _ │ q[1] │ _ │ _
C0015 │ circuit │ X │ q[0] │ _ │ q[0] │ _ │ _
C0016 │ circuit │ X │ q[1] │ _ │ q[1] │ _ │ _
C0017 │ circuit │ CZ │ q[1] │ q[0] │ q[1] │ _ │ _
C0018 │ circuit │ X │ q[0] │ _ │ q[0] │ _ │ _
C0019 │ circuit │ X │ q[1] │ _ │ q[1] │ _ │ _
C0020 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0021 │ circuit │ H │ q[1] │ _ │ q[1] │ _ │ _
C0022 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
C0023 │ measure │ MZ │ c[1] │ _ │ q[1] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `bit[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `H` → `H`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0012 — `CZ` → `CZ`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cz`.

### C0013 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0014 — `H` → `H`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0015 — `X` → `X`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0016 — `X` → `X`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0017 — `CZ` → `CZ`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cz`.

### C0018 — `X` → `X`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0019 — `X` → `X`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0020 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0021 — `H` → `H`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0022 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0023 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.grover ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.grover ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^2 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ bit[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CZ ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cz)
(C0013 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0014 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0015 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0016 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0017 ¦ CODE ¦ circuit ¦ H ¦ CZ ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cz)
(C0018 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0019 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0020 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0021 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0022 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0023 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:0ea84abdfe1062b200ff88d1bf6a862a8a054e3b270caeadee8680da57c9c1b9
END LCTL
@bundle sha256:775b6e14a5c7ce5acec02131eafc4c6c1e433c7fa4c2fa1da687cdb4bdc0679f
```


---

# Annotated Example — 16_phase_estimation

**Unit:** `lctl.example.phase_estimation`  
**Rows:** 7  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.phase_estimation version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.phase_estimation proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2"
D0002 │ alloc │ C │ c │ _ │ _ │ 1 │ qs=H2^2;res="classical_bits=1"
C0010 │ circuit │ X │ q[1] │ _ │ q[1] │ _ │ _
C0011 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0012 │ circuit │ CPHASE │ q[1] │ q[0] │ q[1] │ 3.141592653589793 │ _
C0013 │ circuit │ H │ q[0] │ _ │ q[0] │ _ │ _
C0014 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `bit[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=1`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `X` → `X`

Lane `circuit`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0011 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0012 — `CPHASE` → `CPHASE`

Lane `circuit`; output `q[1]`; control `q[0]`; input `q[1]`; argument `3.141592653589793`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cphase`.

### C0013 — `H` → `H`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0014 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.phase_estimation ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.phase_estimation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^2 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 1 ¦ bit[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=1 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0011 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0012 ¦ CODE ¦ circuit ¦ H ¦ CPHASE ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ 3.141592653589793 ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cphase)
(C0013 ¦ CODE ¦ circuit ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0014 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:03d6fe7003a3c46bb226a39e655ebc6e334d88beb663c8f8c4c43ef057deb236
END LCTL
@bundle sha256:d7b70a4de76ed196ec0f27ca96225908429a8a8e3948f9512c4317fa23260c69
```


---

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


---

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


---

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


---

# Annotated Example — 20_measurement_feedback

**Unit:** `lctl.example.measurement_feedback`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.measurement_feedback version=1.0.0 profile=lctl.quantum.core entry=F0000:C0010 target=local-reference error_budget=0.01 resource_ceiling=q<=12;density<=8 proof=release-proof
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.measurement_feedback proof=lctl-core
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2"
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=H2^2;res="classical_bits=2"
C0010 │ circuit │ X │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ measure │ MZ │ c[0] │ _ │ q[0] │ _ │ res="shots=runtime"
C0012 │ feedback │ IF │ q[1] │ c[0] │ 1›X │ _ │ t=hybrid_control;r=stochastic;asm=measurement_conditioned;err=sampling;res="dynamic=1";p=feedback:x
C0013 │ measure │ MZ │ c[1] │ _ │ q[1] │ _ │ res="shots=runtime"
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `type:qreg`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `bit[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `type:creg`.

### C0010 — `X` → `X`

Lane `circuit`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:x`.

### C0011 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

### C0012 — `IF` → `CLASSICAL_IF`

Lane `feedback`; output `q[1]`; control `c[0]`; input `1›X`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `hybrid_control`, BASIS `computational`, REGIME `stochastic`, ASSUME `measurement_conditioned`, ERROR `sampling`, RESOURCE `dynamic=1`, CONF `1.0`, PROOF `feedback:x`.

### C0013 — `MZ` → `MEASURE_Z`

Lane `measure`; output `c[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement_result`, BASIS `Z`, REGIME `stochastic`, ASSUME `Born_rule;destructive_value_semantics`, ERROR `sampling`, RESOURCE `shots=runtime`, CONF `1.0`, PROOF `measure:z`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.measurement_feedback ¦ 1.0.0 ¦ lctl.quantum.core ¦ F0000:C0010 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.01 ¦ q<=12;density<=8 ¦ q1-q6 ¦ sealed ¦ release-proof)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.measurement_feedback ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-core)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ type:qreg)
(D0002 ¦ DECL ¦ alloc ¦ H2^2 ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ bit[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ type:creg)
(C0010 ¦ CODE ¦ circuit ¦ H ¦ X ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:x)
(C0011 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
(C0012 ¦ CODE ¦ feedback ¦ H ¦ CLASSICAL_IF ¦ q[1] ¦ c[0] ¦ 1 ¦ X ¦ _ ¦ hybrid_control ¦ computational ¦ stochastic ¦ measurement_conditioned ¦ sampling ¦ dynamic=1 ¦ 1.0 ¦ feedback:x)
(C0013 ¦ CODE ¦ measure ¦ H ¦ MEASURE_Z ¦ c[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ measurement_result ¦ Z ¦ stochastic ¦ Born_rule;destructive_value_semantics ¦ sampling ¦ shots=runtime ¦ 1.0 ¦ measure:z)
END QTUPLE
END QFRAME
@frame sha256:4bd6a5831de6c928ef7348f2370c702ee108eb2d238e3f433b3add4b7c3ad698
END LCTL
@bundle sha256:d8e3d13c3e363307fa02e7f5e455729a4538aa01dcc28f79ba578fd715c1f173
```


---

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


---

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


---

# Annotated Example — 23_independent_quantum_lanes

**Unit:** `lctl.example.independent_quantum_lanes`  
**Rows:** 9  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.independent_quantum_lanes version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.independent_quantum_lanes proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
T0001 │ meta │ NODE │ _ │ _ │ _ │ id=N0;class=emulated_cpu │ asm=finite_dimension;p=lctl-dist:1.2
T0002 │ meta │ NODE │ _ │ _ │ _ │ id=N1;class=emulated_cpu │ asm=finite_dimension;p=lctl-dist:1.2
X0003 │ meta │ QBIND │ _ │ _ │ _ │ q=q[0];node=N0 │ asm=finite_dimension;p=lctl-dist:1.2
X0004 │ meta │ QBIND │ _ │ _ │ _ │ q=q[1];node=N1 │ asm=finite_dimension;p=lctl-dist:1.2
C0010 │ lane0 │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ lane1 │ H │ q[1] │ _ │ q[1] │ _ │ _
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### T0001 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N0;class=emulated_cpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0002 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N1;class=emulated_cpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `QBIND` → `QSTATE_BIND`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `q=q[0];node=N0`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0004 — `QBIND` → `QSTATE_BIND`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `q=q[1];node=N1`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C0010 — `H` → `H`

Lane `lane0`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `H` → `H`

Lane `lane1`; output `q[1]`; control `_`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.independent_quantum_lanes ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.independent_quantum_lanes ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(T0001 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N0;class=emulated_cpu ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0002 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N1;class=emulated_cpu ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ QSTATE_BIND ¦ _ ¦ _ ¦ _ ¦ _ ¦ q=q[0];node=N0 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0004 ¦ DISTRIBUTED ¦ meta ¦ H ¦ QSTATE_BIND ¦ _ ¦ _ ¦ _ ¦ _ ¦ q=q[1];node=N1 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C0010 ¦ CODE ¦ lane0 ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ lane1 ¦ H ¦ H ¦ q[1] ¦ _ ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:402ccfe3d3ede304aa9add740b747be5b1375417fef683246508e85dcaf7a69c
END LCTL
@bundle sha256:f1ad8079d8a66a0266037312dcbdcb2511841de46de08a744902db825a055a73
```


---

# Annotated Example — 24_parallel_bell_pair_generation

**Unit:** `lctl.example.parallel_bell_pair_generation`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.parallel_bell_pair_generation version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.parallel_bell_pair_generation proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 4 │ qs=H2^4;res="logical_qubits=4";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
P0001 │ meta │ REG │ _ │ _ │ _ │ name=bell_pairs;lanes=2 │ p=lctl-dist:1.2
C0010 │ lane0 │ H │ q[0] │ _ │ q[0] │ _ │ _
C0011 │ lane1 │ H │ q[2] │ _ │ q[2] │ _ │ _
C0020 │ lane0 │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0021 │ lane1 │ CX │ q[3] │ q[2] │ q[3] │ _ │ _
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `4`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^4`, TYPE `qreg[4]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=4`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### P0001 — `REG` → `REGION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `name=bell_pairs;lanes=2`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C0010 — `H` → `H`

Lane `lane0`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0011 — `H` → `H`

Lane `lane1`; output `q[2]`; control `_`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0020 — `CX` → `CX`

Lane `lane0`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0021 — `CX` → `CX`

Lane `lane1`; output `q[3]`; control `q[2]`; input `q[3]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.parallel_bell_pair_generation ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.parallel_bell_pair_generation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^4 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 4 ¦ qreg[4] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=4 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(P0001 ¦ PARALLEL ¦ meta ¦ H ¦ REGION ¦ _ ¦ _ ¦ _ ¦ _ ¦ name=bell_pairs;lanes=2 ¦ parallel ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C0010 ¦ CODE ¦ lane0 ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0011 ¦ CODE ¦ lane1 ¦ H ¦ H ¦ q[2] ¦ _ ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0020 ¦ CODE ¦ lane0 ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0021 ¦ CODE ¦ lane1 ¦ H ¦ CX ¦ q[3] ¦ q[2] ¦ q[3] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:6c2d799b9e5638f70f697509652777f15ee964bcf94d8d215a3378f1abf0c228
END LCTL
@bundle sha256:14621195546908c18d90717ff85251802d53c086e5c235abfce91966a9f428f2
```


---

# Annotated Example — 25_parallel_ghz_preparation

**Unit:** `lctl.example.parallel_ghz_preparation`  
**Rows:** 7  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.parallel_ghz_preparation version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.parallel_ghz_preparation proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 3 │ qs=H2^3;res="logical_qubits=3";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
P0001 │ meta │ REG │ _ │ _ │ _ │ name=ghz_fanout;lanes=2 │ p=lctl-dist:1.2
C0010 │ lane0 │ H │ q[0] │ _ │ q[0] │ _ │ _
C0020 │ lane0 │ CX │ q[1] │ q[0] │ q[1] │ _ │ _
C0030 │ lane1 │ CX │ q[2] │ q[1] │ q[2] │ _ │ _
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `3`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^3`, TYPE `qreg[3]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=3`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### P0001 — `REG` → `REGION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `name=ghz_fanout;lanes=2`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C0010 — `H` → `H`

Lane `lane0`; output `q[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:h`.

### C0020 — `CX` → `CX`

Lane `lane0`; output `q[1]`; control `q[0]`; input `q[1]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C0030 — `CX` → `CX`

Lane `lane1`; output `q[2]`; control `q[1]`; input `q[2]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `op:cx`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.parallel_ghz_preparation ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.parallel_ghz_preparation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^3 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 3 ¦ qreg[3] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=3 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(P0001 ¦ PARALLEL ¦ meta ¦ H ¦ REGION ¦ _ ¦ _ ¦ _ ¦ _ ¦ name=ghz_fanout;lanes=2 ¦ parallel ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C0010 ¦ CODE ¦ lane0 ¦ H ¦ H ¦ q[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:h)
(C0020 ¦ CODE ¦ lane0 ¦ H ¦ CX ¦ q[1] ¦ q[0] ¦ q[1] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C0030 ¦ CODE ¦ lane1 ¦ H ¦ CX ¦ q[2] ¦ q[1] ¦ q[2] ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ op:cx)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:6c8ea96f6d088c195bb9de7f90ccdb58d1e7c8c80ed2a670c594957d5bf933f0
END LCTL
@bundle sha256:7031e2481afe33ca666a3293f84dc4bbd1c5c12c4b5b4b34b03cbf8804aaecaa
```


---

# Annotated Example — 26_parallel_independent_shots

**Unit:** `lctl.example.parallel_independent_shots`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.parallel_independent_shots version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.parallel_independent_shots proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
X0001 │ meta │ PART │ _ │ _ │ _ │ id=P0;shots=500 │ asm=finite_dimension;p=lctl-dist:1.2
X0002 │ meta │ PART │ _ │ _ │ _ │ id=P1;shots=500 │ asm=finite_dimension;p=lctl-dist:1.2
X0003 │ meta │ PROVENANCE │ _ │ _ │ _ │ complete=true;calibration_epoch=reference │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### X0001 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P0;shots=500`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P1;shots=500`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `PROVENANCE` → `PROVENANCE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `complete=true;calibration_epoch=reference`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.parallel_independent_shots ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.parallel_independent_shots ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(X0001 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P0;shots=500 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P1;shots=500 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PROVENANCE ¦ _ ¦ _ ¦ _ ¦ _ ¦ complete=true;calibration_epoch=reference ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:fce8991c7d2a12610b2624dadf35e19e75b84f90a69e5c31feb28eea7ea348e6
END LCTL
@bundle sha256:1988a30dd407a3a433f4cb1643291ba256afd555f1f9740110fee1979fa6cb40
```


---

# Annotated Example — 27_distributed_parameter_sweep

**Unit:** `lctl.example.distributed_parameter_sweep`  
**Rows:** 7  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.distributed_parameter_sweep version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.distributed_parameter_sweep proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
X0001 │ meta │ SHARD │ _ │ _ │ _ │ id=S0;theta=0.1 │ asm=finite_dimension;p=lctl-dist:1.2
X0002 │ meta │ SHARD │ _ │ _ │ _ │ id=S1;theta=0.2 │ asm=finite_dimension;p=lctl-dist:1.2
X0003 │ meta │ SHARD │ _ │ _ │ _ │ id=S2;theta=0.3 │ asm=finite_dimension;p=lctl-dist:1.2
X0004 │ meta │ PLACEMENT │ _ │ _ │ _ │ partition=S0;node=N0 │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### X0001 — `SHARD` → `SHARD`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=S0;theta=0.1`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `SHARD` → `SHARD`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=S1;theta=0.2`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `SHARD` → `SHARD`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=S2;theta=0.3`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0004 — `PLACEMENT` → `PLACEMENT`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `partition=S0;node=N0`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.distributed_parameter_sweep ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.distributed_parameter_sweep ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(X0001 ¦ DISTRIBUTED ¦ meta ¦ H ¦ SHARD ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=S0;theta=0.1 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ SHARD ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=S1;theta=0.2 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ SHARD ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=S2;theta=0.3 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0004 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PLACEMENT ¦ _ ¦ _ ¦ _ ¦ _ ¦ partition=S0;node=N0 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:494f18288bdb24267c309a544fbbbd5edf3705ae4935691e2d986d5274fec43d
END LCTL
@bundle sha256:f44c43f0eba946f42f441d2f335e21987129b79b9df29354011115231c8e6842
```


---

# Annotated Example — 28_parallel_parameter_shift_gradient

**Unit:** `lctl.example.parallel_parameter_shift_gradient`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.parallel_parameter_shift_gradient version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.parallel_parameter_shift_gradient proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
P0001 │ meta │ REG │ _ │ _ │ _ │ name=parameter_shift;lanes=2 │ p=lctl-dist:1.2
X0002 │ meta │ SHARD │ _ │ _ │ _ │ id=plus;shift=+pi/2 │ asm=finite_dimension;p=lctl-dist:1.2
X0003 │ meta │ SHARD │ _ │ _ │ _ │ id=minus;shift=-pi/2 │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### P0001 — `REG` → `REGION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `name=parameter_shift;lanes=2`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `SHARD` → `SHARD`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=plus;shift=+pi/2`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `SHARD` → `SHARD`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=minus;shift=-pi/2`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.parallel_parameter_shift_gradient ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.parallel_parameter_shift_gradient ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(P0001 ¦ PARALLEL ¦ meta ¦ H ¦ REGION ¦ _ ¦ _ ¦ _ ¦ _ ¦ name=parameter_shift;lanes=2 ¦ parallel ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ SHARD ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=plus;shift=+pi/2 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ SHARD ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=minus;shift=-pi/2 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:7b177afb8eb3ee2918af00afd5a3132c5781964ccb71f9efdd36661f96541e24
END LCTL
@bundle sha256:5ebfdf465fdb38192411528e0867da4bbe30ef412477dfd13ade2a3d090cf2df
```


---

# Annotated Example — 29_measurement_conditioned_branch

**Unit:** `lctl.example.measurement_conditioned_branch`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.measurement_conditioned_branch version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.measurement_conditioned_branch proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
D0003 │ alloc │ C │ m │ _ │ _ │ 1 │ qs=C;t=creg[1];res="classical_bits=1";p=lctl:1.2
C0010 │ lane0 │ MZ │ m[0] │ _ │ q[0] │ _ │ t=measurement;basis=computational;r=exact;asm=finite_dimension;err=exact;p=born
X0001 │ meta │ MEASUREMENT_DEPENDENCY │ _ │ _ │ _ │ declared=true;from=C0010;to=branch │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0003 — `C` → `CREG`

Lane `alloc`; output `m`; control `_`; input `no explicit primary input`; argument `1`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[1]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=1`, CONF `1.0`, PROOF `lctl:1.2`.

### C0010 — `MZ` → `MEASURE_Z`

Lane `lane0`; output `m[0]`; control `_`; input `q[0]`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `measurement`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `born`.

### X0001 — `MEASUREMENT_DEPENDENCY` → `MEASUREMENT_DEPENDENCY`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `declared=true;from=C0010;to=branch`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.measurement_conditioned_branch ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.measurement_conditioned_branch ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(D0003 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ m ¦ _ ¦ _ ¦ _ ¦ 1 ¦ creg[1] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=1 ¦ 1.0 ¦ lctl:1.2)
(C0010 ¦ CODE ¦ lane0 ¦ H ¦ MEASURE_Z ¦ m[0] ¦ _ ¦ q[0] ¦ _ ¦ _ ¦ measurement ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ born)
(X0001 ¦ DISTRIBUTED ¦ meta ¦ H ¦ MEASUREMENT_DEPENDENCY ¦ _ ¦ _ ¦ _ ¦ _ ¦ declared=true;from=C0010;to=branch ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:86dd93b23028b80351620c03082976257fe70db0114d268d7928e30a9aa7ed73
END LCTL
@bundle sha256:e47ee858e800720e8a0fa7174699ebe1dc534037ba538dd24086c71094f2da25
```


---

# Annotated Example — 30_two_node_classical_pipeline

**Unit:** `lctl.example.two_node_classical_pipeline`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.two_node_classical_pipeline version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.two_node_classical_pipeline proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
T0001 │ meta │ NODE │ _ │ _ │ _ │ id=N0;class=emulated_cpu │ asm=finite_dimension;p=lctl-dist:1.2
T0002 │ meta │ NODE │ _ │ _ │ _ │ id=N1;class=emulated_cpu │ asm=finite_dimension;p=lctl-dist:1.2
T0003 │ meta │ LK │ _ │ _ │ _ │ src=N0;dst=N1;type=classical;latency=2;bandwidth=1000 │ asm=finite_dimension;p=lctl-dist:1.2
X0004 │ meta │ MSG │ _ │ _ │ _ │ src=N0;dst=N1;bytes=256;latency=2.256 │ asm=finite_dimension;p=lctl-dist:1.2
P0005 │ meta │ REG │ _ │ _ │ _ │ pipeline=2;stages=producer,consumer │ p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### T0001 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N0;class=emulated_cpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0002 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N1;class=emulated_cpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0003 — `LK` → `LINK`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=N0;dst=N1;type=classical;latency=2;bandwidth=1000`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0004 — `MSG` → `MESSAGE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=N0;dst=N1;bytes=256;latency=2.256`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### P0005 — `REG` → `REGION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `pipeline=2;stages=producer,consumer`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.two_node_classical_pipeline ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.two_node_classical_pipeline ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(T0001 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N0;class=emulated_cpu ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0002 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N1;class=emulated_cpu ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0003 ¦ TOPOLOGY ¦ meta ¦ H ¦ LINK ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=N0;dst=N1;type=classical;latency=2;bandwidth=1000 ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0004 ¦ DISTRIBUTED ¦ meta ¦ H ¦ MESSAGE ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=N0;dst=N1;bytes=256;latency=2.256 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(P0005 ¦ PARALLEL ¦ meta ¦ H ¦ REGION ¦ _ ¦ _ ¦ _ ¦ _ ¦ pipeline=2;stages=producer,consumer ¦ parallel ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:7310e404aa82ccca950e6d1090e7d6fff7f37c98ddcaaae6eb7d22bae7fe6a6f
END LCTL
@bundle sha256:1c8b331231820f9405e39a2bc9fd957606001261f210ab3b21f9c3e931a8bd67
```


---

# Annotated Example — 31_domain_decomposition_halo_exchange

**Unit:** `lctl.example.domain_decomposition_halo_exchange`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.domain_decomposition_halo_exchange version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.domain_decomposition_halo_exchange proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
F0001 │ meta │ DOMAIN │ _ │ _ │ _ │ Omega;dimension=3 │ p=lctl-dist:1.2
P0002 │ meta │ DOMAIN_DECOMP │ _ │ _ │ _ │ subdomains=2;interface=explicit;halo=1 │ p=lctl-dist:1.2
X0003 │ meta │ MSG │ _ │ _ │ _ │ src=S0;dst=S1;bytes=512;budget=1024 │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### F0001 — `DOMAIN` → `DOMAIN`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `Omega;dimension=3`. The compiler lowers this row to FACE `FIELD`, QSPACE `H`, TYPE `field`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### P0002 — `DOMAIN_DECOMP` → `DOMAIN_DECOMP`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `subdomains=2;interface=explicit;halo=1`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `MSG` → `MESSAGE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=S0;dst=S1;bytes=512;budget=1024`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.domain_decomposition_halo_exchange ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.domain_decomposition_halo_exchange ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(F0001 ¦ FIELD ¦ meta ¦ H ¦ DOMAIN ¦ _ ¦ _ ¦ _ ¦ _ ¦ Omega;dimension=3 ¦ field ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(P0002 ¦ PARALLEL ¦ meta ¦ H ¦ DOMAIN_DECOMP ¦ _ ¦ _ ¦ _ ¦ _ ¦ subdomains=2;interface=explicit;halo=1 ¦ parallel ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ MESSAGE ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=S0;dst=S1;bytes=512;budget=1024 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:fa9681928ab6f0ab4666ab0f8c68996c74597cb376af3c6b73bd179320f7781f
END LCTL
@bundle sha256:d70848e412ae94aaab406161086833863544ff106b00a66aaefaa8f90e53cfa3
```


---

# Annotated Example — 32_bsp_supersteps

**Unit:** `lctl.example.bsp_supersteps`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.bsp_supersteps version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.bsp_supersteps proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
P0001 │ meta │ CM │ _ │ _ │ _ │ alpha=1;beta=0.01;profile=BSP │ p=lctl-dist:1.2
X0002 │ meta │ EPOCH │ _ │ _ │ _ │ id=superstep0;compute=10;communication=2;sync=1 │ asm=finite_dimension;p=lctl-dist:1.2
C0010 │ sync │ BAR │ _ │ _ │ _ │ _ │ t=barrier;p=bsp
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### P0001 — `CM` → `COMM_MODEL`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `alpha=1;beta=0.01;profile=BSP`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `EPOCH` → `EPOCH`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=superstep0;compute=10;communication=2;sync=1`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C0010 — `BAR` → `BARRIER`

Lane `sync`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `barrier`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `bsp`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.bsp_supersteps ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.bsp_supersteps ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(P0001 ¦ PARALLEL ¦ meta ¦ H ¦ COMM_MODEL ¦ _ ¦ _ ¦ _ ¦ _ ¦ alpha=1;beta=0.01;profile=BSP ¦ parallel ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ EPOCH ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=superstep0;compute=10;communication=2;sync=1 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C0010 ¦ CODE ¦ sync ¦ H ¦ BARRIER ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ barrier ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ bsp)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:437342087b6cf00fce19354ec95455bb4f5148558d2e32ca2b65cccc568b0b7c
END LCTL
@bundle sha256:e1361741537f5d46fed95a11d8b5b143374e646fbd63efba8d2dcee45ba2ae44
```


---

# Annotated Example — 33_asynchronous_future_event_execution

**Unit:** `lctl.example.asynchronous_future_event_execution`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.asynchronous_future_event_execution version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.asynchronous_future_event_execution proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
X0001 │ meta │ EVENT │ _ │ _ │ _ │ id=E0;kind=remote_completion │ asm=finite_dimension;p=lctl-dist:1.2
X0002 │ meta │ FUTURE │ _ │ _ │ _ │ id=F0;awaits=E0 │ asm=finite_dimension;p=lctl-dist:1.2
X0003 │ meta │ PROVENANCE │ _ │ _ │ _ │ complete=true;clock=logical │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### X0001 — `EVENT` → `EVENT`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=E0;kind=remote_completion`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `FUTURE` → `FUTURE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=F0;awaits=E0`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `PROVENANCE` → `PROVENANCE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `complete=true;clock=logical`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.asynchronous_future_event_execution ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.asynchronous_future_event_execution ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(X0001 ¦ DISTRIBUTED ¦ meta ¦ H ¦ EVENT ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=E0;kind=remote_completion ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ FUTURE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=F0;awaits=E0 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PROVENANCE ¦ _ ¦ _ ¦ _ ¦ _ ¦ complete=true;clock=logical ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:530184ba10e380ae18a872f36fa0075d4c6bb558acbd60763a3550bde83b9e62
END LCTL
@bundle sha256:73fe2d828fc54faa466e3e0184b7cea74724241151eed878b64eb1491eefed4e
```


---

# Annotated Example — 34_work_span_analysis

**Unit:** `lctl.example.work_span_analysis`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.work_span_analysis version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.work_span_analysis proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
P0001 │ meta │ REG │ _ │ _ │ _ │ name=workspan;W=8;D=3 │ p=lctl-dist:1.2
A0001 │ proof │ WORK_SPAN │ _ │ _ │ _ │ 8,3 │ t=assertion;p=PDC80:work-span
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### P0001 — `REG` → `REGION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `name=workspan;W=8;D=3`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### A0001 — `WORK_SPAN` → `WORK_SPAN`

Lane `proof`; output `_`; control `_`; input `no explicit primary input`; argument `8,3`. The compiler lowers this row to FACE `ASSERT`, QSPACE `H`, TYPE `assertion`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `PDC80:work-span`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.work_span_analysis ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.work_span_analysis ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(P0001 ¦ PARALLEL ¦ meta ¦ H ¦ REGION ¦ _ ¦ _ ¦ _ ¦ _ ¦ name=workspan;W=8;D=3 ¦ parallel ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(A0001 ¦ ASSERT ¦ proof ¦ H ¦ WORK_SPAN ¦ _ ¦ _ ¦ _ ¦ _ ¦ 8,3 ¦ assertion ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ PDC80:work-span)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:5ee9d960c9ec924669b666e3e6a4952c08def3b943e3d4ac451ea75a7c4d5a0a
END LCTL
@bundle sha256:a66431ad05ea96405b47c38b0c7333998f4e3d362613f6460d4efd2ecc34aa51
```


---

# Annotated Example — 35_topology_aware_placement

**Unit:** `lctl.example.topology_aware_placement`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.topology_aware_placement version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.topology_aware_placement proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
T0001 │ meta │ NODE │ _ │ _ │ _ │ id=N0;class=cpu;memory=16GB │ asm=finite_dimension;p=lctl-dist:1.2
T0002 │ meta │ NODE │ _ │ _ │ _ │ id=N1;class=qpu;qubits=4 │ asm=finite_dimension;p=lctl-dist:1.2
T0003 │ meta │ LK │ _ │ _ │ _ │ src=N0;dst=N1;type=classical;latency=1;bandwidth=1000 │ asm=finite_dimension;p=lctl-dist:1.2
X0004 │ meta │ PART │ _ │ _ │ _ │ id=P0;kind=quantum │ asm=finite_dimension;p=lctl-dist:1.2
X0005 │ meta │ PLACEMENT │ _ │ _ │ _ │ partition=P0;node=N1 │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### T0001 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N0;class=cpu;memory=16GB`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0002 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N1;class=qpu;qubits=4`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0003 — `LK` → `LINK`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=N0;dst=N1;type=classical;latency=1;bandwidth=1000`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0004 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P0;kind=quantum`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0005 — `PLACEMENT` → `PLACEMENT`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `partition=P0;node=N1`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.topology_aware_placement ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.topology_aware_placement ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(T0001 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N0;class=cpu;memory=16GB ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0002 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N1;class=qpu;qubits=4 ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0003 ¦ TOPOLOGY ¦ meta ¦ H ¦ LINK ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=N0;dst=N1;type=classical;latency=1;bandwidth=1000 ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0004 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P0;kind=quantum ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0005 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PLACEMENT ¦ _ ¦ _ ¦ _ ¦ _ ¦ partition=P0;node=N1 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:e5d5a52d95f921c78624b38acffe62ec294772d9367f36f2adbc2fe0aac38323
END LCTL
@bundle sha256:97d898036a39d19096e1fb38e559563719a5eae978a99a1cffc51508f77f179b
```


---

# Annotated Example — 36_communication_aware_scheduling

**Unit:** `lctl.example.communication_aware_scheduling`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.communication_aware_scheduling version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.communication_aware_scheduling proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
P0001 │ meta │ CM │ _ │ _ │ _ │ alpha=3;beta=0.02 │ p=lctl-dist:1.2
X0002 │ meta │ MSG │ _ │ _ │ _ │ src=N0;dst=N1;bytes=100;budget=200 │ asm=finite_dimension;p=lctl-dist:1.2
X0003 │ meta │ ROUTE │ _ │ _ │ _ │ id=R0;src=N0;dst=N1;kind=classical │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### P0001 — `CM` → `COMM_MODEL`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `alpha=3;beta=0.02`. The compiler lowers this row to FACE `PARALLEL`, QSPACE `H`, TYPE `parallel`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `MSG` → `MESSAGE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=N0;dst=N1;bytes=100;budget=200`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `ROUTE` → `ROUTE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=R0;src=N0;dst=N1;kind=classical`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.communication_aware_scheduling ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.communication_aware_scheduling ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(P0001 ¦ PARALLEL ¦ meta ¦ H ¦ COMM_MODEL ¦ _ ¦ _ ¦ _ ¦ _ ¦ alpha=3;beta=0.02 ¦ parallel ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ MESSAGE ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=N0;dst=N1;bytes=100;budget=200 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ ROUTE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=R0;src=N0;dst=N1;kind=classical ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:eb84179e18f541fe5bc62b2c9150737600352930d7f53b0bdd0fa7920079b9aa
END LCTL
@bundle sha256:2e8c952c293b4ab65fc2dea1b4fcd87bfb2897ea97586ee1fe2a570cf70b58b3
```


---

# Annotated Example — 37_numa_aware_classical_placement

**Unit:** `lctl.example.numa_aware_classical_placement`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.numa_aware_classical_placement version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.numa_aware_classical_placement proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
T0001 │ meta │ NODE │ _ │ _ │ _ │ id=N0;class=cpu │ asm=finite_dimension;p=lctl-dist:1.2
T0002 │ meta │ NUMA_DOMAIN │ _ │ _ │ _ │ id=NUMA0;node=N0;capacity=8GB │ asm=finite_dimension;p=lctl-dist:1.2
T0003 │ meta │ NUMA_DOMAIN │ _ │ _ │ _ │ id=NUMA1;node=N0;capacity=8GB │ asm=finite_dimension;p=lctl-dist:1.2
M0004 │ meta │ CAPACITY │ _ │ _ │ _ │ used=4;capacity=8 │ asm=finite_dimension;p=lctl-dist:1.2
X0005 │ meta │ PLACEMENT │ _ │ _ │ _ │ partition=P0;node=N0;numa=NUMA0 │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### T0001 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=N0;class=cpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0002 — `NUMA_DOMAIN` → `NUMA_DOMAIN`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=NUMA0;node=N0;capacity=8GB`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0003 — `NUMA_DOMAIN` → `NUMA_DOMAIN`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=NUMA1;node=N0;capacity=8GB`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### M0004 — `CAPACITY` → `CAPACITY`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `used=4;capacity=8`. The compiler lowers this row to FACE `MEMORY`, QSPACE `H`, TYPE `memory`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0005 — `PLACEMENT` → `PLACEMENT`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `partition=P0;node=N0;numa=NUMA0`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.numa_aware_classical_placement ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.numa_aware_classical_placement ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(T0001 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=N0;class=cpu ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0002 ¦ TOPOLOGY ¦ meta ¦ H ¦ NUMA_DOMAIN ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=NUMA0;node=N0;capacity=8GB ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0003 ¦ TOPOLOGY ¦ meta ¦ H ¦ NUMA_DOMAIN ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=NUMA1;node=N0;capacity=8GB ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(M0004 ¦ MEMORY ¦ meta ¦ H ¦ CAPACITY ¦ _ ¦ _ ¦ _ ¦ _ ¦ used=4;capacity=8 ¦ memory ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0005 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PLACEMENT ¦ _ ¦ _ ¦ _ ¦ _ ¦ partition=P0;node=N0;numa=NUMA0 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:2ff26e8768f0a8ccdbc185ef0e567e028a6a3dea42833ed77960e9af31e3f051
END LCTL
@bundle sha256:57856775d9a17a74c3c5d665d5acb0302c7d799f1198e09fee17d05ffa201638
```


---

# Annotated Example — 38_distributed_statevector_simulation

**Unit:** `lctl.example.distributed_statevector_simulation`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.distributed_statevector_simulation version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.distributed_statevector_simulation proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
X0001 │ meta │ PART │ _ │ _ │ _ │ id=P0;statevector_slice=0 │ asm=finite_dimension;p=lctl-dist:1.2
X0002 │ meta │ PART │ _ │ _ │ _ │ id=P1;statevector_slice=1 │ asm=finite_dimension;p=lctl-dist:1.2
X0003 │ meta │ PROVENANCE │ _ │ _ │ _ │ complete=true;realization=CLASSICAL_STATEVECTOR_PARTITION │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### X0001 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P0;statevector_slice=0`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P1;statevector_slice=1`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `PROVENANCE` → `PROVENANCE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `complete=true;realization=CLASSICAL_STATEVECTOR_PARTITION`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.distributed_statevector_simulation ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.distributed_statevector_simulation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(X0001 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P0;statevector_slice=0 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P1;statevector_slice=1 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PROVENANCE ¦ _ ¦ _ ¦ _ ¦ _ ¦ complete=true;realization=CLASSICAL_STATEVECTOR_PARTITION ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:495c67cdbec46879ea64693226f63cf0ea2d6d40a639fcbe3eff0ad086ba11da
END LCTL
@bundle sha256:a50272ed1ac74c2fed9c4d1a888754934f2c46b8ffc1439ebeda310da9d3d787
```


---

# Annotated Example — 39_distributed_density_matrix_simulation

**Unit:** `lctl.example.distributed_density_matrix_simulation`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.distributed_density_matrix_simulation version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.distributed_density_matrix_simulation proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
X0001 │ meta │ PART │ _ │ _ │ _ │ id=P0;density_block=00 │ asm=finite_dimension;p=lctl-dist:1.2
X0002 │ meta │ PART │ _ │ _ │ _ │ id=P1;density_block=11 │ asm=finite_dimension;p=lctl-dist:1.2
X0003 │ meta │ PROVENANCE │ _ │ _ │ _ │ complete=true;realization=CLASSICAL_DENSITY_MATRIX_PARTITION │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### X0001 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P0;density_block=00`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P1;density_block=11`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `PROVENANCE` → `PROVENANCE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `complete=true;realization=CLASSICAL_DENSITY_MATRIX_PARTITION`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.distributed_density_matrix_simulation ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.distributed_density_matrix_simulation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(X0001 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P0;density_block=00 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P1;density_block=11 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PROVENANCE ¦ _ ¦ _ ¦ _ ¦ _ ¦ complete=true;realization=CLASSICAL_DENSITY_MATRIX_PARTITION ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:0214d4e124ae904c3e619ae56bd56966a6795d006747d3f8cb795d348cecdc5d
END LCTL
@bundle sha256:cf7c16479108ad3ab9e4ed8f930015bd12001a531cc67c89647ace303aa43961
```


---

# Annotated Example — 40_distributed_stabilizer_simulation

**Unit:** `lctl.example.distributed_stabilizer_simulation`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.distributed_stabilizer_simulation version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.distributed_stabilizer_simulation proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
X0001 │ meta │ SHARD │ _ │ _ │ _ │ id=S0;tableau_rows=0-3 │ asm=finite_dimension;p=lctl-dist:1.2
X0002 │ meta │ SHARD │ _ │ _ │ _ │ id=S1;tableau_rows=4-7 │ asm=finite_dimension;p=lctl-dist:1.2
X0003 │ meta │ PROVENANCE │ _ │ _ │ _ │ complete=true;realization=CLASSICAL_STABILIZER_PARTITION │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### X0001 — `SHARD` → `SHARD`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=S0;tableau_rows=0-3`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `SHARD` → `SHARD`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=S1;tableau_rows=4-7`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `PROVENANCE` → `PROVENANCE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `complete=true;realization=CLASSICAL_STABILIZER_PARTITION`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.distributed_stabilizer_simulation ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.distributed_stabilizer_simulation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(X0001 ¦ DISTRIBUTED ¦ meta ¦ H ¦ SHARD ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=S0;tableau_rows=0-3 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ SHARD ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=S1;tableau_rows=4-7 ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PROVENANCE ¦ _ ¦ _ ¦ _ ¦ _ ¦ complete=true;realization=CLASSICAL_STABILIZER_PARTITION ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:934a672a95a506a958a40dbc9928a14f69c14a4e2102a49b9e3c0bb105795c4a
END LCTL
@bundle sha256:27904afc6c5caaf4392dc44e5f48c270523c23fdcabb84754196d147b4a835dc
```


---

# Annotated Example — 41_epr_generation

**Unit:** `lctl.example.epr_generation`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.epr_generation version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.epr_generation proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
T0001 │ meta │ NODE │ _ │ _ │ _ │ id=Q0;class=qpu_partition │ asm=finite_dimension;p=lctl-dist:1.2
T0002 │ meta │ NODE │ _ │ _ │ _ │ id=Q1;class=qpu_partition │ asm=finite_dimension;p=lctl-dist:1.2
T0003 │ meta │ LK │ _ │ _ │ _ │ src=Q0;dst=Q1;type=quantum;latency=5;fidelity=0.98;setup=4;herald=1;success=0.8 │ asm=finite_dimension;p=lctl-dist:1.2
Q0004 │ meta │ EPR │ _ │ _ │ _ │ count=1;link=QL0 │ p=lctl-dist:1.2
Q0005 │ meta │ ENTANGLE_LINK │ _ │ _ │ _ │ src=Q0;dst=Q1;link=QL0 │ p=lctl-dist:1.2
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

### Q0004 — `EPR` → `EPR_RESERVE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `count=1;link=QL0`. The compiler lowers this row to FACE `PROTOCOL`, QSPACE `H`, TYPE `protocol`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### Q0005 — `ENTANGLE_LINK` → `ENTANGLE_LINK`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=Q0;dst=Q1;link=QL0`. The compiler lowers this row to FACE `PROTOCOL`, QSPACE `H`, TYPE `protocol`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.epr_generation ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.epr_generation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(T0001 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=Q0;class=qpu_partition ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0002 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=Q1;class=qpu_partition ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0003 ¦ TOPOLOGY ¦ meta ¦ H ¦ LINK ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=Q0;dst=Q1;type=quantum;latency=5;fidelity=0.98;setup=4;herald=1;success=0.8 ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(Q0004 ¦ PROTOCOL ¦ meta ¦ H ¦ EPR_RESERVE ¦ _ ¦ _ ¦ _ ¦ _ ¦ count=1;link=QL0 ¦ protocol ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(Q0005 ¦ PROTOCOL ¦ meta ¦ H ¦ ENTANGLE_LINK ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=Q0;dst=Q1;link=QL0 ¦ protocol ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:ad168d928e72d5dfe58d251bd54d8c58f67ae91877f72bb0c92ce6993b694224
END LCTL
@bundle sha256:5a9e7a3607b3c0fd048b9f21f482bb1784b520648a8fc1cd6503429803cab9d1
```


---

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


---

# Annotated Example — 43_remote_cnot_decomposition

**Unit:** `lctl.example.remote_cnot_decomposition`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.remote_cnot_decomposition version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.remote_cnot_decomposition proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
X0001 │ meta │ ROUTE │ _ │ _ │ _ │ id=RQ0;src=Q0;dst=Q1;kind=quantum │ asm=finite_dimension;p=lctl-dist:1.2
Q0002 │ meta │ RCX │ _ │ _ │ _ │ route=RQ0;target_supported=true;control=q[0];target=q[1] │ p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### X0001 — `ROUTE` → `ROUTE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=RQ0;src=Q0;dst=Q1;kind=quantum`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### Q0002 — `RCX` → `REMOTE_CNOT`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `route=RQ0;target_supported=true;control=q[0];target=q[1]`. The compiler lowers this row to FACE `PROTOCOL`, QSPACE `H`, TYPE `protocol`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.remote_cnot_decomposition ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.remote_cnot_decomposition ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(X0001 ¦ DISTRIBUTED ¦ meta ¦ H ¦ ROUTE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=RQ0;src=Q0;dst=Q1;kind=quantum ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(Q0002 ¦ PROTOCOL ¦ meta ¦ H ¦ REMOTE_CNOT ¦ _ ¦ _ ¦ _ ¦ _ ¦ route=RQ0;target_supported=true;control=q[0];target=q[1] ¦ protocol ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:80f58f5394d01ccd4d24cf4b903444eb88c3f1d46f6b96cae5eca7b3d07df289
END LCTL
@bundle sha256:e9162d009c8e0a892bf3e5801801184fb5ac1380601aec432f5d6913e0ebeed5
```


---

# Annotated Example — 44_entanglement_swapping

**Unit:** `lctl.example.entanglement_swapping`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.entanglement_swapping version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.entanglement_swapping proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
Q0001 │ meta │ EPR │ _ │ _ │ _ │ count=2;link=chain │ p=lctl-dist:1.2
Q0002 │ meta │ ESWAP │ _ │ _ │ _ │ ebit_count=2;at=Q1 │ p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### Q0001 — `EPR` → `EPR_RESERVE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `count=2;link=chain`. The compiler lowers this row to FACE `PROTOCOL`, QSPACE `H`, TYPE `protocol`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### Q0002 — `ESWAP` → `ENTANGLEMENT_SWAP`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `ebit_count=2;at=Q1`. The compiler lowers this row to FACE `PROTOCOL`, QSPACE `H`, TYPE `protocol`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.entanglement_swapping ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.entanglement_swapping ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(Q0001 ¦ PROTOCOL ¦ meta ¦ H ¦ EPR_RESERVE ¦ _ ¦ _ ¦ _ ¦ _ ¦ count=2;link=chain ¦ protocol ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(Q0002 ¦ PROTOCOL ¦ meta ¦ H ¦ ENTANGLEMENT_SWAP ¦ _ ¦ _ ¦ _ ¦ _ ¦ ebit_count=2;at=Q1 ¦ protocol ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:0a06b0a3977f349f0bab0427ba7c6aca61b31e73ed349ce52ad553d166cb8b56
END LCTL
@bundle sha256:0c2993592f2a518df247e5bdbe08519640cfa1fb3d7ff32abc41f6d9d7a24c00
```


---

# Annotated Example — 45_classical_feedback_across_nodes

**Unit:** `lctl.example.classical_feedback_across_nodes`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.classical_feedback_across_nodes version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.classical_feedback_across_nodes proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
T0001 │ meta │ NODE │ _ │ _ │ _ │ id=Q0;class=qpu_partition │ asm=finite_dimension;p=lctl-dist:1.2
T0002 │ meta │ NODE │ _ │ _ │ _ │ id=Q1;class=qpu_partition │ asm=finite_dimension;p=lctl-dist:1.2
T0003 │ meta │ LK │ _ │ _ │ _ │ src=Q0;dst=Q1;type=classical;latency=2;bandwidth=1000 │ asm=finite_dimension;p=lctl-dist:1.2
Q0004 │ meta │ CLASSICAL_FEEDBACK │ _ │ _ │ _ │ src=Q0;dst=Q1;measurement=m0;latency=2 │ p=lctl-dist:1.2
X0005 │ meta │ MEASUREMENT_DEPENDENCY │ _ │ _ │ _ │ declared=true;from=m0;to=feedback │ asm=finite_dimension;p=lctl-dist:1.2
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

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=Q0;dst=Q1;type=classical;latency=2;bandwidth=1000`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### Q0004 — `CLASSICAL_FEEDBACK` → `CLASSICAL_FEEDBACK`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `src=Q0;dst=Q1;measurement=m0;latency=2`. The compiler lowers this row to FACE `PROTOCOL`, QSPACE `H`, TYPE `protocol`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0005 — `MEASUREMENT_DEPENDENCY` → `MEASUREMENT_DEPENDENCY`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `declared=true;from=m0;to=feedback`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.classical_feedback_across_nodes ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.classical_feedback_across_nodes ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(T0001 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=Q0;class=qpu_partition ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0002 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=Q1;class=qpu_partition ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0003 ¦ TOPOLOGY ¦ meta ¦ H ¦ LINK ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=Q0;dst=Q1;type=classical;latency=2;bandwidth=1000 ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(Q0004 ¦ PROTOCOL ¦ meta ¦ H ¦ CLASSICAL_FEEDBACK ¦ _ ¦ _ ¦ _ ¦ _ ¦ src=Q0;dst=Q1;measurement=m0;latency=2 ¦ protocol ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0005 ¦ DISTRIBUTED ¦ meta ¦ H ¦ MEASUREMENT_DEPENDENCY ¦ _ ¦ _ ¦ _ ¦ _ ¦ declared=true;from=m0;to=feedback ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:126be62ea2eb7164365f65daf23d4113a41af756b43c01f77f13e467f048c6af
END LCTL
@bundle sha256:194b85a848af90fed94e191eec25a9956a3389fe250d248bc2d8252e9c0f4f72
```


---

# Annotated Example — 46_target_rerouting_after_node_failure

**Unit:** `lctl.example.target_rerouting_after_node_failure`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.target_rerouting_after_node_failure version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.target_rerouting_after_node_failure proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
Y0001 │ meta │ NODE_UNAVAILABLE │ _ │ _ │ _ │ node=N1;detect=heartbeat;retry=true │ p=lctl-dist:1.2
X0002 │ meta │ ROUTE │ _ │ _ │ _ │ id=R0;src=N0;dst=N2;reason=reroute │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### Y0001 — `NODE_UNAVAILABLE` → `NODE_UNAVAILABLE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `node=N1;detect=heartbeat;retry=true`. The compiler lowers this row to FACE `FAILURE`, QSPACE `H`, TYPE `failure`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `ROUTE` → `ROUTE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=R0;src=N0;dst=N2;reason=reroute`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.target_rerouting_after_node_failure ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.target_rerouting_after_node_failure ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(Y0001 ¦ FAILURE ¦ meta ¦ H ¦ NODE_UNAVAILABLE ¦ _ ¦ _ ¦ _ ¦ _ ¦ node=N1;detect=heartbeat;retry=true ¦ failure ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ ROUTE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=R0;src=N0;dst=N2;reason=reroute ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:099c1e57b996ee3f848408a3f9c97b35c49b64320cb5d14d60fd73883617719f
END LCTL
@bundle sha256:3a4cb80dab164d5076faa1bc0162fc57c848ae1a96531c0aad831950bf312daf
```


---

# Annotated Example — 47_shot_retry_after_target_failure

**Unit:** `lctl.example.shot_retry_after_target_failure`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.shot_retry_after_target_failure version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.shot_retry_after_target_failure proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
Y0001 │ meta │ QPU_UNAVAILABLE │ _ │ _ │ _ │ node=Q0;detect=adapter;retry=true;scope=shot │ p=lctl-dist:1.2
X0002 │ meta │ PROVENANCE │ _ │ _ │ _ │ complete=true;retry=shot_only │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### Y0001 — `QPU_UNAVAILABLE` → `QPU_UNAVAILABLE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `node=Q0;detect=adapter;retry=true;scope=shot`. The compiler lowers this row to FACE `FAILURE`, QSPACE `H`, TYPE `failure`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0002 — `PROVENANCE` → `PROVENANCE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `complete=true;retry=shot_only`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.shot_retry_after_target_failure ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.shot_retry_after_target_failure ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(Y0001 ¦ FAILURE ¦ meta ¦ H ¦ QPU_UNAVAILABLE ¦ _ ¦ _ ¦ _ ¦ _ ¦ node=Q0;detect=adapter;retry=true;scope=shot ¦ failure ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0002 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PROVENANCE ¦ _ ¦ _ ¦ _ ¦ _ ¦ complete=true;retry=shot_only ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:c6acd8a3fd3f06fa03386a67a778743c4330fa9a3fc306929d7295a4d7e67901
END LCTL
@bundle sha256:94e019fe3a5c75adbef86ebfc31d5d7089522ff0e9133f19abd1a44e298e74c2
```


---

# Annotated Example — 48_weakly_coupled_partition

**Unit:** `lctl.example.weakly_coupled_partition`  
**Rows:** 8  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.weakly_coupled_partition version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.weakly_coupled_partition proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
K0001 │ meta │ BLOCK_COUPLING │ _ │ _ │ _ │ A,B;C,D │ p=lctl-dist:1.2
K0002 │ meta │ COUPLING_STRENGTH │ _ │ _ │ _ │ value=0.05;classification=WEAKLY_COUPLED │ p=lctl-dist:1.2
K0003 │ meta │ STAGGERED │ _ │ _ │ _ │ admitted=true │ p=lctl-dist:1.2
X0004 │ meta │ PART │ _ │ _ │ _ │ id=P0;subsystem=A │ asm=finite_dimension;p=lctl-dist:1.2
X0005 │ meta │ PART │ _ │ _ │ _ │ id=P1;subsystem=D │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### K0001 — `BLOCK_COUPLING` → `BLOCK_COUPLING`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `A,B;C,D`. The compiler lowers this row to FACE `COUPLING`, QSPACE `H`, TYPE `coupling`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### K0002 — `COUPLING_STRENGTH` → `COUPLING_STRENGTH`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `value=0.05;classification=WEAKLY_COUPLED`. The compiler lowers this row to FACE `COUPLING`, QSPACE `H`, TYPE `coupling`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### K0003 — `STAGGERED` → `STAGGERED`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `admitted=true`. The compiler lowers this row to FACE `COUPLING`, QSPACE `H`, TYPE `coupling`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0004 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P0;subsystem=A`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0005 — `PART` → `PARTITION`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=P1;subsystem=D`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.weakly_coupled_partition ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.weakly_coupled_partition ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(K0001 ¦ COUPLING ¦ meta ¦ H ¦ BLOCK_COUPLING ¦ _ ¦ _ ¦ _ ¦ _ ¦ A,B;C,D ¦ coupling ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(K0002 ¦ COUPLING ¦ meta ¦ H ¦ COUPLING_STRENGTH ¦ _ ¦ _ ¦ _ ¦ _ ¦ value=0.05;classification=WEAKLY_COUPLED ¦ coupling ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(K0003 ¦ COUPLING ¦ meta ¦ H ¦ STAGGERED ¦ _ ¦ _ ¦ _ ¦ _ ¦ admitted=true ¦ coupling ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0004 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P0;subsystem=A ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0005 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PARTITION ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=P1;subsystem=D ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:4acaaa0cd6d7962d131061dceef5cc050e46c763ecd39f793b5c953729eb4f1b
END LCTL
@bundle sha256:887cadda47ab97284028557a6561e94b3a4aa12fdafd2e60f8d54c17fcb893ab
```


---

# Annotated Example — 49_strongly_coupled_monolithic_schedule

**Unit:** `lctl.example.strongly_coupled_monolithic_schedule`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.strongly_coupled_monolithic_schedule version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.strongly_coupled_monolithic_schedule proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
K0001 │ meta │ BLOCK_COUPLING │ _ │ _ │ _ │ A,B;C,D │ p=lctl-dist:1.2
K0002 │ meta │ COUPLING_STRENGTH │ _ │ _ │ _ │ value=0.9;classification=STRONGLY_COUPLED │ p=lctl-dist:1.2
K0003 │ meta │ MONOLITHIC │ _ │ _ │ _ │ required=true │ p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### K0001 — `BLOCK_COUPLING` → `BLOCK_COUPLING`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `A,B;C,D`. The compiler lowers this row to FACE `COUPLING`, QSPACE `H`, TYPE `coupling`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### K0002 — `COUPLING_STRENGTH` → `COUPLING_STRENGTH`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `value=0.9;classification=STRONGLY_COUPLED`. The compiler lowers this row to FACE `COUPLING`, QSPACE `H`, TYPE `coupling`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### K0003 — `MONOLITHIC` → `MONOLITHIC`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `required=true`. The compiler lowers this row to FACE `COUPLING`, QSPACE `H`, TYPE `coupling`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.strongly_coupled_monolithic_schedule ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.strongly_coupled_monolithic_schedule ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(K0001 ¦ COUPLING ¦ meta ¦ H ¦ BLOCK_COUPLING ¦ _ ¦ _ ¦ _ ¦ _ ¦ A,B;C,D ¦ coupling ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(K0002 ¦ COUPLING ¦ meta ¦ H ¦ COUPLING_STRENGTH ¦ _ ¦ _ ¦ _ ¦ _ ¦ value=0.9;classification=STRONGLY_COUPLED ¦ coupling ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(K0003 ¦ COUPLING ¦ meta ¦ H ¦ MONOLITHIC ¦ _ ¦ _ ¦ _ ¦ _ ¦ required=true ¦ coupling ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:bd10f8e4af33bca21769ff1bbebcdb701dc14e12c88045e13c2ba33d1597d6d7
END LCTL
@bundle sha256:2bcc592b7baf84e12d8cd492df6876703fe280ccf5889c4497bc893d31048d34
```


---

# Annotated Example — 50_reciprocal_coupling_validation

**Unit:** `lctl.example.reciprocal_coupling_validation`  
**Rows:** 5  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.reciprocal_coupling_validation version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.reciprocal_coupling_validation proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
K0001 │ meta │ RECIPROCITY │ _ │ _ │ _ │ B=C^T │ p=lctl-dist:1.2
A0001 │ proof │ COUPLING_RECIPROCITY │ _ │ _ │ _ │ 0,1;2,0||0,2;1,0 │ t=assertion;p=CP23:reciprocity
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### K0001 — `RECIPROCITY` → `RECIPROCITY`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `B=C^T`. The compiler lowers this row to FACE `COUPLING`, QSPACE `H`, TYPE `coupling`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### A0001 — `COUPLING_RECIPROCITY` → `COUPLING_RECIPROCITY`

Lane `proof`; output `_`; control `_`; input `no explicit primary input`; argument `0,1;2,0||0,2;1,0`. The compiler lowers this row to FACE `ASSERT`, QSPACE `H`, TYPE `assertion`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `CP23:reciprocity`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.reciprocal_coupling_validation ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.reciprocal_coupling_validation ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(K0001 ¦ COUPLING ¦ meta ¦ H ¦ RECIPROCITY ¦ _ ¦ _ ¦ _ ¦ _ ¦ B=C^T ¦ coupling ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(A0001 ¦ ASSERT ¦ proof ¦ H ¦ COUPLING_RECIPROCITY ¦ _ ¦ _ ¦ _ ¦ _ ¦ 0,1;2,0||0,2;1,0 ¦ assertion ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ CP23:reciprocity)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:ccb04ffde354da1c9793ef02783654fae5f42d54c9e3e553b683967a187fc5c8
END LCTL
@bundle sha256:8d19e8800be39110e67a07ef127a87082cd4ce03a2d94643528895a70c55b06c
```


---

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


---

# Annotated Example — 52_physical_distributed_execution_blocked

**Unit:** `lctl.example.physical_distributed_execution_blocked`  
**Rows:** 6  
**Authority:** compact source lowered by LCTL-C 1.0 into canonical 18-cell LCTL.

## Compact source

```text
LCTLC/1.0
@unit id=lctl.example.physical_distributed_execution_blocked version=1.2.0-rc1 profile=lctl.quantum.parallel.distributed entry=F0000:C9999 target=local-reference error_budget=0.05 resource_ceiling=q<=12;distributed=logical;physical=false proof=lctl-dist:1.2
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.example.physical_distributed_execution_blocked proof=lctl-dist:1.2
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D0001 │ alloc │ Q │ q │ _ │ _ │ 2 │ qs=H2^2;res="logical_qubits=2";p=lctl:1.2
D0002 │ alloc │ C │ c │ _ │ _ │ 2 │ qs=C;t=creg[2];res="classical_bits=2";p=lctl:1.2
T0001 │ meta │ NODE │ _ │ _ │ _ │ id=Q0;class=abstract_qpu │ asm=finite_dimension;p=lctl-dist:1.2
T0002 │ meta │ NODE │ _ │ _ │ _ │ id=Q1;class=abstract_qpu │ asm=finite_dimension;p=lctl-dist:1.2
X0003 │ meta │ PROVENANCE │ _ │ _ │ _ │ complete=true;physical=false │ asm=finite_dimension;p=lctl-dist:1.2
C9999 │ lane0 │ . │ _ │ _ │ _ │ _ │ p=lctl:nop
@end
```

## Row-by-row semantics

### D0001 — `Q` → `QREG`

Lane `alloc`; output `q`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `H2^2`, TYPE `qreg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `logical_qubits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### D0002 — `C` → `CREG`

Lane `alloc`; output `c`; control `_`; input `no explicit primary input`; argument `2`. The compiler lowers this row to FACE `DECL`, QSPACE `C`, TYPE `creg[2]`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `classical_bits=2`, CONF `1.0`, PROOF `lctl:1.2`.

### T0001 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=Q0;class=abstract_qpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### T0002 — `NODE` → `NODE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `id=Q1;class=abstract_qpu`. The compiler lowers this row to FACE `TOPOLOGY`, QSPACE `H`, TYPE `topology`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### X0003 — `PROVENANCE` → `PROVENANCE`

Lane `meta`; output `_`; control `_`; input `no explicit primary input`; argument `complete=true;physical=false`. The compiler lowers this row to FACE `DISTRIBUTED`, QSPACE `H`, TYPE `distributed`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl-dist:1.2`.

### C9999 — `.` → `NOP`

Lane `lane0`; output `_`; control `_`; input `no explicit primary input`; argument `_`. The compiler lowers this row to FACE `CODE`, QSPACE `H`, TYPE `operator`, BASIS `computational`, REGIME `exact`, ASSUME `finite_dimension`, ERROR `exact`, RESOURCE `_`, CONF `1.0`, PROOF `lctl:nop`.

## Lowered canonical authority

```text
LCTL/1.3
BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF
(bundle ¦ lctl.example.physical_distributed_execution_blocked ¦ 1.2.0-rc1 ¦ lctl.quantum.parallel.distributed ¦ F0000:C9999 ¦ 1 ¦ local-reference ¦ none ¦ deny ¦ deterministic-seeded ¦ f64 ¦ 0.05 ¦ q<=12;distributed=logical;physical=false ¦ q1-q6 ¦ sealed ¦ lctl-dist:1.2)
QFRAME/1.0 BEGIN F0000
ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF
(F0000 ¦ ROOT ¦ lctl.example.physical_distributed_execution_blocked ¦ H ¦ 0 ¦ _ ¦ declared ¦ computational ¦ pure_or_mixed ¦ explicit ¦ typed ¦ local-reference ¦ 1.0 ¦ tracked ¦ observe ¦ lctl-dist:1.2)
QTUPLE/1.0
ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF
(D0001 ¦ DECL ¦ alloc ¦ H2^2 ¦ QREG ¦ q ¦ _ ¦ _ ¦ _ ¦ 2 ¦ qreg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ logical_qubits=2 ¦ 1.0 ¦ lctl:1.2)
(D0002 ¦ DECL ¦ alloc ¦ C ¦ CREG ¦ c ¦ _ ¦ _ ¦ _ ¦ 2 ¦ creg[2] ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ classical_bits=2 ¦ 1.0 ¦ lctl:1.2)
(T0001 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=Q0;class=abstract_qpu ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(T0002 ¦ TOPOLOGY ¦ meta ¦ H ¦ NODE ¦ _ ¦ _ ¦ _ ¦ _ ¦ id=Q1;class=abstract_qpu ¦ topology ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(X0003 ¦ DISTRIBUTED ¦ meta ¦ H ¦ PROVENANCE ¦ _ ¦ _ ¦ _ ¦ _ ¦ complete=true;physical=false ¦ distributed ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl-dist:1.2)
(C9999 ¦ CODE ¦ lane0 ¦ H ¦ NOP ¦ _ ¦ _ ¦ _ ¦ _ ¦ _ ¦ operator ¦ computational ¦ exact ¦ finite_dimension ¦ exact ¦ _ ¦ 1.0 ¦ lctl:nop)
END QTUPLE
END QFRAME
@frame sha256:4921b8ff38a8382a8b07528e7db33bbebe70826a95b7b94803937a726056b716
END LCTL
@bundle sha256:d88e821c3c86161031ff68eb51e08406479732d60ab3db22c6737dccfec0642d
```


---

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
