@echo off
setlocal
set "ROOT=%~dp0.."
python "%ROOT%\tools\run_lctl15_conformance.py"
exit /b %ERRORLEVEL%
