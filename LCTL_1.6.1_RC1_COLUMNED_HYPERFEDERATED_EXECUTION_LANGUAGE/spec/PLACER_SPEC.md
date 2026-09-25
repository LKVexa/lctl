# Topology-Calibrated Placer Specification

RC1 provides deterministic topology binding and placement from a partition plan to nodes read from LCTL topology JSON.

The reference placer:
- deterministically orders available topology nodes;
- assigns partitions reproducibly;
- emits per-placement capacity, memory, topology, and calibration fields;
- deliberately refuses to imply that an abstract QPU node is calibrated physical hardware.

The current placement cost optimizer is reference-grade, not globally optimal. Calibrated qubit-connectivity and crosstalk affinity optimization remain partial.
