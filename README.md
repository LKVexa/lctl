# lctl

Explore LCTL from compact source to parallel execution in one repository. Three corpora bring together LCTL 1.6.0-RC1, LCTL 1.6.1-RC1, and Columned LCTL, including Java 21 reference runtimes, compiler tooling, 53 examples, training records, and a 34-chapter handbook. Study canonical verification and fabric design with explicit execution boundaries.

LCTL brings three related corpora into one repository: LCTL 1.6.0-RC1 Hyperfederated Execution Fabric, LCTL 1.6.1-RC1 Columned Hyperfederated Execution Language, and Columned LCTL Corpus 1.0.0. Together they connect compact program authoring, canonical verification, and host reference execution with educational and machine-facing material. The collection is useful for language researchers, runtime implementers, instructors, and code-model developers.

The 1.6.0 package explores placement, local parallel execution, distributed descriptors, adapters, and recovery plans. Version 1.6.1 adds the eight-column LCTL-C source form, which lowers into sealed 18-cell canonical LCTL. The teaching corpus supplies 53 compiler-verified examples, 365 row pairs, 1,838 instruction records, annotated programs, a 34-chapter handbook, and a 200-exercise workbook.

Shared audit repairs preserve full-policy roundtrips, strengthen malformed-input rejection, enforce canonical admission, and qualify test evidence. Archived negative records and descriptor inventories are distinguished from independently executed cases. These are reference and release-candidate assets: authenticated remote federation, physical QPU execution, complete restoration and merge semantics, and long soak qualification remain open. Each corpus retains its provenance and evidence boundaries.

## Corpora and audit records

### [LCTL_1.6.0_RC1_HYPERFEDERATED_EXECUTION_FABRIC](LCTL_1.6.0_RC1_HYPERFEDERATED_EXECUTION_FABRIC/README.md)

LCTL 1.6.0-RC1 is a Java 21 host reference package for exploring parallel and distributed execution language design. It builds on earlier LCTL layers and adds a hyperfederated execution interface, placement descriptors, adapter boundaries, and recovery descriptions. The package is intended for language researchers, runtime implementers, and engineers studying the relationship between canonical verification and execution planning.

Examples and launch scripts provide entry points into local work stealing, elastic worker growth, topology inspection, and calibration. The collection also contains runtime source and binaries, compiler components, conformance descriptors, benchmark artifacts, proof records, and audit material. These assets support close examination of admission checks, numeric constraints, canonical seals, and the separation of source authority from execution claims.

The updated audit corrects earlier conformance and authentication claims. Its 1.6 descriptor inventory is structurally checked rather than executed as 1,024 independent semantic tests. Checkpoint inspection does not restore runtime state, and aggregate output does not prove CRDT merge laws. This remains a release candidate: authenticated remote transport, physical QPU authority, extended soak testing, and several implementation gates remain open.

### [LCTL_1.6.1_RC1_COLUMNED_HYPERFEDERATED_EXECUTION_LANGUAGE](LCTL_1.6.1_RC1_COLUMNED_HYPERFEDERATED_EXECUTION_LANGUAGE/README.md)

LCTL 1.6.1-RC1 introduces LCTL-C 1.0, a compact authoring language layered over the existing Java 21 hyperfederated reference stack. Its eight source columns capture identifiers, lanes, operations, outputs, controls, inputs, arguments, and metadata, while defaults and frame policy carry stable semantics. The aim is to make programs easier to write and inspect without creating a second verification authority.

The package includes compact-language compiler source and tooling, grammar and specification assets, an alias catalog, migration guidance, design rationale, and 53 bundled examples. Commands support columnization, canonical lowering, verification, statistics, and roundtrip comparison. Every compact program lowers into sealed 18-cell canonical LCTL before the canonical verifier supplies admission authority.

The October 2026 repairs preserve complete bundle, frame, and row fields during roundtrips and reject malformed quoting, duplicate keys, and unfinished frames. Updated tests report their actual execution scope. The package is useful for compiler research, concise source design, and parallel-language education. It remains a release candidate with scoped local reference behavior; native federation, authenticated physical adapters, and long soak qualification are still open.

### [COLUMNED_LCTL_CORPUS_1.0.0](COLUMNED_LCTL_CORPUS_1.0.0/README.md)

The Columned LCTL Corpus is a teaching and machine-learning collection for LCTL-C 1.0 and LCTL 1.6.1-RC1. It focuses on reading, writing, explaining, lowering, and validating compact programs while retaining canonical LCTL as the source of semantic authority. It is intended for students, instructors, code-model researchers, parser authors, and verification-tool developers.

The package includes 53 compiler-verified programs, 365 compact-to-canonical row pairs, and 1,838 instruction records. Human study is supported by a 34-chapter handbook, 53 annotated examples, an operator catalog, and a 200-exercise workbook. Machine-facing data supports supervised transformation and diagnostic tasks, while compiler source, grammar, specifications, and a self-contained verification utility make the mappings inspectable.

The updated compiler preserves full bundle, frame, and row policy in roundtrips and rejects malformed or ambiguous compact input. The archived 512 negative records represent eight distinct fixtures, which the verifier exercises separately. Compiler execution requires Java 21; static mode reports its narrower scope. Compact syntax lowers to canonical 18-cell rows, and integration into a different execution profile requires a checked adapter.

Historical source archives and source manifests remain provenance records. Current publication file hashes are listed in `PUBLICATION_SHA256.json`; licensing changes are recorded in `PUBLICATION_LICENSE_LEDGER.json`.

## License

lctl (C) 2026 RUSSELL PHILIP SMITHSON

Licensed under the GNU General Public License version 3 only (`GPL-3.0-only`). See [LICENSE](LICENSE) and [NOTICE](NOTICE).
