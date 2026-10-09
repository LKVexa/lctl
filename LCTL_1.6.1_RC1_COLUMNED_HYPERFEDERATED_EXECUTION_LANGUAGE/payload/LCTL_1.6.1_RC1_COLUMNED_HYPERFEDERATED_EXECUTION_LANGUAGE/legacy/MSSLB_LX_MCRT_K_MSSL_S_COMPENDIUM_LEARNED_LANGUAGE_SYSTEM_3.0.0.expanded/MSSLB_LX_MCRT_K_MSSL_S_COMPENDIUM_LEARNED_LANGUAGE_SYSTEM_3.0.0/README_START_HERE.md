# MSSLB-LX / MCRT-K / MSSL-S 3.0.0
## Compendium-Learned Backendless Language System

This is the learned successor to the 2.0.0 MSSLB-FX / MCRT-F / MSSL-T system.

The architecture is now:

`MSSLB-LX bundle -> MCRT-K knowledge/causal frame -> MSSL-S semantic tuple -> BWX1`

The language incorporates a derived semantic memory from **20 attached compendium
chapters and 1205 normalized entries**.

## What changed

The language is no longer limited to “an opcode with provenance.” It now treats
**mathematical regime, type, assumptions, uncertainty/error and confidence as part
of the source semantics**.

An MSSL-S row has 18 columns:

`ROW¦FACE¦LANE¦OP¦OUT¦A¦B¦CAP¦WIDTH¦MODE¦AUX¦TYPE¦CLASS¦REGIME¦ASSUME¦ERROR¦CONF¦PROOF`

A learned MCRT-K frame has 20 columns:

`ID¦PARENT¦SOURCE¦FIELD¦MODULE¦SEQ¦DEPTH¦TICK¦SYMBOL¦EXEC¦ROUTE¦LEARN¦CLASS¦REGIME¦ASSUME¦ERROR¦CONF¦ADMIT¦STATUS¦PROOF`

A top-level MSSLB-LX bundle has 16 columns:

`BUNDLE¦ID¦VERSION¦PROFILE¦TARGET¦ENTRY¦FRAMES¦KNOWLEDGE¦BACKEND¦NETWORK¦POLICY¦REPLAY¦ADMISSION¦ERROR¦STATE¦PROOF`

## Learned but not self-deceiving

The language can carry:

- exact and exact-linear statements;
- linearized models;
- structural analogues;
- nonlinear/combinatorial models;
- explicit no-faithful-form/rejection outcomes;
- uncertainty and confidence;
- assumptions and regularity;
- numerical error envelopes;
- learned relations;
- Q1-Q6 proof/admission evidence.

A `LEARN` row becomes admissible only inside a frame with `ADMIT=admit` and complete
Q1-Q6 evidence rows.

## No backend remains normative

`BACKEND=none`, `NETWORK=deny`, `POLICY=local-only`.

The reference processor and compendium memory run locally in-process. The core
machine target remains the supplied BWX1 full-width VM.

## Implemented package contents

- normative 3.0 specifications;
- 1205-entry local semantic memory;
- 20 field lesson profiles;
- Q1-Q6 learning/admission kernel;
- epistemic type system and semantic regimes;
- local Java reference runtime;
- local compendium knowledge query;
- BWX1 lowering;
- positive and negative conformance tests;
- executable multi-frame example;
- proof output against the supplied BASIC-BWVM.
