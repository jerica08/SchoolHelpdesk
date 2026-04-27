@echo off
echo Testing SchoolHelpdesk main class...
echo.

cd /d "C:\Users\USER\OneDrive\Documents\NetBeansProjects\SchoolHelpdesk"

echo Setting classpath...
set CLASSPATH=src;build\classes

echo Compiling SchoolHelpdesk.java...
javac -d build\classes -cp "%CLASSPATH%" src\schoolhelpdesk\SchoolHelpdesk.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo Running SchoolHelpdesk...
    java -cp build\classes schoolhelpdesk.SchoolHelpdesk
) else (
    echo Compilation failed with error %ERRORLEVEL%
)

pause
