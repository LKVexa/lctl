# LCTL 1.5 Architecture Report

## Result
The 1.5 RC1 candidate adds `LctlFederatedMain` as a Java 21 layer over the preserved 1.4 `LctlMeshMain` and 1.3 `LctlFabricMain`. The authority chain remains additive: 1.5 delegates unknown commands to 1.4, which delegates inherited commands to 1.3.

## New architecture
Program regions can now be described through federation, execution-domain, worker-group, route, placement, resource-pool, failure-domain, trust-domain and consistency-domain concepts. The reference runtime is offline by default and treats logical QPU domains as non-physical.

## Safety
Unknown quantum state is never represented as cloneable CRDT or actor state. Checkpoints are classical metadata/numerical-state artifacts only. Physical execution remains evidence-gated.
