@echo off
echo =========================================
echo   Inventory Management System - Launcher
echo =========================================
echo.

:: Find javac
where javac >nul 2>&1
if %errorlevel% == 0 (
    set JAVAC=javac
    set JAVA=java
) else (
    :: Try Microsoft OpenJDK path
    for /d %%i in ("C:\Program Files\Microsoft\jdk-*") do (
        if exist "%%i\bin\javac.exe" (
            set JAVAC=%%i\bin\javac.exe
            set JAVA=%%i\bin\java.exe
        )
    )
)

if "%JAVAC%"=="" (
    echo ERROR: Java JDK not found. Please install JDK 17 or later.
    pause
    exit /b 1
)

echo [1/2] Compiling source files...
"%JAVAC%" -d out src\*.java
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed.
    pause
    exit /b 1
)

echo [2/2] Launching application...
echo.
"%JAVA%" -cp out InventoryApp
pause
