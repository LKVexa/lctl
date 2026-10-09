# Distributed Numerical Engine Specification

## Statevector
RC1 implements actual Java OS-process workers operating on a shared memory-mapped classical amplitude file. The amplitude vector is numerically sharded by worker index ranges. Each gate is executed by multiple worker processes using canonical pair ownership, followed by synchronization. The controller compares the result to a single-process reference and reports maximum amplitude error and deterministic file hash.

Supported reference distributed kernels:
I, X, Y, Z, H, S, SDG, T, TDG, RX, RY, RZ, PHASE, CX/CNOT, CZ, SWAP.

Measurements are not executed in the sharded kernel; measurement rows are skipped for pre-measurement state equivalence. Noise channels are not silently approximated.

## Density matrix
RC1 implements multi-process construction of a pure-state density matrix from a distributed statevector, with distributed row blocks, trace check, Hermiticity check, and density hash. General sharded channel evolution is `IMPLEMENTED_PARTIAL`.

## Stabilizer
RC1 includes a dedicated binary symplectic stabilizer-tableau engine for the Clifford subset H, S, X, Z, CX/CNOT. It is not a statevector alias.

All engines are classical numerical computation.
