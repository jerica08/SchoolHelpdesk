@echo off
cd /d "%~dp0"

set "CP=lib\bson-4.5.0.jar;lib\mongodb-driver-core-4.5.0.jar;lib\mongodb-driver-sync-4.5.0.jar;lib\jfreechart-1.5.4.jar;lib\jcommon-1.0.24.jar;target\classes"

if not exist "src\main\java\schoolhelpdesk\SchoolHelpdesk.java" (
  echo Missing sources under src\main\java. Cannot build.
  exit /b 1
)

echo Compiling...
if not exist "target\classes" mkdir "target\classes"
xcopy /E /I /Y /Q "src\main\resources\*" "target\classes\" >nul 2>nul
dir /s /b "src\main\java\*.java" > sources_list.txt
javac --release 17 -encoding UTF-8 -cp "%CP%" -d "target\classes" @sources_list.txt
if errorlevel 1 (
  echo Compile failed. Ensure JDK 17+ is on PATH and lib\ contains the JARs listed in this script ^(same versions as pom.xml^).
  exit /b 1
)

echo Starting application...
java -cp "%CP%" schoolhelpdesk.SchoolHelpdesk
del /q sources_list.txt 2>nul
