# LCTL 1.5 Gap Ledger

This ledger applies the exact 1.4→1.5 gap list to the resulting RC1 development candidate. Statuses are deliberately conservative.

| # | Gap | Status | Evidence / boundary |
|---:|---|---|---|
| 1 | Full Clifford/channel commutation authority | **IMPLEMENTED_PARTIAL** | Exact Pauli/diagonal/bounded-matrix rules preserved; full Clifford/channel authority remains incomplete. |
| 2 | Multilevel coarsening and canonical KL/FM hypergraph partitioning | **IMPLEMENTED_PARTIAL** | Deterministic multistart + local FM-like refinement executes; canonical full multilevel implementation remains partial. |
| 3 | Full Pareto vector latency/ebit/fidelity/energy/failure | **IMPLEMENTED_PARTIAL** | All objective dimensions are exposed; several values are reference proxies rather than measured physical quantities. |
| 4 | Portable NUMA/device/QPU hierarchical placement | **IMPLEMENTED_PARTIAL** | Hierarchical logical placement executes; portable live NUMA/device calibration binding remains partial. |
| 5 | Ebit inventory min-cost-flow/time-expanded/purification routing | **IMPLEMENTED_PARTIAL** | Logical ebit lifecycle/reservation model and alternate routing are present; full time-expanded min-cost flow remains partial. |
| 6 | Calibrated crosstalk/deadline/energy-aware scheduler | **IMPLEMENTED_PARTIAL** | Reference multi-policy scheduler executes; authenticated physical calibration/crosstalk authority is unavailable. |
| 7 | Dynamic scheduling/work stealing/live reoptimization/stragglers | **IMPLEMENTED_PARTIAL** | Adaptive runtime and failure response execute; full live work-steal/straggler engine is not release-qualified. |
| 8 | Explicit dataflow channels/backpressure and actor capabilities | **OPERATIONAL** | Bounded channels, backpressure, actor mailboxes/capabilities model, and pipeline runtime execute locally. |
| 9 | Executable CRDT and distributed merge runtime | **OPERATIONAL** | G-Counter, PN-Counter, G-Set, OR-Set and LWW reference semantics are implemented for classical state. |
| 10 | Stabilizer reset/measurement breadth | **IMPLEMENTED_PARTIAL** | Existing tableau execution and batch parallelism preserved; reset/measurement breadth remains partial. |
| 11 | Tensor-network numerical contraction engine | **IMPLEMENTED_PARTIAL** | Bounded executable full-state contraction oracle added; production sparse contraction-tree backend remains partial. |
| 12 | Arbitrary-state numerical remote-CNOT equivalence | **QUALIFIED** | Deterministic arbitrary normalized two-qubit states pass all four measurement branches against ideal CNOT at fidelity 1.0 in bounded reference emulation. |
| 13 | Transactional live recovery across modeled failures | **IMPLEMENTED_PARTIAL** | Nine recovery/failure cases execute; full transactionality across every resource class remains partial. |
| 14 | Event-sourced reconstruction of shards/resources | **IMPLEMENTED_PARTIAL** | Hash-chained task replay is verified; complete numerical shard, route and ebit inventory reconstruction remains partial. |
| 15 | Covariance/calibration-aware error algebra | **IMPLEMENTED_PARTIAL** | Probability/variance/tolerance handling preserved; covariance/calibration propagation remains partial. |
| 16 | Streaming/dynamic Adapter ABI 3.0 | **IMPLEMENTED_PARTIAL** | ABI 3 mock validation covers dynamic capability/topology/calibration/reservation/receipt fields; live vendor streams are external. |
| 17 | Long-duration soak qualification | **IMPLEMENTED_PARTIAL** | Short development soak passed; mandatory 8-hour and recommended 24-hour soaks were not run. |
| 18 | Authenticated physical multi-QPU authority | **BLOCKED_EXTERNAL_AUTHORITY** | No authenticated physical endpoint/calibration/execution/measurement receipt chain was supplied. |
