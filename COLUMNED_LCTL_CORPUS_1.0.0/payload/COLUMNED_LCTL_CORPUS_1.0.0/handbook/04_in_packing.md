# IN Packing: A and B


`IN` maps to canonical operand `A`. When a second operand is required, write `A›B`. At most two operands may be packed this way. `_` means no explicit operand.

Examples:

- `q[0]` → `A=q[0]`, `B=_`
- `left›right` → `A=left`, `B=right`
- `_` → both blank

Do not use commas for A/B packing; commas are intentionally left available to mathematical expressions and parameter payloads.
