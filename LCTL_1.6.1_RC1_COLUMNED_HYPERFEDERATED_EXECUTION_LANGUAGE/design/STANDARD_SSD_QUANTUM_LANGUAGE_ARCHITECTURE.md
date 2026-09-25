# Standard-SSD LCTL Architecture

An ordinary SSD can hold the complete LCTL development authority:

`source -> parser -> verifier -> QCIR -> simulator -> optimizer -> resource ledger -> target adapter`

Only the optional transition from a qualified target adapter into a QPU crosses the physical quantum boundary.

## Four operating modes
1. **AUTHORING / SYMBOLIC** — source, grammar, type/ownership, proof, static checks.
2. **CLASSICAL QUANTUM SIMULATION** — statevector/density/stabilizer/tensor representations executed classically.
3. **QUANTUM COMPILATION** — deterministic QCIR, rewrites, routing, scheduling, target lowering.
4. **PHYSICAL QUANTUM EXECUTION** — qualified QPU target, measurement, authenticated result evidence.

The architecture deliberately keeps language authority independent of a vendor cloud or hardware backend. The SSD is therefore a durable repository and execution substrate for the *classical authority surrounding the quantum program*, not a substitute QPU.
