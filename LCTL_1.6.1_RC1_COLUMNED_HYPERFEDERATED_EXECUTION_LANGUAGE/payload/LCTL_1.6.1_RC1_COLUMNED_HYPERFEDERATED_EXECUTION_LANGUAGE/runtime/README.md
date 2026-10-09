# LCTL 1.2 Reference Runtime

Requires Java 21.

The runtime is a local classical reference authority. It parses/verifies LCTL source, simulates supported quantum operations, emits QCIR/QCIR-P, computes conservative parallel plans, emits causal DAGs, validates topology/protocol/failure metadata, and classically emulates declared distributed communication costs.

It is not a physical QPU runtime.

Run `runtime/bin/lctl.sh` (POSIX) or `runtime\bin\lctl.cmd` (Windows).

Use `physical-distributed-run` to test the physical boundary: the reference package intentionally returns `PHYSICAL_DISTRIBUTED_EXECUTION_BLOCKED`.
