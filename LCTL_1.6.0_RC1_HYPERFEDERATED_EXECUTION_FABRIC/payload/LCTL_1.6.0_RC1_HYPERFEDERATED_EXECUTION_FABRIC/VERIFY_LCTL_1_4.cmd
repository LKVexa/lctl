@echo off
setlocal
set "ROOT=%~dp0"
set "EX=%ROOT%examples\24_parallel_bell_pair_generation\program.lctl"
set "DENS=%ROOT%examples\09_density_matrix_noise\program.lctl"
set "TEL=%ROOT%examples\42_teleportation_across_logical_nodes\program.lctl"
echo [1/5] internal 320-case invariant selftest
java -jar "%ROOT%runtime\bin\lctl-mesh.jar" selftest14 dummy || exit /b 1
echo [2/5] partial-order smoke
java -jar "%ROOT%runtime\bin\lctl-mesh.jar" partial-order "%EX%" || exit /b 1
echo [3/5] process-sharded density smoke
java -jar "%ROOT%runtime\bin\lctl-mesh.jar" simulate-density2 "%DENS%" 2 || exit /b 1
echo [4/5] teleportation numerical equivalence smoke
java -jar "%ROOT%runtime\bin\lctl-mesh.jar" protocol-run "%TEL%" 314159 || exit /b 1
echo [5/5] physical boundary fail-closed
java -jar "%ROOT%runtime\bin\lctl-mesh.jar" physical-distributed-run "%EX%" >nul 2>nul
if %ERRORLEVEL% EQU 0 exit /b 1
echo PASS: LCTL 1.4 smoke verification complete.
echo Full suites: python tools\run_lctl13_conformance.py and python tools\run_lctl14_conformance.py
echo Required 8-hour soak is separate.
exit /b 0
