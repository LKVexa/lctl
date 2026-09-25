# Semantic Execution Supergraph (SES)

SES/1.0 is the hierarchical execution planning graph. The reference RC1 implementation emits a deterministic task graph from LCTL CODE rows and typed dependency edges.

Implemented dependency reasons:
- `QUANTUM_OWNERSHIP`
- `MEASUREMENT_DEPENDENCY`
- `BARRIER`

SES nodes preserve source row ID, lane, operation, quantum operands, regime, and proof reference. SES is the input to partitioning, placement, routing, and scheduling.

Current RC1 scope: deterministic conservative extraction from ordered LCTL rows. Full nested failure-domain, memory-domain, communication, and coupling subgraphs remain implementation-partial and are carried in the gap ledger.
