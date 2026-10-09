# Adapter Feature Matrix

| Feature | Local reference | OpenQASM text | Generic hardware scaffold |
|---|---|---|---|
| Core 1Q gates | SUPPORTED | SUPPORTED | TARGET_SPECIFIC |
| CX/CZ/SWAP | SUPPORTED | SUPPORTED | TARGET_SPECIFIC |
| Measurement Z | SUPPORTED | SUPPORTED | TARGET_SPECIFIC |
| X/Y measurement | SUPPORTED | APPROXIMATE via basis lowering (not emitted automatically) | TARGET_SPECIFIC |
| Density/noise channels | SUPPORTED | UNSUPPORTED in core export | TARGET_SPECIFIC |
| Dynamic CLASSICAL_IF | SUPPORTED | TARGET_SPECIFIC comment | TARGET_SPECIFIC |
| Transform/model rows | semantic only | preserved outside target IR | TARGET_SPECIFIC |
