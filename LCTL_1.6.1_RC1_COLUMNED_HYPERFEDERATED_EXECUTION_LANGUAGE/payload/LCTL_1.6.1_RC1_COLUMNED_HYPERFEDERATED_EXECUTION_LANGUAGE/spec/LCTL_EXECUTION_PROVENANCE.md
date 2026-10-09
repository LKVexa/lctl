# LCTL Distributed Execution Provenance

Every distributed artifact should bind:

`language`, `profile`, `execution_class`, `parallel_state`, `distributed_state`, `quantum_boundary`, `target`, `source_hash`, `topology_hash`, `schedule_hash`, `qcir_p_hash`, `physical_qpu`, `physical_parallel`, and `physical_distributed`.

The reference compiler hashes source, topology, schedule, and QCIR-P. Physical claims require authenticated adapter evidence unavailable in this package, so `physical-distributed-run` intentionally fails.
