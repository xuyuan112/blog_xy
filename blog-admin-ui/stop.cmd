@echo off
REM ============================================================================
REM  blog-admin-ui 停止脚本 (Windows 原生 cmd)
REM ============================================================================
setlocal EnableExtensions EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
set "PID_DIR=%SCRIPT_DIR%.pids"
set "BACKEND_PORT=8080"
set "UI_PORT=5174"

:stop_one
set "NAME=%~1"
set "PORT=%~2"
set "PID_FILE=%~3"
if exist "%PID_FILE%" (
    set /p PID=<"%PID_FILE%"
    if not "!PID!"=="" (
        powershell -NoProfile -Command "try { Stop-Process -Id !PID! -Force -ErrorAction Stop; exit 0 } catch { exit 1 }" >nul 2>&1
        if !ERRORLEVEL!==0 (echo [ok]  !NAME! ^(PID !PID!^) 已停止) else (echo [warn] !NAME! PID !PID! 已不在，尝试按端口清理)
    )
    del /f /q "%PID_FILE%" 2>nul
) else (
    powershell -NoProfile -Command "Get-NetTCPConnection -State Listen -LocalPort %PORT% -ErrorAction SilentlyContinue | ForEach-Object { try { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue } catch {} }" >nul 2>&1
)
exit /b 0

set "TARGET=%~1"
if "%TARGET%"=="" set "TARGET=all"

if /i "%TARGET%"=="backend" (call :stop_one "blog-web-admin 后端" %BACKEND_PORT% "%PID_DIR%\backend.pid" & exit /b 0)
if /i "%TARGET%"=="ui"      (call :stop_one "blog-admin-ui 前端"   %UI_PORT%      "%PID_DIR%\ui.pid"      & exit /b 0)
if /i "%TARGET%"=="all"     (
    call :stop_one "blog-admin-ui 前端"   %UI_PORT%      "%PID_DIR%\ui.pid"
    call :stop_one "blog-web-admin 后端" %BACKEND_PORT% "%PID_DIR%\backend.pid"
    exit /b 0
)

echo [fail] 未知目标: %TARGET% ^(可用: backend ^| ui ^| all^) 1>&2
exit /b 2
