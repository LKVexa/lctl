# LCTL Federation 1.5 Reference Specification

## Objects
`FEDERATION`, `EXECUTION_DOMAIN`, `DOMAIN_GROUP`, `WORKER_GROUP`, `PLACEMENT_SET`, `ROUTE_SET`, `FAILURE_DOMAIN`, `TRUST_DOMAIN`, `CONSISTENCY_DOMAIN`, `RESOURCE_POOL`, `EBIT_POOL`, `MEMORY_POOL`, `TASK_POOL`, `SHOT_POOL`, `PARAMETER_POOL`, and `CIRCUIT_POOL` are first-class planning objects in the 1.5 reference layer.

Every object carries identity, scope/ownership, membership, capabilities, limits, trust, failure boundary, migration/replication/consistency policy and provenance.

## Ownership invariant
Unknown quantum state is single-owner. It is not actor-replicated, CRDT-merged, speculatively duplicated, or serialized as arbitrary checkpoint bytes. Migration requires ownership transfer or an admitted quantum protocol.

## Federation boundary
The packaged federation is a local/offline reference implementation. Logical QPU domains and logical quantum routes are never labeled physical. External/physical execution requires Adapter ABI 3 evidence and the physical evidence firewall.
