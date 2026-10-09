# COLUMNED_LCTL_CORPUS_1.0.0

Learn to read, write, lower, and validate Columned LCTL through all 53 compiler-verified examples, compact-to-canonical row pairs, instruction records, and diagnostic fixtures. A 34-chapter handbook and 200-exercise workbook support human instruction and model training, while the included compiler preserves canonical LCTL as the semantic authority.

The Columned LCTL Corpus is a teaching and machine-learning collection for LCTL-C 1.0 and LCTL 1.6.1-RC1. It focuses on reading, writing, explaining, lowering, and validating compact programs while retaining canonical LCTL as the source of semantic authority. It is intended for students, instructors, code-model researchers, parser authors, and verification-tool developers.

The package includes 53 compiler-verified programs, 365 compact-to-canonical row pairs, and 1,838 instruction records. Human study is supported by a 34-chapter handbook, 53 annotated examples, an operator catalog, and a 200-exercise workbook. Machine-facing data supports supervised transformation and diagnostic tasks, while compiler source, grammar, specifications, and a self-contained verification utility make the mappings inspectable.

The updated compiler preserves full bundle, frame, and row policy in roundtrips and rejects malformed or ambiguous compact input. The archived 512 negative records represent eight distinct fixtures, which the verifier exercises separately. Compiler execution requires Java 21; static mode reports its narrower scope. Compact syntax lowers to canonical 18-cell rows, and integration into a different execution profile requires a checked adapter.

Part of the [lctl repository](https://github.com/LKVexa/lctl).

See [AUDIT_AND_UPGRADES.md](AUDIT_AND_UPGRADES.md) for release changes and evidence boundaries.

## License

lctl (C) 2026 RUSSELL PHILIP SMITHSON

Licensed under the GNU General Public License version 3 only (`GPL-3.0-only`). See [LICENSE](LICENSE).
