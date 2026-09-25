# Lesson 31 — Classical Host, Standard SSD, and the Quantum Boundary

**Objective:** distinguish quantum language semantics from the physical implementation of a QPU.

**Normative rule:** LCTL source, compiler, verifier, QCIR, proof records, and simulators may reside on an ordinary SSD. This does not constitute physical quantum execution. `PHYSICAL_QPU_EXECUTION` requires qualified hardware evidence.

**Valid example:** a Bell-state LCTL source is verified on a laptop and classically simulated; the evidence says `CLASSICAL_SIMULATION`, `physical_qpu=false`, `quantum_boundary=NOT_CROSSED`.

**Invalid example:** labeling the same statevector file `PHYSICAL_QPU_EXECUTION` because it represents amplitudes.

**Verification exercise:** inspect runtime outputs for `verify`, `simulate`, `qcir`, and `physical-run` and explain why only the last command could ever cross the boundary.

**Evidence boundary:** semiconductor physics inside an SSD does not make its cells LCTL qubits.
