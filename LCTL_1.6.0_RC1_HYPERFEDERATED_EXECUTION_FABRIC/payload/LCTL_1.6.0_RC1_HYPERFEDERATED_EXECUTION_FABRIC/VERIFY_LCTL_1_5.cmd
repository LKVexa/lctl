@echo off
setlocal EnableExtensions
set "ROOT=%~dp0"
set "JAR=%ROOT%runtime\bin\lctl-federated.jar"
set "EX=%ROOT%examples\24_parallel_bell_pair_generation\program.lctl"
set "TOPO=%ROOT%topology\multi_route_logical_topology.json"
set "CFG=%ROOT%runtime\federation\reference_federation.json"
echo [1/9] 1.5 512-case conformance
java -jar "%JAR%" federation-selftest || exit /b 1
echo [2/9] 1.4 320-case baseline
java -jar "%JAR%" baseline14-check "%ROOT%" || exit /b 1
echo [3/9] federation plan
java -jar "%JAR%" federation-plan "%EX%" "%TOPO%" || exit /b 1
echo [4/9] native local federation
java -jar "%JAR%" run-federation "%EX%" "%CFG%" || exit /b 1
echo [5/9] actor/channel/pipeline
java -jar "%JAR%" run-actor 4 >nul || exit /b 1
java -jar "%JAR%" run-channel 4 >nul || exit /b 1
java -jar "%JAR%" run-pipeline 4 >nul || exit /b 1
echo [6/9] CRDT
java -jar "%JAR%" run-crdt G-COUNTER 4 || exit /b 1
echo [7/9] statevector equivalence
java -jar "%JAR%" simulate-statevector3 "%EX%" 2 || exit /b 1
echo [8/9] arbitrary-state remote CNOT
java -jar "%JAR%" remote-cnot-check 314159 || exit /b 1
echo [9/9] adapter ABI mock
java -jar "%JAR%" adapter-check3 "%ROOT%adapters\mocks\abi3_reference.json" || exit /b 1
echo PASS: LCTL 1.5 RC1 smoke verification complete.
exit /b 0
