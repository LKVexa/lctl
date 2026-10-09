# Migration: Canonical LCTL → LCTL-C

## Convert existing source

```sh
./START_LCTL_COLUMN.sh columnize examples/01_bell_state/program.lctl bell.lctlc
```

Windows:

```bat
START_LCTL_COLUMN.cmd columnize examples\01_bell_state\program.lctl bell.lctlc
```

## Verify compact source

```sh
./START_LCTL_COLUMN.sh column-verify bell.lctlc
```

## Compile compact source back to sealed canonical LCTL

```sh
./START_LCTL_COLUMN.sh column-compile bell.lctlc bell.lowered.lctl
```

## Measure compression

```sh
./START_LCTL_COLUMN.sh column-stats bell.lctlc
```

## Check semantic round-trip

```sh
./START_LCTL_COLUMN.sh column-roundtrip examples/01_bell_state/program.lctl
```

## Run compact source directly

The 1.6.1 runtime transparently lowers `.lctlc` inputs for the canonical core and 1.6 hyperfederated planning surface.

Examples:

```sh
./START_LCTL_COLUMN.sh verify bell.lctlc
./START_LCTL_COLUMN.sh simulate bell.lctlc 256 20260810
./START_LCTL_COLUMN.sh parallel-depth examples_column/24_parallel_bell_pair_generation/program.lctlc
./START_LCTL_COLUMN.sh grain-plan examples_column/24_parallel_bell_pair_generation/program.lctlc
```

## Compatibility rule

Canonical `.lctl` remains first-class. LCTL-C is additive. Migration does not require deleting or rewriting the canonical corpus.
