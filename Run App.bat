@echo off
setlocal enabledelayedexpansion

echo ========================================
echo   Smart Lost & Found System - Launcher
echo ========================================
echo.

cd /d "%~dp0SmartLostFoundSystemFX_2.O"

REM Set Java path
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Set Maven path
set "MAVEN_HOME=C:\Users\sount\Downloads\apache-maven-3.9.15-bin\apache-maven-3.9.15"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

REM JVM arguments for Java 21+ compatibility
set "MAVEN_OPTS=--enable-native-access=javafx.graphics --enable-native-access=javafx.controls"

echo Java: %JAVA_HOME%
echo Maven: %MAVEN_HOME%
echo.

echo Starting application...
echo.
echo IMPORTANT: A JavaFX window should appear on your screen!
echo.
echo If the application doesn't start, make sure:
echo - MySQL server is running on port 8082
echo - Database 'lostfound' exists
echo.

call mvn javafx:run

echo.
echo ========================================
echo Application closed.
echo ========================================
pause