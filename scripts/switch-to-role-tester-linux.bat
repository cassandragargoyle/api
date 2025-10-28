@echo off

REM OS validation for local testing
ver | find "Windows" >nul
if %ERRORLEVEL% EQU 0 (
    echo WARNING: You are running on Windows OS but switching to Linux tester role
    echo    This role is designed for Linux testing and should only accept Linux tests on local host
    echo    For container/VM testing, accept tests based on container/VM OS, not host OS
    echo.
)

if exist ".claude\roles\current.md" (
    del ".claude\roles\current.md"
)
copy /Y ".claude\roles\tester-linux.md" ".claude\roles\current.md" >nul
echo Switched to role: TESTER (Linux)
echo.
echo Role Guidelines:
echo    - Local host testing: Only accept Linux tests
echo    - Container/VM testing: Accept tests based on container/VM OS
echo    - Always document tested OS in acceptance protocol
