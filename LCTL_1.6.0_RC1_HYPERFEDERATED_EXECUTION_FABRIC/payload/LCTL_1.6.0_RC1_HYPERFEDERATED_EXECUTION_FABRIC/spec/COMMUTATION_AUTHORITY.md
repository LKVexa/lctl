# LCTL 1.3 Commutation Authority

The 1.3 fabric introduces an executable commutation planner rather than treating all scheduling independence as metadata.

Operational exact rules in RC1:
1. disjoint tensor-factor operations commute for scheduling purposes;
2. same-qubit diagonal operations from {Z,S,SDG,T,TDG,RZ,PHASE} commute;
3. all shared-operand cases without an admitted proof are conservatively serialized.

Every decision emits a rule ID and proof hash.

Planned but not yet fully operational:
- binary symplectic Pauli/Clifford proof catalog;
- controlled-operation algebra beyond disjointness;
- channel/noise commutation catalog;
- exact matrix commutator fallback;
- learned-rule Q1-Q6 admission integration.

No unproved shared-operand rewrite is applied in RC1.
