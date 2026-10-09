# LCTL Quantum Networking Model

Reference protocol metadata includes:

`ENTANGLE_LINK`, `EPR_RESERVE`, `EPR_RELEASE`, `TELEPORT`, `REMOTE_CONTROL`, `REMOTE_CNOT`, `REMOTE_MEASURE`, `CLASSICAL_FEEDBACK`, `ENTANGLEMENT_SWAP`, `PURIFY`, `HERALD`, `SYNC_QCLOCK`, `QCHANNEL_SEND`, `QCHANNEL_RECEIVE`.

Teleportation requires an entanglement resource and a classical path. Remote gates require an explicit route and a supporting target profile. Entanglement swapping requires at least two ebit resources in the reference checks.

These are target-neutral semantic/protocol records. They become physical operations only after qualified target binding.
