@echo off
setlocal
set "ROOT=%~dp0"
java -jar "%ROOT%runtime\bin\lctl-federated.jar" %*
exit /b %ERRORLEVEL%
