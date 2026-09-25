# LCTL Parallel Cost, Locality, Reliability, and Energy Model

The reference profile uses a layered accounting model rather than a single scalar "parallel cost":

`T_total = T_compute + T_comm + T_sync + T_route + T_recovery` when phases are additive.

When communication is proven to overlap computation, the relevant interval MAY use `max(T_compute, T_comm)` rather than their sum. Such overlap must be declared/proved by the target or scheduler model.

A communication edge MAY use `T_msg = alpha + beta*m`. More detailed target models may replace it.

Parallel planning reports work/span independently of these estimates. `W/D` is an exploitable-parallelism upper descriptor, not a guaranteed speedup.

Reliability annotations may include failure rate, MTBF, checkpoint cost, recovery cost, and redundancy. Energy annotations may include operation, communication, memory, and target-specific energy estimates. Estimates SHALL identify their provenance and uncertainty.
