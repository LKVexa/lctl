# Determinism and Replay


The same compact source and compiler generation produce deterministic inferred cells, preserve row order and IDs, and generate deterministic frame/bundle seals. Runtime determinism still depends on the selected execution mode.

Do not confuse deterministic lowering with deterministic distributed scheduling. The former is a compiler property; the latter requires scheduler/replay evidence.
