@echo off
REM run.bat - build project and run Tomcat on Windows
REM Usage: run.bat

setlocal

cd /d "%~dp0"

echo ==^> Building project...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Build failed!
    exit /b 1
)

echo.
echo ==^> Starting Tomcat...
echo     (Press Ctrl+C to stop)
echo.
call mvn org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run

endlocal
