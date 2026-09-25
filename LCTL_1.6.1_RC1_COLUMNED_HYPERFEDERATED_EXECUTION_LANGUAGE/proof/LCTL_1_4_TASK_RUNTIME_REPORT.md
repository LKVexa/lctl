# LCTL 1.4 Task Runtime Report

The Java 21 mesh runtime executes the causal graph on real local worker threads. It supports task, dataflow-style dependency execution, BSP level/barrier execution, and asynchronous `CompletableFuture` execution.

Reference run with four workers:

- tasks: **5**
- maximum observed concurrency: **3**
- semantic result hash: `d6e579bc3e66790cf14b1982a705beab43ccbaf085cd755873cff91412cfc929`
- event reconstruction: **PASS**

This is an executable reference subset. Explicit bounded channels/backpressure, a stateful actor model, custom work stealing, and live dynamic rescheduling remain incomplete.
