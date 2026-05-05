@echo off
echo Fixing department staff assignments...
cd /d "%~dp0"

set "CP=lib\bson-4.5.0.jar;lib\mongodb-driver-core-4.5.0.jar;lib\mongodb-driver-sync-4.5.0.jar"

echo Compiling fix script...
javac -cp "%CP%" FixDepartmentStaff.java

if errorlevel 1 (
    echo Compile failed.
    pause
    exit /b 1
)

echo Running database fix...
java -cp "%CP%;." FixDepartmentStaff

pause
