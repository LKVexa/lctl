# Lesson 32 — Execution Provenance and Result Authority

**Objective:** prevent simulated, estimated, and physically measured results from being conflated.

**Normative rule:** every result is assigned an execution class and physical-boundary disposition.

**Valid example:** QASM generation is `QUANTUM_COMPILATION`; a density-matrix simulator run is `CLASSICAL_SIMULATION`; a target cost projection is `HARDWARE_ESTIMATE`.

**Invalid example:** calling a compiler resource report a QPU measurement.

**Verification exercise:** identify the minimum evidence required before `physical_qpu=true` may appear.

**Evidence boundary:** the reference runtime fails closed for `physical-run` because this package contains no qualified physical-QPU adapter.
