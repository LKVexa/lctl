# LCTL Causal DAG Specification

Before distributed lowering, the reference compiler can emit `LCTL-CAUSAL-DAG/1.0`.

Vertices contain source ID, operator, lane, and resource set. Edges carry reasons such as `QUANTUM_OWNERSHIP`, `DATA_DEPENDENCY`, `BARRIER`, `USER_ORDER`, and `MEASUREMENT_DEPENDENCY`.

The DAG is evidence for why operations may or may not be concurrent. Same-resource quantum operations create ownership edges. Explicit dependency rows create user-order edges. Measurement-dependent distributed rows create causal feedback edges.

This RC does not yet prove a globally minimum-span schedule; it emits deterministic conservative schedules and a causal evidence graph.
