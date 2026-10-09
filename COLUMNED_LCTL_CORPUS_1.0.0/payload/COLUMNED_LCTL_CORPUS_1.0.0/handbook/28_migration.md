# Migration from Canonical LCTL


Use `columnize` to convert canonical `.lctl` into compact `.lctlc`, then `column-roundtrip` to check semantic equivalence. Use `column-compile` to reconstruct the sealed canonical unit.

A migration should retain the canonical original until the compact source has passed verification and round-trip checks. The corpus includes 53 paired programs showing both representations.
