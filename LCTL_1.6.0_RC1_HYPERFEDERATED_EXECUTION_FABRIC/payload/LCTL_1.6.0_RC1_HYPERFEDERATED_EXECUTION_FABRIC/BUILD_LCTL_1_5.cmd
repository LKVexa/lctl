@echo off
setlocal EnableExtensions
set "ROOT=%~dp0"
if exist "%ROOT%runtime\classes" rmdir /s /q "%ROOT%runtime\classes"
mkdir "%ROOT%runtime\classes" || exit /b 1
javac -encoding UTF-8 -d "%ROOT%runtime\classes" "%ROOT%runtime\src\LctlMain.java" "%ROOT%runtime\src\LctlFabricMain.java" "%ROOT%runtime\src\LctlMeshMain.java" "%ROOT%runtime\src\LctlFederatedMain.java" || exit /b 1
jar --create --file "%ROOT%runtime\bin\lctl-federated.jar" --main-class LctlFederatedMain -C "%ROOT%runtime\classes" . || exit /b 1
echo PASS: built runtime\bin\lctl-federated.jar
exit /b 0
