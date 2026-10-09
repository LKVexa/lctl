# FACE Inference and Precedence


OP normally determines FACE. The compiler checks families in a fixed order: DECL, CODE, PARALLEL, COUPLING, FIELD, PROTOCOL, FAILURE, MEMORY, TRANSFORM, ASSERT, TOPOLOGY, DISTRIBUTED.

That order matters when a name belongs to more than one catalog. For example, `NODE` appears in both topology and distributed vocabularies; current compiler precedence resolves it to TOPOLOGY. When an operation is ambiguous or outside the known catalog, supply `f=FACE` in META.

Never guess a FACE for an unknown operation in generated source. Make the override explicit.
