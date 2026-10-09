# Lesson 35 — Communication, locality, and overlap

**Objective:** incorporate the parallel-language semantic extension into LCTL.

**Normative rule:** Communication and memory locality are explicit resource terms; overlap uses max only when justified.

**Valid example:** an LCTL parallel profile declares lanes and the reference `parallel-plan` derives a deterministic schedule while reporting `physical_parallel=false`.

**Invalid example:** claiming two operations physically ran at the same instant because the compiler placed them in one logical layer.

**Verification exercise:** inspect the parallel example, its schedule evidence, and the target boundary fields.

**Evidence boundary:** local planning is compiler evidence, not physical-QPU timing evidence.
