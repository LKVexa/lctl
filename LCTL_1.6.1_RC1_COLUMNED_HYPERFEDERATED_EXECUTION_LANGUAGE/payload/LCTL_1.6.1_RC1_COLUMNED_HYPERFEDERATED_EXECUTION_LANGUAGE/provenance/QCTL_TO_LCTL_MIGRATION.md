# QCTL → LCTL Identifier Migration Record

This release changes the active language identifier from **QCTL** to **LCTL** at the user's direction.

The migration is semantic-preserving with respect to the quantum language core. The active source magic, profile namespace, file extension, runtime class/launcher names, specification filenames, corpus references, learned-admission namespace, manifest records, evidence labels, and release gates now use **LCTL**.

Historical identifier **QCTL** appears here only as provenance for the migration from the immediately preceding development candidate. QCIR, QFRAME, and QTUPLE retain their names because they identify quantum intermediate/frame/tuple structures rather than the language brand.

This release also adds a normative classical-host/quantum-boundary model. A source file, compiler, verifier, simulator, QCIR artifact, or target adapter may reside on an ordinary SSD. That does not make the SSD a quantum processor. Physical quantum execution may be asserted only when a qualified QPU target actually realizes quantum state evolution and returns measurement evidence.

## Authority hashes

- Pre-migration workflow SHA-256: `5b60c1a8c58799fc5730bb0f94e33a9fda2dc29273ade83e539d7595f08a9c6c`
- Active LCTL workflow SHA-256: `8cf3a556f95cba885e036cd787b83c056cf0bd46c5a21c8b1a72a63b111c23ee`
- Calculus Transform SHA-256: `df5e603ebcf5b202c76707ed374ceae464907bbe7665078dddba741dcbdfcbd3`
