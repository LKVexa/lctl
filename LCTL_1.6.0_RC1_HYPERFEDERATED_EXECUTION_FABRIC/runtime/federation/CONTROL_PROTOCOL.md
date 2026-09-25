# Federated Control Protocol 1.5

Reference messages: `HELLO`, `CAPABILITIES`, `JOIN`, `LEAVE`, `HEARTBEAT`, `ASSIGN`, `MIGRATE`, `START`, `PAUSE`, `RESUME`, `CHECKPOINT`, `RESULT`, `FAIL`, `ROUTE_UPDATE`, `TOPOLOGY_UPDATE`, `CALIBRATION_UPDATE`, `CANCEL`, `SHUTDOWN`.

Every production transport binding must add sequence numbers, message hashes, provenance, trust-domain checks and replay-safe transition handling. The packaged runtime specifies this contract but does not claim a live remote control-plane deployment.
