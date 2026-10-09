# MSSLB-LX 1.0

Magic: `MSSLB-LX/1.0`

Columns:

`BUNDLE¦ID¦VERSION¦PROFILE¦TARGET¦ENTRY¦FRAMES¦KNOWLEDGE¦BACKEND¦NETWORK¦POLICY¦REPLAY¦ADMISSION¦ERROR¦STATE¦PROOF`

Required canonical values:

- PROFILE = `msslb.lx.bwec1`
- TARGET = `BWX1`
- BACKEND = `none`
- NETWORK = `deny`
- POLICY = `local-only`
- REPLAY = `deterministic`
- ADMISSION = `q1-q6`
- ERROR = `fail-closed`
- ENTRY = `<frame-id>:<row-id>`

`KNOWLEDGE` identifies the embedded read-only semantic memory profile.

The bundle ends with `END MSSLB-LX` and an `@bundle sha256:` seal computed over the
LF-normalized bundle through that END line inclusive.
