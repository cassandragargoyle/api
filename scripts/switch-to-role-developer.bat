@echo off

if exist ".claude\roles\current.md" (
    del ".claude\roles\current.md"
)
copy /Y ".claude\roles\developer.md" ".claude\roles\current.md" >nul
echo Switched to role: DEVELOPER
echo.
echo Role Guidelines:
echo    - Provide minimal diffs, explain impact, add tests
echo    - Follow project's code style and commit conventions
echo    - Include rollback steps
echo    - NEVER merge to main without tester's acceptance protocol