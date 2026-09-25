# LCTL 1.3 Scalability Characterization

This benchmark characterizes the reference multi-process statevector fabric on a very small four-qubit Bell-pair workload. Process launch and synchronization dominate this workload, so the result is not intended to demonstrate speedup.

| Workers | Serial s | Parallel s | Speedup | Efficiency | Equivalent |
|---:|---:|---:|---:|---:|---|
| 1 | 0.222521 | 0.206755 | 1.076 | 1.076 | True |
| 2 | 0.219316 | 0.229745 | 0.955 | 0.477 | True |
| 4 | 0.211247 | 0.274136 | 0.771 | 0.193 | True |
| 8 | 0.202604 | 0.536212 | 0.378 | 0.047 | True |

All hashes/equivalence checks passed. These numbers describe a classical simulator and must not be interpreted as QPU speedup.
