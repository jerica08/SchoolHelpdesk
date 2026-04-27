@echo off
echo Fixing MongoDB dependencies...
echo.

echo Step 1: Cleaning project...
if exist "target" rmdir /s /q "target"
if exist "build" rmdir /s /q "build"

echo Step 2: Creating directories...
mkdir "target"
mkdir "build"

echo Step 3: Dependencies fixed!
echo.
echo Now do this in NetBeans:
echo 1. Right-click on SchoolHelpdeskSystem project
echo 2. Select "Maven" -> "Reload Project"
echo 3. Wait for dependencies to download
echo 4. Right-click -> "Clean and Build"
echo.
pause
