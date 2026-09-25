@echo off
setlocal
python "%~dp0run_distributed_conformance.py"
exit /b %errorlevel%
