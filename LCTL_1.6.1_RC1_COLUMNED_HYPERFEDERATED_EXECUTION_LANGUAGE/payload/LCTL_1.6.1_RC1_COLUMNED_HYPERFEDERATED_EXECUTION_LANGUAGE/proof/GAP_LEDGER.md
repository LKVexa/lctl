# Exact Gap Ledger — LCTL 1.0.0 Release Target

The package is a **verified development candidate**, not a fully qualified LCTL 1.0.0 release. This is intentional: the governing workflow requires fail-closed release qualification.

## Remaining blockers

- **Measurement:** arbitrary POVM definitions can be validated/modelled, but the local runtime does not yet implement general POVM shot sampling/post-measurement update.
- **Open systems:** canonical channels are executable; arbitrary user-defined Kraus channels and a general Lindblad integrator remain unimplemented.
- **Simulator profiles:** state-vector and density execution are implemented; a dedicated stabilizer-tableau engine and tensor-network engine are not.
- **Optimizer:** exact inverse cancellation and equivalence checking are present; the full required commutation/fusion/Pauli/Clifford/routing/scheduling/ancilla/depth optimization suite is incomplete.
- **Variational engine:** parameterized circuits and expectation semantics are present; a complete classical optimizer/convergence engine is not.
- **QEC:** a three-qubit repetition-code reference circuit is executable; a generalized QEC code-profile runtime is not.
- **Target qualification:** an OpenQASM textual adapter and feature matrix exist; no external hardware adapter has been qualified, and no vendor calibration authority is claimed.

## Deliberately not claimed
External hardware execution, vendor calibration authority, pulse-level semantics, fault-tolerant physical-qubit estimates, scalable tensor-network simulation, or automatic executable lowering of all 1,205 learned records.

## Transform honesty
The Calculus Transform is a typed semantic guide. Its asymptotic claims remain asymptotic; Θ-G bandwidth behavior, Θ-J lattice retraction, regularity, density drift, boundary conditions, convexity limits, and D14 obstructions remain visible.

## Classical-host / quantum-boundary qualification

- **IMPLEMENTED/VERIFIED:** runtime execution-class labels for symbolic verification, classical simulation, quantum compilation, and hardware estimates.
- **IMPLEMENTED/VERIFIED:** `physical-run` fails closed without a qualified physical-QPU adapter.
- **BLOCKED:** actual `PHYSICAL_QPU_EXECUTION` remains unavailable until a qualified hardware adapter with authenticated execution evidence is added.
- **Rule:** ordinary SSD/CPU execution must never be promoted to physical quantum execution.
