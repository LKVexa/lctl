# Mental Model: Rows, Columns, Frames, Authority


A Columned LCTL program is a unit containing one or more frames. A frame contains executable or declarative rows. Every row has eight source cells:

`ID │ LANE │ OP │ OUT │ CTRL │ IN │ ARG │ META`

The source row is not the final semantic record. Compilation reconstructs ten additional semantic cells and emits the canonical 18-cell row. This makes compact source readable without discarding explicit semantic authority.

Think of the eight columns as the **changing surface** and the canonical cells as the **semantic closure** of the row.
