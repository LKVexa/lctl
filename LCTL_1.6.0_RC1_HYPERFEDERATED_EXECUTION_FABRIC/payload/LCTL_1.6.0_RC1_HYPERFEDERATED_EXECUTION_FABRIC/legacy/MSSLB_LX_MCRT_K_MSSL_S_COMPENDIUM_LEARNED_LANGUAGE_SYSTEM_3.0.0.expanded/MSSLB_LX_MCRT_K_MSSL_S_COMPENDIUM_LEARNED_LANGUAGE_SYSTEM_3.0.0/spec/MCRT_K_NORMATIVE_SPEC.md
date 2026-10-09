# MCRT-K 1.0

MCRT-K means **Knowledge/Causal MCRT Frame**.

Columns:

`ID¦PARENT¦SOURCE¦FIELD¦MODULE¦SEQ¦DEPTH¦TICK¦SYMBOL¦EXEC¦ROUTE¦LEARN¦CLASS¦REGIME¦ASSUME¦ERROR¦CONF¦ADMIT¦STATUS¦PROOF`

New learned fields beyond the prior causal frame include:

- FIELD: compendium/domain field identifier.
- CLASS: mathematical/semantic class.
- REGIME: exact/linearized/stochastic/etc.
- ASSUME: regularity and structural assumptions.
- ERROR: frame-level error contract.
- CONF: confidence `[0,1]`.
- ADMIT: `admit`, `reject`, or `observe`.

A frame with one or more MSSL-S `LEARN` rows MUST use `ADMIT=admit` and contain six
MSSL-S `EVIDENCE` rows whose OP values are Q1, Q2, Q3, Q4, Q5 and Q6 and whose A
value is `pass`.

PARENT is causal provenance, not implicit executable control flow.

Every frame has an independent SHA-256 seal.
