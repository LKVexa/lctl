# Parallel Regions and Lanes


Parallel structure is expressed with lanes plus PARALLEL-family metadata such as `REGION`, `LANE_BIND`, `DEPENDENCY`, `COMM_MODEL`, `COLLECTIVE`, `DOMAIN_DECOMP`, and `OVERLAP`.

The verified parallel Bell example places independent gates in `lane0` and `lane1` and declares a two-lane region. Lanes expose concurrency; they do not by themselves prove that multiple workers executed concurrently. Runtime evidence must establish actual parallel execution.
