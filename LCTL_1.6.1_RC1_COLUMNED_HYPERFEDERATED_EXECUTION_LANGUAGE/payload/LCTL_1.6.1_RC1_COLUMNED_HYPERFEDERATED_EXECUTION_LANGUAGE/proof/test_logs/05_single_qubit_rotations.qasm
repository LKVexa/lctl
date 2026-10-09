OPENQASM 3.0;
include "stdgates.inc";
qubit[1] q;
bit[1] c;
rx(0.4) q[0];
ry(0.7) q[0];
rz(-0.2) q[0];
c[0] = measure q[0];
