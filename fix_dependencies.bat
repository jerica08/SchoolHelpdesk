@echo off
echo School Helpdesk — dependency refresh
echo.
echo This script only removes the Maven "target" folder so the next build is clean.
echo It does NOT delete "build" (legacy NetBeans output) or "lib" (optional local JARs).
echo.

if exist "target" (
    echo Removing target...
    rmdir /s /q "target"
)

echo Done. With Maven on PATH, from this folder run:
echo   mvn -q compile exec:java
echo.
pause
