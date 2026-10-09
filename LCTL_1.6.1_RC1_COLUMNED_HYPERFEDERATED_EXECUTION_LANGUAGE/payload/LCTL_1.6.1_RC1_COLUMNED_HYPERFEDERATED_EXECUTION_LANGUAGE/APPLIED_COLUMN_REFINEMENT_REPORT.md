# LCTL 1.6.1-RC1 Column Language Refinement Report

## Result

The LCTL 1.6.0-RC1 hyperfederated runtime has been refined into **LCTL 1.6.1-RC1 with LCTL-C 1.0**, an additive compact column source language.

The refinement is syntactic and ergonomic, not a weakening of semantic authority. Compact `.lctlc` source is always lowered into sealed canonical LCTL before the existing verifier/runtime is trusted.

## Compression

- Compact row columns: **8**
- Canonical row cells: **18**
- Repeated cells removed from normal source rows: **10**
- Bundled examples converted: **53**
- Semantic round-trip: **53/53 PASS**
- Aggregate compact bytes: **58,857**
- Aggregate lowered canonical bytes: **123,330**
- Aggregate source reduction: **52.28%**
- Median per-example reduction: **51.15%**

## Column surface

`ID │ LANE │ OP │ OUT │ CTRL │ IN │ ARG │ META`

`IN` represents canonical `A`, or `A›B` when a second input operand is required. `ARG` is canonical `PARAM`. `META` contains only semantic overrides that cannot be inferred or inherited.

## Runtime integration

Added native Java 21 support for:

- `column-compile`
- `columnize`
- `column-verify`
- `column-stats`
- `column-roundtrip`
- `column-selftest`
- transparent compact-source lowering for canonical `verify`, `inspect`, `simulate`, `resources`, `qcir`, `qasm`, `opt-check`, and `provenance`;
- transparent compact-source lowering for the LCTL 1.6 hyperfederated planning commands.

## Compatibility

Canonical `.lctl` source remains supported and unchanged. The 1.6 smoke suite still passes after introducing the compact layer. No existing semantic invariant was intentionally removed.

## Qualification boundary

This remains a development candidate. Long 1-hour/8-hour/24-hour soak gates remain not run, and physical multi-QPU authority remains externally blocked. The column refinement does not alter that boundary.
