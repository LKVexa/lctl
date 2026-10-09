
# Directives

## `@unit`
Defines unit-level identity and execution defaults. The current lowerer recognizes `id`, `version`, `profile`, `entry`, `target`, `backend`, `network`, `replay`, `precision`, `error_budget`, `resource_ceiling`, `admission`, `state`, and `proof`.

## `@defaults`
Defines recurring row semantics. Typical keys are `qspace`, `basis`, `regime`, `assume`, `error`, and `conf`.

## `@frame`
Begins a frame. `id` is required by convention and generated if absent. Optional frame fields include parent/module/qspace/tick/prev/registers/basis/state_regime/noise_regime/approx_regime/hardware/conf/error/admit/proof.

## `@end`
Ends the current frame.
