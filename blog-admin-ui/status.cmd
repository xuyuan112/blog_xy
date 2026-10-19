@echo off
REM ============================================================================
REM  blog-admin-ui 状态查询 (Windows 原生 cmd)
REM ============================================================================
setlocal EnableExtensions

set "SCRIPT_DIR=%~dp0"
set "PID_DIR=%SCRIPT_DIR%.pids"
set "BACKEND_PORT=8080"
set "UI_PORT=5174"

echo blog-admin-ui 状态：

for %%p in (%BACKEND_PORT% %UI_PORT%) do (
    set "PORT=%%p"
    for /f "delims=" %%i in ('powershell -NoProfile -Command "(Get-NetTCPConnection -State Listen -LocalPort %%p -ErrorAction SilentlyContinue ^| Select-Object -First 1 -ExpandProperty OwningProcess)" 2^>nul') do (
        set "PID=%%i"
    )
    if defined PID (
        echo   ●  port=!PORT!  PID=!PID!
    ) else (
        echo   ○  port=!PORT!  未运行
    )
    set "PID="
)

echo.
echo 日志：
echo   后端  %SCRIPT_DIR%..\blog-parent\blog-web\blog-web-admin\logs\app.log
echo   前端  %SCRIPT_DIR%logs\ui.log
echo PID 文件目录: %PID_DIR%
