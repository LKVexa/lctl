# Target Adapter Specification

Adapters declare `SUPPORTED / APPROXIMATE / UNSUPPORTED / TARGET_SPECIFIC`. The included OpenQASM 3 text adapter lowers its exact supported subset and emits explicit comments for operations requiring additional lowering or target-specific handling. Unsupported semantics are never silently dropped. A generic hardware feature matrix is included under `compiler/targets/`.

## Parallel target capability matrix

A target adapter SHALL declare whether logical parallel operations can be physically concurrent, including control-channel limits, topology, simultaneous-gate constraints, crosstalk exclusions, measurement concurrency, reset concurrency, classical-feedback latency, remote-entanglement/communication capability, and calibration validity. Unsupported physical concurrency lowers to serialization unless serialization would change semantics or violate a declared timing requirement, in which case compilation fails closed.
