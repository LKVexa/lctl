# LCTL 1.4 Scheduler Report

The reference `schedule2` implementation uses critical-path priority, causal dependencies, logical placement, and classical communication cost. The reference Bell-pair schedule has modeled makespan **3.0**.

Calibrated crosstalk, full coherence/deadline/energy objectives, exact bounded branch-and-bound validation, and live schedule recomputation are not yet complete. The scheduler therefore does not claim physical simultaneity or global optimality.
