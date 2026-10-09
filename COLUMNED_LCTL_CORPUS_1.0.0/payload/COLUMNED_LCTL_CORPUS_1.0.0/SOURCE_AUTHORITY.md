
# Source Authority

This corpus was generated from the Columned LCTL implementation in `LCTL_1.6.1_RC1_COLUMNED_HYPERFEDERATED_EXECUTION_LANGUAGE`.

Authority order inside this corpus:

1. `authority/LctlColumnMain.java` — implemented lowering/parser inference behavior.
2. `authority/LCTL_C_1_0_SPEC.md` and grammar — normative compact-language description.
3. `examples/compact/` verified `.lctlc` programs.
4. `examples/lowered/` canonical outputs produced by the packaged compiler.
5. Handbook explanations and machine tasks derived from those authorities.

Corpus generation verified all 53 compact examples with `column-verify` before packaging.
