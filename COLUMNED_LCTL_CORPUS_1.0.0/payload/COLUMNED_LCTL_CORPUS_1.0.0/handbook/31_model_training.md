# Using the Machine Corpus


`machine/row_pairs.jsonl` is the highest-density supervised source: every record contains the compact row, the actual lowered canonical row, the alias expansion, and compiler-inferred fields. `program_pairs.jsonl` teaches whole-program conversion. `instruction_corpus.jsonl` turns those verified pairs into instruction tasks. `negative_cases.jsonl` teaches fail-closed behavior.

For model training, keep training/validation splits by **program**, not by row, to reduce leakage between rows from the same example.
