@echo off
if exist ".claude\roles\current.md" (
    del ".claude\roles\current.md"
)
copy /Y ".claude\roles\product_owner.md" ".claude\roles\current.md" >nul
echo Switched to role: Product Owner
