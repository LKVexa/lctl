# LCTL_1.6.1_RC1_COLUMNED_HYPERFEDERATED_EXECUTION_LANGUAGE

Write compact quantum and parallel programs with LCTL-C 1.0 in LCTL 1.6.1-RC1. Eight-column source lowers into sealed 18-cell canonical LCTL, preserving verification authority. Explore 53 examples, compiler source, grammar, migration guides, and audit records to study concise authoring, policy-preserving roundtrips, and the local reference runtime.

LCTL 1.6.1-RC1 introduces LCTL-C 1.0, a compact authoring language layered over the existing Java 21 hyperfederated reference stack. Its eight source columns capture identifiers, lanes, operations, outputs, controls, inputs, arguments, and metadata, while defaults and frame policy carry stable semantics. The aim is to make programs easier to write and inspect without creating a second verification authority.

The package includes compact-language compiler source and tooling, grammar and specification assets, an alias catalog, migration guidance, design rationale, and 53 bundled examples. Commands support columnization, canonical lowering, verification, statistics, and roundtrip comparison. Every compact program lowers into sealed 18-cell canonical LCTL before the canonical verifier supplies admission authority.

The October 2026 repairs preserve complete bundle, frame, and row fields during roundtrips and reject malformed quoting, duplicate keys, and unfinished frames. Updated tests report their actual execution scope. The package is useful for compiler research, concise source design, and parallel-language education. It remains a release candidate with scoped local reference behavior; native federation, authenticated physical adapters, and long soak qualification are still open.

Part of the [lctl repository](https://github.com/LKVexa/lctl).

See [AUDIT_AND_UPGRADES.md](AUDIT_AND_UPGRADES.md) for release changes and evidence boundaries.

## License

lctl (C) 2026 RUSSELL PHILIP SMITHSON

Licensed under the GNU General Public License version 3 only (`GPL-3.0-only`). See [LICENSE](LICENSE).
