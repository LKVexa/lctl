# Distributed Protocol Execution Specification

The RC1 protocol emulator implements executable resource state transitions for:
- EPR reservation accounting;
- entanglement-link events;
- teleportation consumption and ownership-transfer evidence;
- remote-gate reference events;
- entanglement swapping (two ebits consumed, one derived ebit produced);
- purification reference state transition;
- classical-feedback happens-before events.

Teleportation is represented as Bell operation → measurement → classical message → correction → ownership transfer in the evidence stream. RC1 does not claim a physical link or QPU.

Protocol-level numerical teleportation equivalence for arbitrary supplied source states remains a next-step enhancement; the legacy LCTL simulator still provides ordinary teleportation circuit evidence.
