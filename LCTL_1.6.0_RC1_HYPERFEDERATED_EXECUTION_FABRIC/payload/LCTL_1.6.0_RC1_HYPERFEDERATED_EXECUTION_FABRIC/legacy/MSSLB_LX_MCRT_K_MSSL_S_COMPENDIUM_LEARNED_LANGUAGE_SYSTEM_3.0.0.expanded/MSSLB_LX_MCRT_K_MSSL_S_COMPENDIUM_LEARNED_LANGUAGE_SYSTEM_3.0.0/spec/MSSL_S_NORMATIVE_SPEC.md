# MSSL-S 1.0
## MSSL Semantic Tuple Language

Columns:

`ROW¦FACE¦LANE¦OP¦OUT¦A¦B¦CAP¦WIDTH¦MODE¦AUX¦TYPE¦CLASS¦REGIME¦ASSUME¦ERROR¦CONF¦PROOF`

## FACE

- `CODE`: executable BWEC/BWX1 primitive.
- `KNOW`: admitted read-only semantic relation.
- `LEARN`: candidate learned relation; requires Q1-Q6 frame admission.
- `MODEL`: mathematical/model statement not directly lowered.
- `ASSERT`: invariant, constraint or contract.
- `EVIDENCE`: proof/admission evidence.

## CODE

CODE retains the 24 base operations:

`NOP MOVI MOV ADD SUB MUL DIVU MODU AND OR XOR NOT SHL SHR CMP JMP JZ JNZ LOAD STORE PUSH POP SVC HALT`

CODE rows lower to BWX1 exactly as in 2.0.0.

A CODE row MUST use REGIME `exact` or `exact_linear`. Approximation belongs at the
MODEL/LEARN layer and must be explicit.

## Semantic relation form

For KNOW/LEARN rows, the canonical relation idiom is:

`OP=REL, OUT=<subject>, A=<predicate>, B=<object>`

This creates a local knowledge triple while retaining TYPE/CLASS/REGIME/ASSUME/
ERROR/CONF/PROOF.

## MODEL examples

`OP=OBJECTIVE`, `CONSTRAINT`, `DYNAMICS`, `COUPLE`, `BELIEF`, `ENTROPY`, `SPECTRAL`,
`TRANSFORM`, `INVARIANT`, `POLICY`, `VALUE`.

These are semantic model operators, not claims that the 24-op BWX1 machine natively
implements matrix algebra, optimization or AI.

## EVIDENCE

Q1-Q6 are valid EVIDENCE operations.

- Q1 Classification / differential signature
- Q2 Image / semantic transform
- Q3 Regularity / assumptions
- Q4 Derivation
- Q5 Retraction / replay / lowering
- Q6 Admission

The proof cycle makes learned semantics auditable.
