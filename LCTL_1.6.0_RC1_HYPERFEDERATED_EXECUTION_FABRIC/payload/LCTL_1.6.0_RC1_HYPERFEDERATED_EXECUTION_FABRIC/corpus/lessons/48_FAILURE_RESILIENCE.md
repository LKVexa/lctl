# Lesson 48 — Failure & Resilience

## Objective
Use typed failure semantics and quantum-honest recovery strategies.

## Normative rule
LCTL must preserve quantum semantics, explicit communication, target-neutral source meaning, and evidence-gated physical claims.

## Valid example
See the corresponding distributed examples under `/examples/23_*` through `/examples/52_*`.

## Invalid example
See `/conformance/invalid_distributed/`.

## Verification exercise
Run `runtime/bin/lctl.sh verify`, `causal-dag`, `distributed-plan`, and `qcir-p` on a relevant example and inspect the provenance fields.

## Evidence boundary
Reference-host results are symbolic compilation or classical distributed emulation unless authenticated physical target evidence is present.
