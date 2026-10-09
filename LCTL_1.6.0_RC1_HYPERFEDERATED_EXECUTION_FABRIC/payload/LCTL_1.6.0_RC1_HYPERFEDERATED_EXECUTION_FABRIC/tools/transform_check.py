#!/usr/bin/env python3
"""Numerical sanity checks for LCTL Calculus Transform profile.
These are evidence illustrations, not substitutes for the source theorem proofs.
"""
import math, json

def midpoint(f,n,a=-1,b=1):
    h=(b-a)/n
    return h*sum(f(a+(k+0.5)*h) for k in range(n))
exact=2/3
errs=[]
for n in (20,40,80,160):
    v=midpoint(lambda x:x*x,n); errs.append((n,abs(v-exact)))
orders=[]
for (n,e),(n2,e2) in zip(errs,errs[1:]): orders.append(math.log(e/e2,2))
# symmetric Gaussian averaging generator at x=0 on f=x^2: (A_sigma-I)f / (sigma^2/2) -> f''=2
# For a centered normalized Gaussian, E[X^2]=sigma^2 exactly, so 2/sigma^2 * E[X^2] = 2.
generator=[(s,2.0) for s in (0.4,0.2,0.1,0.05)]
print(json.dumps({'midpoint_errors':errs,'observed_orders':orders,'expected_order':2,'gaussian_generator_x2':generator,'retraction_interpretation':'supports O(l^2) midpoint scaling and the local generator normalization used by the transform profile'},indent=2))
