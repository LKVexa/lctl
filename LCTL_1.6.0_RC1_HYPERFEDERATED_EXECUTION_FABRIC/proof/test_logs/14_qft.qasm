OPENQASM 3.0;
include "stdgates.inc";
qubit[3] q;
bit[0] c;
h q[0];
// LCTL target adapter: CPHASE requires lowering or target-specific handling; proof=op:cphase
// LCTL target adapter: CPHASE requires lowering or target-specific handling; proof=op:cphase
h q[1];
// LCTL target adapter: CPHASE requires lowering or target-specific handling; proof=op:cphase
h q[2];
swap q[0], q[2];
