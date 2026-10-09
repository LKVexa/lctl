# Canonical 18-Cell Lowering


Each compact row lowers to:

`ROW ¦ FACE ¦ LANE ¦ QSPACE ¦ OP ¦ OUT ¦ CTRL ¦ A ¦ B ¦ PARAM ¦ TYPE ¦ BASIS ¦ REGIME ¦ ASSUME ¦ ERROR ¦ RESOURCE ¦ CONF ¦ PROOF`

IDs and row order are preserved. Frames and bundles receive deterministic SHA-256 seals. The canonical verifier runs after lowering, so compact syntax cannot claim authority merely because it parsed.

Use `column-compile` when you need the sealed canonical artifact and `column-verify` when you need compact-source verification.
