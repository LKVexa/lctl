# COLUMNED LCTL CORPUS 1 0 0 Updated Corpus Audit

This updated copy supplies human column authoring, explicit metadata, canonical lowering and roundtrip equivalence. Its import boundary is: Require a checked profile adapter before mapping existing LCTL rows into LCUF.

## Source and coverage

Original input: `COLUMNED_LCTL_CORPUS_1.0.0.zip`. Source SHA 256: `b60b268a05b817bd7a9865f77f36b26b3952ef9bc28023eb3a8216b908bb634e`. The source archive is preserved in the parent folder. All 226 original leaf files were read and hashed; 2,768 stored JSONL rows were structurally parsed, including historical/reconstructed copies. Nested ZIPs are materialized in `.expanded` directories. Dataset row counts include duplicate representations and metadata headers; they are not unique executable programs.

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

### LCTL-009 Medium priority

Corpus verifier did not validate compiler behavior or linked targets

Status: **APPLIED**. Check exact source/target projections and training targets, run all53 compiler verifications/full-field roundtrips/current canonical lowerings, execute8 distinct negative fixtures, expose records vs uniqueness, support explicit static scope.

Evidence: `validation/VERIFY_CORPUS.py | machine/negative_cases.jsonl`.

### BASE-01 Medium priority

Nested packaging, raw modeled labels and historical copies need explicit current provenance and reuse boundaries.

Status: **APPLIED**. Expanded materialized payload; source and updated manifests; change ledger; dataset-bound qualification; individual audit and fabric role.

Evidence: `audit/source_manifest.json | audit/updated_manifest.json | audit/dataset_qualification.json`.

Remaining gate: Require a checked profile adapter before mapping existing LCTL rows into LCUF.

## Contribution to the new fabric language

Profile owner: `lctl.columned`. Human column authoring, explicit metadata, canonical lowering and roundtrip equivalence. Require a checked profile adapter before mapping existing LCTL rows into LCUF.

The [adapter contract](audit/fabric_adapter_contract.json) is a proposed interface with executable_adapter_implemented=false. It preserves source authority rather than using a feature label as inferred semantics. See [the collection architecture](../../fabric_language/ARCHITECTURE.md) and the [8S foundation](../../fabric_language/profiles/coupling8s/PROFILE.md).

## Qualification and verification

Use [dataset qualifications](audit/dataset_qualification.json) before training, importing or treating a record as evidence. A modeled pass or a structural validator pass is not native execution, authenticated federation, race proof, or physical device evidence. Current JA and DeepML technical records carry additional qualifications; deliberate negative examples remain negative examples.

The release provides an offline integrity checker: run `python tools/verify_release.py --deep` from the collection root. Per-profile test reports and validators qualify the exact behavior they checked. Complete grammars, native adapters, authenticated transports and a bootable unikernel remain separately tracked implementation gates.
