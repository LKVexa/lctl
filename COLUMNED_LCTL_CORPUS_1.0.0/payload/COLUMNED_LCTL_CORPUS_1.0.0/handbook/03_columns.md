# The Eight Source Columns

## `ID`

Stable row identity; it is required and is never inferred.

## `LANE`

Semantic or scheduling lane; underscore means no explicit lane.

## `OP`

Operation or compact alias; normally determines canonical FACE.

## `OUT`

Output or destination.

## `CTRL`

Explicit control operand or classical condition source.

## `IN`

Primary canonical A operand, optionally packed with B as A›B.

## `ARG`

Canonical PARAM payload.

## `META`

Semantic overrides not safely inferred or inherited.
