# LCTL Adapter ABI 3.0

The reference ABI models dynamic capabilities, live topology/calibration updates, reservation, submit, status, cancel, failure notification, measurement delivery, receipt retrieval, route/link capability and authentication evidence.

Default runtime authority remains `BACKEND=none` and `NETWORK=deny`. A physical target claim without authenticated endpoint authority and receipts is blocked with `BLOCKED_EXTERNAL_AUTHORITY`.
