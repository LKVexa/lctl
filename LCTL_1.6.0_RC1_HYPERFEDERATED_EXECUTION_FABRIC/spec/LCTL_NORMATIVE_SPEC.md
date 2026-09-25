# LCTL Normative Specification 1.0

LCTL models controlled evolution, coupling, transformation, observation, and verification of typed quantum state. Source is ledger-shaped and deterministic. The bundle, frame and row layers are separately sealed with SHA-256 over LF-normalized canonical text.

The core is target-independent. `BACKEND=none`, `NETWORK=deny`, and deterministic seeded replay are normative for the reference profile. External hardware is only an explicit adapter target.

A quantum statement carries `REGIME`, `ASSUME`, `ERROR`, `CONF`, and `PROOF`; approximation may never masquerade as exact. Unsupported and no-faithful-form outcomes are valid semantic results.

The local reference profile supports pure-state and density-matrix execution, explicit measurement, standard unitary primitives, explicit canonical noise channels, hybrid feedback, Pauli expectations, QCIR serialization, resource accounting, exact rewrite checks, and OpenQASM textual lowering for a supported subset.


## Classical-host / quantum-boundary doctrine
LCTL language authority is independent of storage medium. Ordinary SSD/HDD/flash storage is classical persistence. Local simulation is a classical mathematical realization of quantum semantics, not physical quantum state evolution. All result-producing implementations SHALL distinguish symbolic verification, classical simulation, quantum compilation, hardware estimation, and physical QPU execution. A physical-execution assertion requires qualified QPU evidence and SHALL fail closed otherwise. See `LCTL_CLASSICAL_HOST_QUANTUM_BOUNDARY_SPEC.md` and `LCTL_EXECUTION_PROVENANCE_SPEC.md`.

# Version 1.1 Parallel-Language Amendment

LCTL 1.1 adds the `lctl.quantum.parallel` profile. The parallel profile is a strict semantic extension of the quantum core: exact quantum state/operator semantics remain unchanged, while lane, tick, dependency, coupling, communication, locality, reliability, and physical-parallel qualification become first-class.

Normative rule: **logical parallelism does not imply physical parallelism**. Parallel schedule evidence generated on a classical host remains `QUANTUM_COMPILATION` with `physical_qpu=false` and `physical_parallel=false` until qualified target evidence exists.

Normative parallel metadata faces are `PARALLEL`, `COUPLING`, and `FIELD`. Executable synchronization primitives include `BARRIER`, `SYNC`, and `FENCE`; in the reference simulator they are state-neutral causal markers.


## LCTL 1.2 Parallel/Distributed Extension

The active extension profile is `lctl.quantum.parallel.distributed`. See `LCTL_PARALLEL_DISTRIBUTED_NORMATIVE_SPEC.md` and the distributed specification set. The 1.2 profile preserves all 1.1 quantum-boundary and proof/error-honesty rules.
