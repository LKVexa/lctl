# LCTL Global Quantum Ownership

Unknown quantum state has one authoritative ownership lineage across lanes, processes, logical nodes, partitions, and QPU regions.

The reference verifier implements `QSTATE_BIND`, `QSTATE_MOVE`, `REMOTE_QOP`, `QSTATE_COPY`, and `SERIALIZE_QSTATE` rules. Copying an unknown quantum state is rejected. A remote operation is rejected when the named node is not the current owner.

State relocation is semantically distinct from replication. Admitted mechanisms include local move/SWAP, teleportation, or target-specific transfer backed by proof. Re-preparation is allowed only when the state is classically known.

Simulator checkpoint files are classical numerical artifacts and do not constitute physical-state cloning.
