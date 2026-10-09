# Distributed Target Adapter ABI Specification

A physical adapter must separate discovery, topology, calibration, compilation/lowering, reservation, submission, status, cancellation, measurement retrieval, and receipt retrieval.

The included `adapter-check` tool only evaluates whether an adapter record contains the minimum evidence categories. It never marks an adapter qualified automatically.

Qualification requires external authenticated evidence. Mock adapters are explicitly nonphysical.
