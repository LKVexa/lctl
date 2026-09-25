# QCIR - Quantum Column Intermediate Representation

QCIR/1.0 is deterministic JSON emitted from verified LCTL. It preserves source hash, qubit/classical dimensions, row identity, operation, control/target operands, parameters, semantic regime and proof identifier. The reference conformance suite hashes repeated QCIR emissions to establish serialization determinism.

## Parallel QCIR extension

QCIR 1.1 preserves `lane`, frame `tick`, causal predecessor, resource set, barrier scope, coupling edges, interface edges, communication metadata, and target-parallel restrictions. A derived **PQIR schedule view** MAY group QCIR operations into deterministic layers without replacing QCIR as the canonical semantic IR.
