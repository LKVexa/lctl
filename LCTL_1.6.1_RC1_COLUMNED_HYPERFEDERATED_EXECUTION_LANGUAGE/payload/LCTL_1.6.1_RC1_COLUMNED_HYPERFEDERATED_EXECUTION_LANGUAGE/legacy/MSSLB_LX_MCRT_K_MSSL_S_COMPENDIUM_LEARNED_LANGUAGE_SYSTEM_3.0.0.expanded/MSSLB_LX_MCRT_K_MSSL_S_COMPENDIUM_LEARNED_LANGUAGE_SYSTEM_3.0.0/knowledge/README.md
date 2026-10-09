# Compendium Semantic Memory

This directory contains a **derived local semantic memory** built from the 20 supplied
LKVexa Compendium chapters.

- Fields represented: **20**
- Parsed semantic entries: **1205**
- Exact-linear family: **1062**
- Linearized family: **50**
- Structural-analogue family: **55**
- No-faithful-form family: **38**

`compendium_semantic_memory.jsonl` stores one normalized semantic record per entry:
field, title, objects, structure, reduction, status/regime, semantic classes and the
presence of the Q1-Q6 proof cycle.

The runtime ships a read-only local query command. This memory is **not a hidden
backend** and is never fetched from a network.
