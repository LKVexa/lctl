@echo off
setlocal
set ROOT=%~dp0
call "%ROOT%BUILD_LCTL_1_6.cmd" || exit /b 1
java -jar "%ROOT%runtime\bin\lctl-hyperfederated.jar" fabric-selftest || exit /b 1
java -jar "%ROOT%runtime\bin\lctl-hyperfederated.jar" federation-selftest || exit /b 1
java -jar "%ROOT%runtime\bin\lctl-hyperfederated.jar" baseline14-check "%ROOT%" || exit /b 1
java -jar "%ROOT%runtime\bin\lctl-hyperfederated.jar" fabric-topology || exit /b 1
java -jar "%ROOT%runtime\bin\lctl-hyperfederated.jar" fabric-calibrate || exit /b 1
java -jar "%ROOT%runtime\bin\lctl-hyperfederated.jar" run-stealing "%ROOT%examples\24_parallel_bell_pair_generation\program.lctl" 4 128 || exit /b 1
echo PASS: LCTL 1.6 RC1 smoke verification complete.
