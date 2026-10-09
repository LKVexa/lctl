@echo off
setlocal
set ROOT=%~dp0
if exist "%ROOT%runtime\classes16" rmdir /s /q "%ROOT%runtime\classes16"
mkdir "%ROOT%runtime\classes16"
javac -encoding UTF-8 -d "%ROOT%runtime\classes16" "%ROOT%runtime\src\*.java"
if errorlevel 1 exit /b 1
jar --create --file "%ROOT%runtime\bin\lctl-hyperfederated.jar" --main-class LctlHyperFederatedMain -C "%ROOT%runtime\classes16" .
if errorlevel 1 exit /b 1
echo PASS: built runtime\bin\lctl-hyperfederated.jar
