# Quantum Ownership Discipline


Columned syntax inherits the canonical ownership rules. Unknown quantum state cannot be cloned for convenience. Use `QBIND`/`QSTATE_BIND` to establish ownership handles and `QMOVE`/`QSTATE_MOVE` for admitted ownership migration.

`QSTATE_COPY` and `SERIALIZE_QSTATE` may exist in the distributed vocabulary so the verifier can identify or reject forbidden actions; their presence in the grammar is not permission to copy an unknown physical state.

Keep ownership claims explicit and fail closed when ownership is ambiguous.
