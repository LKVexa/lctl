# LCTL 1.5 Actor & Dataflow Report

Executable local reference runtimes were added for actors, bounded channels and pipelines. Actor mailboxes process deterministic message chains; channels use bounded queues and measured producer wait as backpressure evidence; pipelines use bounded stage buffers. Quantum state may be referenced by ownership handle but is never replicated as actor/CRDT state.
