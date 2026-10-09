# A Reliable Writing Method


For a new program:

1. State the unit identity/profile/target truthfully.
2. Choose defaults that are truly stable across the unit.
3. Create the frame hierarchy.
4. Declare registers/resources.
5. Write CODE rows with explicit OUT/CTRL/IN/ARG.
6. Add parallel/distributed/topology/protocol rows only where semantically needed.
7. Add META overrides only when inference would be wrong or incomplete.
8. Run column verification.
9. Lower to canonical and inspect the 18-cell result.
10. Run semantic/runtime tests appropriate to the profile.
11. Keep blocked physical authority blocked unless evidence exists.
