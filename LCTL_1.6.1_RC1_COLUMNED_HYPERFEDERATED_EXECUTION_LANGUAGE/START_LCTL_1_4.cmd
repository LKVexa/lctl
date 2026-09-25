@echo off
setlocal
set "ROOT=%~dp0"
java -jar "%ROOT%runtime\bin\lctl-mesh.jar" %*
exit /b %ERRORLEVEL%
