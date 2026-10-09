@echo off
setlocal
set ROOT=%~dp0
set J=%ROOT%runtime\bin\lctl-hyperfederated.jar
java -jar "%J%" column-selftest || exit /b 1
java -jar "%J%" column-verify "%ROOT%examples_column\01_bell_state\program.lctlc" || exit /b 1
java -jar "%J%" verify "%ROOT%examples_column\01_bell_state\program.lctlc" || exit /b 1
java -jar "%J%" parallel-depth "%ROOT%examples_column\24_parallel_bell_pair_generation\program.lctlc" >nul || exit /b 1
echo PASS: LCTL-C compact column language verification complete.
