# Lowering and Honesty

MSSL-S has two semantic planes.

## Executable plane

`FACE=CODE` lowers to the 24-op BWX1 profile and can be executed by the supplied
BASIC-BWVM.

## Learned/model plane

`KNOW`, `LEARN`, `MODEL`, `ASSERT`, and `EVIDENCE` are verified and queryable local
semantics. They do not pretend to be native BWX1 matrix/optimization/AI opcodes.

The compiler lowers CODE and preserves the rest as source/proof metadata.

This split is deliberate: adding mathematical vocabulary must not manufacture
machine capabilities that were never implemented.
