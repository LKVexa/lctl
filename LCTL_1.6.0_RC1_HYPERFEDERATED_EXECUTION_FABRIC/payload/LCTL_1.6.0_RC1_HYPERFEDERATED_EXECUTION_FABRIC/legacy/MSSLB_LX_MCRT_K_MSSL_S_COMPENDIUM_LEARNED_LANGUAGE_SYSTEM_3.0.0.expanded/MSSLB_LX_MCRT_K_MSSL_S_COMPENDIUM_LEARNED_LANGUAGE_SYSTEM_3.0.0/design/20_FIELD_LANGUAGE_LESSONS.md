# Twenty Field Lessons -> Language Features

## Field 1: Pure Mathematics

**Lesson.** Preserve exact structure where it exists, but encode non-reducibility and analogy as legitimate semantic outcomes.

**Language features.** proof objects, structural types, rejection/no-faithful-form regime, projection/rank invariants.

## Field 13: Probability Theory

**Lesson.** Make uncertainty typed: distributions, normalization, expectation, covariance, concentration and Markov evolution must carry probabilistic constraints.

**Language features.** distribution/simplex types, confidence, stochastic regime, belief transitions.

## Field 15: Computational Mathematics

**Lesson.** Numerical meaning requires error, conditioning and exactness metadata; algorithms should expose structured factorization rather than hide it.

**Language features.** error envelopes, conditioning contracts, exact/validated mode, factorization/operator tags.

## Field 17: Optimization

**Lesson.** Optimization should be expressed through objective, constraints, regularity, duality and admission conditions rather than opaque search.

**Language features.** objective/constraint rows, KKT proof gate, convexity assumptions, argmin/argmax model ops.

## Field 19: Decision Theory

**Lesson.** Decisions require utility, risk, beliefs and uncertainty to remain visible; descriptive nonlinearity should not be silently linearized.

**Language features.** utility/risk types, policy state, belief/confidence, nonlinear/no-faithful-form regimes.

## Field 20: Game Theory

**Lesson.** Multi-agent semantics require payoff structure, strategic state and equilibrium status, while computational hardness remains an explicit boundary.

**Language features.** agent/game model types, payoff relations, equilibrium tags, complexity/impossibility status.

## Field 21: Classical Mechanics

**Lesson.** Dynamics should carry state, conserved quantities, energy and evolution operators, with exact and linearized regimes separated.

**Language features.** state/evolution types, invariant/conservation assertions, energy metadata, linearized dynamics regime.

## Field 22: Continuum Mechanics

**Lesson.** Tensor state and constitutive laws need symmetry, positivity and deformation-regime contracts.

**Language features.** tensor/matrix types, symmetry/PSD assumptions, balance-law assertions, small/finite deformation regimes.

## Field 23: Coupled Mechanics

**Lesson.** Coupling belongs in first-class block relations; reciprocity, off-diagonal strength and thermodynamic admissibility should be inspectable.

**Language features.** coupling graph, block operator type, reciprocity assertions, coupling-strength metadata.

## Field 27: Statistical Mechanics

**Lesson.** Statistical state should expose ensembles, spectra, covariance, fluctuation and equilibrium/non-equilibrium regime.

**Language features.** ensemble/distribution types, spectral state, covariance/fluctuation metadata, equilibrium tags.

## Field 32: Quantum Mechanics

**Lesson.** Vector/operator/tensor-product semantics can be exact and should not be degraded into generic scalar plumbing.

**Language features.** state-vector type, Hermitian/unitary properties, projection/measurement relations, tensor-product type.

## Field 57: Computational Neuroscience

**Lesson.** Learned systems are often linear operators wrapped by nonlinear gates; adaptation should be represented as explicit update rules.

**Language features.** recurrent-state type, gating regime, outer-product updates, decoding/regression relations.

## Field 58: Computer Science

**Lesson.** A language must represent algorithmic and combinatorial limits honestly; undecidability and complexity are semantic properties, not compiler failures.

**Language features.** complexity contracts, combinatorial regime, impossibility/rejection status, graph-semiring models.

## Field 59: Data Science

**Lesson.** Data semantics need metrics, embeddings, spectra, clustering objectives and explicit nonlinear boundaries.

**Language features.** metric/distance types, embedding metadata, spectral reduction, evaluation/error contracts.

## Field 60: Machine Learning

**Lesson.** Learning should expose objectives, regularization, Jacobians, backpropagation and model-update provenance.

**Language features.** loss/objective type, regularization metadata, Jacobian/gradient relations, learned-update rows.

## Field 61: Artificial Intelligence

**Lesson.** Reasoning mixes combinatorial search, probabilistic inference, contraction/value updates and matrix-based learned models; each regime should remain distinguishable.

**Language features.** search/model regime, belief inference, Bellman/value relations, reasoning proof status.

## Field 62: Robotics

**Lesson.** Transform, Jacobian, estimation, planning and control semantics should be typed with local capability and uncertainty.

**Language features.** SE(3)/transform type, Jacobian capability metrics, belief/estimate types, control constraints.

## Field 77: Network Science

**Lesson.** Graphs are native algebraic objects: adjacency, Laplacian, spectra, paths and communities should not be encoded as untyped arrays.

**Language features.** graph type, adjacency/laplacian operators, spectral graph metadata, locality relations.

## Field 79: Information Theory

**Lesson.** Information is measurable state: entropy, divergence, channel and code structure should be first-class with exactness and capacity bounds.

**Language features.** information/entropy types, KL/divergence relations, channel/coding types, capacity/error bounds.

## Field 80: Parallel and Distributed Computing

**Lesson.** Parallel execution needs work, communication, memory, scheduling and impossibility contracts; backendless execution may model distribution without opening a network.

**Language features.** lane/schedule semantics, cost/communication models, barrier/scan models, consensus-impossibility status.
