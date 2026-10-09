# Classical Router Specification

The reference classical router performs deterministic shortest-path routing over topology links typed `classical`, minimizing declared latency. It emits path, latency cost, fidelity/success fields where meaningful, and a route hash.

If no route exists, planning fails closed for a dependency that requires inter-node communication.

Future work: bandwidth-aware and k-shortest routing, congestion modeling, and failure-domain-aware alternate-route optimization.
