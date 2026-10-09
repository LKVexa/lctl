# LCTL Communication Model

Classical communication uses an affine reference cost model where applicable:

`T_message = alpha + beta * n`

with latency `alpha`, per-unit transfer cost `beta`, and payload size `n`.

Quantum links have a separate model containing setup time, heralding delay, success probability, fidelity, ebit consumption, and retry/expected-completion behavior.

The distributed emulator models these values classically and labels itself `DISTRIBUTED_CLASSICAL_EMULATION`. It does not transmit physical qubits.
