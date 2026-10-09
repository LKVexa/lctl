# LCTL Parallel Primitive Catalog

| Construct | Semantic role | Reference runtime |
|---|---|---|
| `LANE` column | stable logical lane | parsed/preserved |
| frame `TICK` | causal epoch | parsed/preserved |
| `BARRIER` | full declared-scope scheduling barrier | verified, simulator no-op |
| `SYNC` | synchronization point | verified, simulator no-op |
| `FENCE` | ordering fence | verified, simulator no-op |
| `PARALLEL/REGION` | parallel-region metadata | verified/preserved |
| `PARALLEL/DEPENDENCY` | explicit dependency edge metadata | verified/preserved |
| `PARALLEL/COMM_MODEL` | communication model metadata | verified/preserved |
| `PARALLEL/COLLECTIVE` | collective communication metadata | verified/preserved |
| `parallel-plan` | deterministic work/span/layer analysis | executable reference tool |

Parallel metadata is non-state-mutating. Target-specific distributed quantum operations require an admitted target adapter.
