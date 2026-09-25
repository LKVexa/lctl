# LCTL Failure and Resilience Model

Failure is a typed semantic input, including node/process/link failure, timeout, packet loss, quantum-link failure, calibration expiry, QPU unavailability, coherence exhaustion, resource exhaustion, target drift, deadlock, and ownership ambiguity.

Recovery may retry shots, reroute, rebind a target, regenerate entanglement, restart from known preparation, or resume from classical state. It may not reconstruct an arbitrary unknown physical quantum state from a disk checkpoint.

`failure-report` summarizes declared failure classes. The reference package demonstrates orchestration semantics but does not claim hardware fault tolerance.
