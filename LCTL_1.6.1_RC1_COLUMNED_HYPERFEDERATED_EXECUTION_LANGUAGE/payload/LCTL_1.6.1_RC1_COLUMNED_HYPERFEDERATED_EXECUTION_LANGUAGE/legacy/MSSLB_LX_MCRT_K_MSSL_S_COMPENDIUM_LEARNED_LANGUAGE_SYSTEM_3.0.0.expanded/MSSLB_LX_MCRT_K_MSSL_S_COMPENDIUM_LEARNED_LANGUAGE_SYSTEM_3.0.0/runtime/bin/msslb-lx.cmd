@echo off
setlocal
set "HERE=%~dp0"
java -jar "%HERE%msslb-lx-runtime.jar" %*
exit /b %ERRORLEVEL%
