@echo off
setlocal
set ROOT=%~dp0
call "%ROOT%VERIFY_LCTL_1_6.cmd" || exit /b 1
call "%ROOT%VERIFY_LCTL_COLUMN.cmd" || exit /b 1
echo PASS: LCTL 1.6.1 columned hyperfederated verification complete.
