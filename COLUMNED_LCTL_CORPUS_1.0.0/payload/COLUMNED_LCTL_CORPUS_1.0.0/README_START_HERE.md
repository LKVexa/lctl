
# COLUMNED LCTL CORPUS 1.0.0
## Full Human + Machine Corpus for LCTL-C 1.0 / LCTL 1.6.1-RC1

This package is designed to teach a human, code model, parser author, or verifier how to **read, write, lower, explain, and validate Columned LCTL**.

### What is inside

The corpus contains 53 compiler-verified programs, 365 compact-to-canonical row pairs, 1838 instruction records, 512 fail-closed negative records, a 34-chapter handbook, all 53 annotated examples, a 200-exercise workbook, the actual compact-language compiler source, grammar/specification, and a self-contained verification utility.

Across the verified example suite, compact source occupies 58,857 bytes versus 123,330 bytes after canonical lowering, a 52.277% aggregate source reduction.

### Start here

1. Read `handbook/00_orientation.md` through `handbook/10_declarations.md`.
2. Use `reference/OPERATOR_CATALOG.md` while writing.
3. Study `examples/annotated/01_bell_state.md`, then the parallel and distributed examples.
4. Use `machine/row_pairs.jsonl` for supervised compact→canonical training.
5. Run `python validation/VERIFY_CORPUS.py` to verify corpus integrity.

### Semantic rule

**Compact syntax is never the final authority.** LCTL-C lowers to canonical 18-cell LCTL, and semantic/runtime authority remains with the canonical verifier and execution stack.

## Audit upgrade 2026-10-09

The compiler now preserves every bundle/frame/row field in roundtrip checks and rejects unknown or duplicate keys, malformed quoting and unfinished frames. Run `python validation/VERIFY_CORPUS.py` with Java 21 on PATH, or set `JAVA` to its executable path. `--static` performs limited dataset/integrity checks and reports that compiler execution was skipped. The 512 archived negative records contain 8 unique samples; the verifier executes those 8 distinct cases. The source runtime is retained in `authority/source_runtime/`, and source manifest in `authority/MANIFEST.source.json`. Existing source certificates remain historical; current receipts describe the rebuilt runtime.
