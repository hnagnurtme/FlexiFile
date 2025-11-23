@echo off
setlocal enabledelayedexpansion

echo ================================
echo   TOMCAT DEPLOYER FOR WINDOWS
echo ================================
echo.

:: ---- Nhập đường dẫn TOMCAT ----
set /p TOMCAT_DIR=Nhập đường dẫn Tomcat (ví dụ C:\tomcat-10): 

if not exist "%TOMCAT_DIR%" (
    echo ERROR: Folder %TOMCAT_DIR% khong ton tai.
    exit /b 1
)

echo.
echo ==> Building Maven project...
mvn clean package -DskipTests

:: --- Tìm file WAR trong target ---
for %%f in ("target\*.war") do (
    set WAR_FILE=%%f
)

if not defined WAR_FILE (
    echo ERROR: Khong tim thay file WAR trong folder target.
    exit /b 1
)

:: Lấy context bằng tên file WAR (bỏ đuôi .war)
set FILENAME=%WAR_FILE%
set FILENAME=%FILENAME:target\=%
set CONTEXT_NAME=%FILENAME:.war=%

echo.
echo ==> Stopping Tomcat...
call "%TOMCAT_DIR%\bin\shutdown.bat"

echo.
echo ==> Deploying !WAR_FILE! to context "%CONTEXT_NAME%" ...
copy /Y "!WAR_FILE!" "%TOMCAT_DIR%\webapps\%CONTEXT_NAME%.war" > nul

echo.
echo ==> Starting Tomcat...
call "%TOMCAT_DIR%\bin\startup.bat"

echo.
echo Application running at:
echo     http://localhost:8080/%CONTEXT_NAME%
echo.
echo Nhan CTRL+C de dung script nay.
echo.

:: Giữ script chạy để Ctrl+C có tác dụng
:loop
timeout /t 1 > nul
goto loop
