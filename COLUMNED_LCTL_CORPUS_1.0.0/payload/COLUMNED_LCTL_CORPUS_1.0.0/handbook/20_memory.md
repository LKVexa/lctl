# Memory and Data Movement


The MEMORY family contains register/local/cache/NUMA/device/host/remote/persistent/capacity/move concepts. The default assumption for MEMORY rows is `classical_storage_only`.

This distinction is deliberate: classically representable simulator shards and metadata can be stored, checkpointed, replicated, or moved according to policy; arbitrary unknown physical quantum state cannot be serialized as ordinary bytes.
