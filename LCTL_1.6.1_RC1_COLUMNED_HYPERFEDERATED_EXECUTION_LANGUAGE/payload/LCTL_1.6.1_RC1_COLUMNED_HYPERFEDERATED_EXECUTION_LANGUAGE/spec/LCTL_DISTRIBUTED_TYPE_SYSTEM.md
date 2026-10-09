# LCTL Distributed Type System

LCTL 1.2 introduces target-neutral semantic classes for distributed systems. A source-level declaration does not imply real hardware.

## Logical types
`node`, `cluster`, `partition`, `shard`, `route`, `event`, `future`, `communicator`, `classical_channel`, `quantum_link`, `remote_handle`, `failure_domain`, and `checkpoint_scope`.

## Device descriptors
`cpu`, `gpu`, `qpu`, `memory_domain`, and `numa_domain` are descriptors until target binding. A `qpu` descriptor is not a claim that a physical quantum processor is available.

## Separation rules
Classical channels carry classical information. Quantum links carry quantum-network resource semantics such as fidelity, entanglement setup, heralding, and probabilistic success. They are never reduced to a classical byte pipe.

## Exactness
All distributed metadata carries the ordinary LCTL exactness regime. Approximate or linearized metadata must carry an error bound where the reference verifier requires it.
