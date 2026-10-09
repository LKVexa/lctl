# Columned LCTL Style Guide


Prefer the following style:

- one stable row ID per semantic action;
- short but meaningful lanes (`alloc`, `circuit`, `measure`, `lane0`, `meta`);
- compact aliases for very common operations, canonical names for rare/ambiguous ones;
- `_` for absence rather than empty cells;
- ARG for operation payload, META for semantic overrides;
- quote META values containing semicolons/spaces/quotes/`=`;
- explicit QSPACE where a register lives outside the generic `H` default;
- explicit proof tags for externally sourced or higher-authority claims;
- explicit `f=FACE` whenever inference is not authoritative.

Optimize for semantic density, not cryptic brevity.
