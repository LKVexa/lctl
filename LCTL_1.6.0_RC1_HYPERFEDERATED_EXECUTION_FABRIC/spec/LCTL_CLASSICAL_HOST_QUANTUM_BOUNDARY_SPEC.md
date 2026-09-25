# LCTL Classical Host and Quantum Boundary Specification
## Normative profile: LCTL-QB/1.0

## 1. Purpose
LCTL defines quantum-computing semantics independently of the medium used to store source code, compiler artifacts, proof records, simulators, or target-lowering artifacts. An ordinary SSD is a **classical persistence medium**. It may hold exact or approximate classical representations of quantum objects, but storage on an SSD is never evidence of physical superposition, entanglement, interference, or measurement.

## 2. Normative separation
LCTL SHALL distinguish three layers:

`LCTL semantic program -> classical reference/compilation authority -> optional physical quantum target`

The source language remains quantum-native because its semantics enforce quantum state, operator, tensor, measurement, channel, ownership/no-cloning, error, resource, and quantum/classical-control rules. The host machine need not itself be quantum.

## 3. Execution classes
Every executable or analytical result SHALL be classifiable as exactly one primary execution class:

- `SYMBOLIC` — parsing, type checking, proof checking, static verification, or source inspection. No quantum state is physically or numerically evolved unless separately declared.
- `CLASSICAL_SIMULATION` — a CPU/RAM/GPU classical numerical representation evolves according to LCTL quantum semantics. This is not physical quantum execution.
- `QUANTUM_COMPILATION` — LCTL/QCIR is rewritten, optimized, scheduled, resource-accounted, or lowered toward a quantum target. This is not physical quantum execution.
- `HARDWARE_ESTIMATE` — resource/noise/runtime predictions about a target. Estimates are not measurements from that target.
- `PHYSICAL_QPU_EXECUTION` — a qualified QPU adapter submits the lowered program to physical quantum hardware and returns target-authenticated execution/measurement evidence.

Support tools MAY use more specific sub-classes, but they MUST map to one of these primary classes.

## 4. State-realization classes
A result SHALL distinguish the semantic quantum object from its realization:

- `SYMBOLIC_QUANTUM_OBJECT`
- `CLASSICAL_NUMERIC_STATEVECTOR`
- `CLASSICAL_NUMERIC_DENSITY`
- `CLASSICAL_STABILIZER_OR_TENSOR_REPRESENTATION`
- `TARGET_LOWERED_PROGRAM`
- `PHYSICAL_QUANTUM_STATE_UNOBSERVED`
- `PHYSICAL_MEASUREMENT_RESULT`

The runtime SHALL NOT call a classical amplitude vector, density matrix, tensor-network state, stabilizer tableau, disk file, or QCIR record a physical quantum state.

## 5. Quantum-boundary rule
The **quantum boundary** is crossed only when a qualified target adapter transfers a target-lowered program into an authenticated physical QPU execution context. The boundary is not crossed by source authoring, SSD storage, parsing, compilation, simulation, optimization, QASM generation, resource estimation, or remote API preparation alone.

A record claiming `PHYSICAL_QPU_EXECUTION` MUST include at minimum:

1. target adapter identity and version;
2. target/hardware identity;
3. execution/job identity or equivalent authenticated receipt;
4. lowering artifact hash;
5. calibration/context identifier when applicable;
6. measurement/result evidence;
7. timestamp or monotonic execution sequence;
8. proof/evidence status;
9. explicit statement that the physical boundary was crossed.

If these cannot be produced, the claim SHALL fail closed.

## 6. Classical-storage doctrine
LCTL SHALL be storage-medium agnostic for language authority. Source and artifacts may live on SSD, HDD, flash, optical media, RAM disk, or another classical store. Storage characteristics may affect performance, durability, confidentiality, and reproducibility, but they do not alter quantum semantics.

## 7. No-quantum-by-description rule
The following are prohibited claims in LCTL evidence unless physical QPU evidence exists:

- that SSD cells constitute program qubits;
- that a stored amplitude table is physical superposition;
- that two classical files are entangled;
- that classical random sampling is quantum measurement;
- that CPU/GPU matrix multiplication is itself physical unitary quantum evolution;
- that semiconductor quantum effects in ordinary storage confer programmable quantum-computing capability.

## 8. Fail-closed evidence
A tool SHALL report `physical_qpu=false` and `quantum_boundary=NOT_CROSSED` for symbolic, simulation, compilation, and estimate outputs. A physical-execution command without a qualified adapter SHALL terminate with a blocker rather than silently simulate.

## 9. Relation to the Calculus Transform
The Calculus Transform remains a mathematical authority for continuum limits, spectral/variational structure, derivatives, retractions, quantified approximation, and preserved obstructions. It does not convert classical storage or simulation into a physical quantum device. Approximation hypotheses, error rates, and retraction conditions travel with every applicable claim.
