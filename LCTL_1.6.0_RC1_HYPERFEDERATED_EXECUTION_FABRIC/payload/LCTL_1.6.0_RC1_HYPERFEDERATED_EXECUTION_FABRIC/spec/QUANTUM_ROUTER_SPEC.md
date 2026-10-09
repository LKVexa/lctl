# Quantum / Entanglement Router Specification

Quantum routing is modeled separately from classical communication. Reference quantum edges carry latency, fidelity, and success probability.

RC1 can discover a direct or multi-hop quantum path using an expected-cost metric that penalizes infidelity and failure probability, and reports path fidelity/success as products of edge values.

This is a reference logical/emulation model. It is not evidence of a physical quantum network. Ebit inventory, time-expanded routing, purification-aware routing, min-cost flow, and multi-party Steiner planning remain partial.
