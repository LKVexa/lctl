# LCTL Topology Specification

A topology is a sealable target-neutral graph of nodes and links.

Node metadata may include device class, processor/QPU count, memory, gate set, connectivity, calibration age, coherence limits, trust domain, and failure domain.

Link metadata includes source, destination, type, latency, bandwidth, jitter/loss, and for quantum links fidelity, setup/heralding cost, success probability, and entanglement-generation information.

The reference verifier rejects self-links, unknown link classes, invalid fidelity, exceeded concurrency, crosstalk, coherence, or calibration budgets. `topology-check` emits a deterministic topology hash.
