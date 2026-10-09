# LCTL Type System

Families: amplitude, probability, phase, angle, complex, real, time, frequency, energy; qubit, qudit[d], qreg[n], ket[d], bra[d], pure_state[d], mixed_state[d], density[d], subsystem and multipartite-state types; operator[d], linear_operator, Hermitian, unitary, projector, POVM, Hamiltonian, KrausSet, channel, CPTP, isometry, controlled_operator and oracle; structural tensor/graph/basis/spectrum/PauliString/stabilizer/syndrome types; classical bit/integer/real/complex/vector/matrix/distribution/measurement_result/histogram/confidence_interval/resource_report/proof_record.

The reference verifier statically distinguishes declared quantum and classical locations and rejects measurement into a non-classical destination. Dimensions are attached to qreg and matrix assertions. Approximation regime is semantic type metadata rather than commentary.

## Parallel / field / coupling additions (1.1)

Additional semantic types include `lane`, `tick`, `dependency`, `happens_before`, `parallel_region`, `work`, `span`, `critical_path`, `communication_edge`, `collective`, `memory_tier`, `topology`, `failure_model`, `checkpoint_policy`, `field[T,domain]`, `tensor[rank,shape]`, `symmetric_tensor`, `coupling_block`, `coupled_system`, and `interface`.

These do not weaken quantum linear ownership. A `qstate`/`qubit` remains non-copyable across lanes or nodes.
