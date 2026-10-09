# LCTL 1 6 1 RC1 COLUMNED HYPERFEDERATED EXECUTION LANGUAGE Updated Corpus Audit

This updated copy supplies column authoring integrated with fabric descriptions and canonical rows. Its import boundary is: Use full-policy fingerprints; no native federation execution claim.

## Source and coverage

Original input: `LCTL_1.6.1_RC1_COLUMNED_HYPERFEDERATED_EXECUTION_LANGUAGE.zip`. Source SHA 256: `f4bb3c50e16f40cb243fc25c134d997b93efb09ee24dfff2b2e6c0a912fe25c5`. The source archive is preserved in the parent folder. All 3,346 original leaf files were read and hashed; 3,630 stored JSONL rows were structurally parsed, including historical/reconstructed copies. Nested ZIPs are materialized in `.expanded` directories. Dataset row counts include duplicate representations and metadata headers; they are not unique executable programs.

Original checks and locations are in [the source manifest](audit/source_manifest.json). Current payload bytes are in [the updated manifest](audit/updated_manifest.json), and changes are in [the ledger](audit/change_ledger.json). Embedded source layers have [separate coverage](audit/embedded_layer_coverage.json). Historical hashes and modeled labels retain their historical meaning. The updated manifest and qualification sidecars govern this release.

## Upgrades and open implementation work

### LCTL-001 High priority

Roundtrip could certify loss of bundle or frame policy

Status: **APPLIED**. Serialize every bundle and frame field, preserve exact row confidence spelling, quote/unescape exactly, and fingerprint length-delimited complete arrays.

Evidence: `authority/LctlColumnMain.java | runtime/src/LctlColumnMain.java`.

### LCTL-002 High priority

Malformed compact source silently accepted

Status: **APPLIED**. Reject unknown/duplicate keys and semantic alias collisions, malformed quotes, wrong directive order, unfinished frames, duplicate headers/IDs, and empty frames. Invalid commands and failed roundtrips now return a failure exit.

Evidence: `authority/LctlColumnMain.java | runtime/src/LctlColumnMain.java`.

### LCTL-003 Medium priority

Column selftest counts exceeded executed cases

Status: **APPLIED**. Report 1 positive and 1 distinct negative case, add 120-check semantic-policy regression including all 53 roundtrips and 14 malformed variants, retain historical artifacts as source evidence.

Evidence: `authority/LctlColumnMain.java | runtime/src/LctlColumnMain.java | MANIFEST.json`.

### LCTL-004 Medium priority

Grammar referenced undefined terminals

Status: **APPLIED**. Complete lexical definitions and explain accepted forms, restricted key sets, required structure and uniqueness constraints.

Evidence: `authority/LCTL_C_GRAMMAR.ebnf | language/LCTL_C_GRAMMAR.ebnf`.

### LCTL-005 High priority

1.6 descriptor counts were presented as semantic conformance

Status: **APPLIED_SCOPE_CORRECTION_WITH_OPEN_IMPLEMENTATION**. Add descriptor schema/identity validation; report PASS_WITH_SCOPE, descriptor_execution=false, actual repeated selftest coverage, and full_conformance_verified=false. Building executable inputs for every named group remains open.

Evidence: `tools/run_lctl16_conformance.py | runtime/src/LctlHyperFederatedMain.java | conformance/1_6/positive/*.json | conformance/1_6/negative/*.json`.

### LCTL-006 High priority

1.5 runner ignored individual descriptor data

Status: **APPLIED**. Read and validate all 512 descriptors, pass exact inputs to a batched Java runner, execute 768 underlying reference calls/package, enforce invalid-type error specificity. Results remain scoped because CRDT algebra is not implemented.

Evidence: `tools/run_lctl15_conformance.py`.

### LCTL-007 High priority

Unsigned adapter text could become authenticated evidence

Status: **APPLIED_FAIL_CLOSED_WITH_OPEN_AUTHENTICATION**. Declared fields no longer become authentication; verified flags remain false, all physical requests remain BLOCKED_EXTERNAL_AUTHORITY, blocking propagates through wrapper and CLI exits3. Signed structured receipt validation remains open.

Evidence: `runtime/src/LctlFederatedMain.java | runtime/src/LctlHyperFederatedMain.java`.

### LCTL-008 High priority

Reference aggregates and checkpoint text overstated implementation

Status: **APPLIED_SCOPE_CORRECTION_WITH_OPEN_IMPLEMENTATION**. Report merge laws as unknown and merge_laws_verified=false; report runtime_state_restored=false and operation=CHECKPOINT_METADATA_INSPECTION. Real merge algebra and validated complete-state restoration remain open.

Evidence: `runtime/src/LctlFederatedMain.java`.

### LCTL-010 Medium priority

Missing status/checksum records could be accepted

Status: **APPLIED**. Require explicit PASS/status evidence, bound runtime calls, reject malformed/duplicate/escaping/missing checksum records, preserve source hashes in provenance and rebuild current checksum manifests.

Evidence: `tools/run_lctl14_conformance.py | tools/verify_package_checksums.py`.

### LCTL-011 High priority

Nonfinite numeric cells bypassed comparison-based bounds

Status: **APPLIED**. Require Double.isFinite in the canonical numeric decoder, rebuild every exposed runtime jar and mirror the updated column tooling jar. Reject 16 resealed variants spanning four numeric contexts and four nonfinite spellings in both core and fabric admission.

Evidence: `runtime/src/LctlMain.java | tests/NumericAndAdmissionRegression.java`.

### LCTL-012 High priority

Fabric entry points bypassed canonical seals and admission

Status: **APPLIED**. Make canonical verification mandatory at the fabric boundary and derive the program from the verified in-memory snapshot, binding its original source digest without rereading mutable source. Remove unsupported source magic claims and exercise five admission variants with explicit core/fabric rejection checks. Rebuild all five exposed runtime jars per package.

Evidence: `runtime/src/LctlFabricMain.java | runtime/src/LctlMain.java | MANIFEST.json | tests/NumericAndAdmissionRegression.java`.

### BASE-23 Medium priority

Nested packaging, raw modeled labels and historical copies need explicit current provenance and reuse boundaries.

Status: **APPLIED**. Expanded materialized payload; source and updated manifests; change ledger; dataset-bound qualification; individual audit and fabric role.

Evidence: `audit/source_manifest.json | audit/updated_manifest.json | audit/dataset_qualification.json`.

Remaining gate: Use full-policy fingerprints; no native federation execution claim.

## Contribution to the new fabric language

Profile owner: `lctl.columned.fabric.1.6.1`. Column authoring integrated with fabric descriptions and canonical rows. Use full-policy fingerprints; no native federation execution claim.

The [adapter contract](audit/fabric_adapter_contract.json) is a proposed interface with executable_adapter_implemented=false. It preserves source authority rather than using a feature label as inferred semantics. See [the collection architecture](../../fabric_language/ARCHITECTURE.md) and the [8S foundation](../../fabric_language/profiles/coupling8s/PROFILE.md).

## Qualification and verification

Use [dataset qualifications](audit/dataset_qualification.json) before training, importing or treating a record as evidence. A modeled pass or a structural validator pass is not native execution, authenticated federation, race proof, or physical device evidence. Current JA and DeepML technical records carry additional qualifications; deliberate negative examples remain negative examples.

The release provides an offline integrity checker: run `python tools/verify_release.py --deep` from the collection root. Per-profile test reports and validators qualify the exact behavior they checked. Complete grammars, native adapters, authenticated transports and a bootable unikernel remain separately tracked implementation gates.
