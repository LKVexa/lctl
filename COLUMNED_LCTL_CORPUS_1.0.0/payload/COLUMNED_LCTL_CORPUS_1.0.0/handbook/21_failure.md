# Failure and Recovery Semantics


Failure operators identify node/process/link failure, timeout, packet loss, quantum-link failure, calibration expiry, unavailable QPU, coherence limit, resource exhaustion, target drift, scheduler deadlock, ownership ambiguity, and checkpoints.

Use failure rows to make failure states explicit. Recovery claims require runtime evidence showing replay/restart/reroute/checkpoint restoration, not merely a logged failure label.
