# Smart Lost & Found System - PowerShell Launcher
# Run this script to start the application with one click

$projectPath = Join-Path $PSScriptRoot "SmartLostFoundSystemFX_2.O"

# Set Java and Maven paths
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot"
$env:MAVEN_HOME = "C:\Users\sount\Downloads\apache-maven-3.9.15-bin\apache-maven-3.9.15"
$env:Path = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:Path"

# JVM arguments for Java 21+ compatibility
$env:MAVEN_OPTS = "--enable-native-access=javafx.graphics --enable-native-access=javafx.controls"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Smart Lost & Found System - Launcher" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Java: $env:JAVA_HOME" -ForegroundColor Gray
Write-Host "Maven: $env:MAVEN_HOME" -ForegroundColor Gray
Write-Host ""

Write-Host "Starting application..." -ForegroundColor Green
Write-Host ""
Write-Host "If the application doesn't start, make sure:" -ForegroundColor Yellow
Write-Host "- MySQL server is running on port 8082" -ForegroundColor Yellow
Write-Host "- Database 'lostfound' exists" -ForegroundColor Yellow
Write-Host ""

Set-Location $projectPath
& "$env:MAVEN_HOME\bin\mvn.cmd" javafx:run

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Application closed." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Read-Host "Press Enter to exit"