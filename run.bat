@echo off
echo ================================
echo Bajaj Hiring App - Quick Start
echo ================================
echo.

REM Check if JAR exists
if exist "target\bajaj-hiring-0.1.0.jar" (
    echo [OK] JAR file found
    echo.
    echo Running application...
    echo.
    java -jar target\bajaj-hiring-0.1.0.jar
) else (
    echo [!] JAR file not found. Building now...
    echo.
    
    REM Set JAVA_HOME if needed
    if "%JAVA_HOME%"=="" (
        echo Setting JAVA_HOME...
        set JAVA_HOME=C:\Program Files\Java\jdk-23
    )
    
    echo Building project...
    call mvnw.cmd clean package -DskipTests
    
    echo.
    echo Build complete! Running application...
    echo.
    java -jar target\bajaj-hiring-0.1.0.jar
)

echo.
echo ================================
echo Execution completed!
echo Check target\solution.sql for the generated SQL query.
echo ================================
pause
