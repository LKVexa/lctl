# LCTL 1.4 Soak Report

A short smoke soak was executed:

- requested hours: `0.001`
- actual hours: `0.0010011837155555555`
- iterations: **4340**
- stable result hash: `d6e579bc3e66790cf14b1982a705beab43ccbaf085cd755873cff91412cfc929`
- result: **PASS**

The governing 1.4 workflow requires a minimum **8-hour local distributed soak** for release-candidate qualification. That 8-hour run was **not executed in this build session**, so the corresponding qualification gate remains `BLOCKED`.
