# Failure and Recovery Specification

RC1 adds deterministic failure injection for:
worker crash, node unavailable, memory exhaustion, classical link timeout/loss, quantum-link failure, ebit generation failure/expiration, stale calibration, QPU unavailable, route invalidation, and coherence deadline exceeded.

Recovery classifications:
- RETRY_SAFE
- RESTART_FROM_CLASSICAL_BOUNDARY
- REROUTE_SAFE
- REGENERATE_ENTANGLEMENT
- ROLLBACK_CLASSICAL_ONLY
- RECOVERY_TARGET_SPECIFIC
- RECOVERY_IMPOSSIBLE

Unknown physical quantum state is never restored from a fabricated classical checkpoint.
