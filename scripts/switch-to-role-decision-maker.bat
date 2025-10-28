@echo off

if exist ".claude\roles\current.md" (
    del ".claude\roles\current.md"
)
copy /Y ".claude\roles\decision-maker.md" ".claude\roles\current.md" >nul

echo Switched to Decision Maker role
echo.
echo Role: Architectural decisions and component placement
echo.
echo Key responsibilities:
echo - Analyze issues and decide on architecture
echo - Split functionality between core, plugins, and 3rd party
echo - Suggest better alternatives (e.g. Podman over Docker)
echo - Recommend issue modifications for better integration
echo.
echo Decision framework:
echo CORE: Essential functionality for all users
echo PLUGIN: Specialized developer tools
echo 3RD PARTY: External quality solutions
