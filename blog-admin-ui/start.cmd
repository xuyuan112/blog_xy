@echo off
REM ============================================================================
REM  blog-admin-ui 启动脚本 (Windows 原生 cmd)
REM  作用：拉起 blog-web-admin 后端 (8080) + Vite dev (5174)
REM  用法：start.cmd            启动全部
REM        start.cmd backend    只启动后端
REM        start.cmd ui         只启动前端
REM ============================================================================
setlocal EnableExtensions EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%.."
set "BACKEND_DIR=%PROJECT_ROOT%\blog-parent\blog-web\blog-web-admin"
set "BACKEND_JAR=%BACKEND_DIR%\target\blog-web-admin-1.0.0.jar"
set "BACKEND_LOG_DIR=%BACKEND_DIR%\logs"
set "BACKEND_LOG=%BACKEND_LOG_DIR%\app.log"
set "BACKEND_PORT=8080"
if not defined BLOG_ADMIN_PROFILE set "BLOG_ADMIN_PROFILE=local"
set "UI_PORT=5174"
set "UI_LOG_DIR=%SCRIPT_DIR%logs"
set "UI_LOG=%UI_LOG_DIR%\ui.log"
set "PID_DIR=%SCRIPT_DIR%.pids"
set "BACKEND_PID_FILE=%PID_DIR%\backend.pid"
set "UI_PID_FILE=%PID_DIR%\ui.pid"

REM 优先使用隔离托管的 Node
set "MANAGED_NODE=C:\Users\许袁\.workbuddy\binaries\node\versions\22.22.2-2\node.exe"
if exist "%MANAGED_NODE%" (set "NODE_BIN=%MANAGED_NODE%") else (
    for /f "delims=" %%i in ('where node 2^>nul') do (set "NODE_BIN=%%i" & goto :node_found)
    :node_found
)

if not exist "%BACKEND_LOG_DIR%" mkdir "%BACKEND_LOG_DIR%"
if not exist "%UI_LOG_DIR%"     mkdir "%UI_LOG_DIR%"
if not exist "%PID_DIR%"        mkdir "%PID_DIR%"

REM ---- 工具函数 ---------------------------------------------------------------
:port_in_use
powershell -NoProfile -Command "$c = Get-NetTCPConnection -State Listen -LocalPort %1 -ErrorAction SilentlyContinue; if ($c) { exit 0 } else { exit 1 }" >nul 2>&1
exit /b %ERRORLEVEL%

:pid_alive
powershell -NoProfile -Command "try { Get-Process -Id %2 -ErrorAction Stop | Out-Null; exit 0 } catch { exit 1 }" >nul 2>&1
exit /b %ERRORLEVEL%

:wait_port
set /a count=0
:wait_loop
call :port_in_use %1
if %ERRORLEVEL%==0 exit /b 0
set /a count+=1
if %count% geq %2 exit /b 1
timeout /t 1 /nobreak >nul
goto :wait_loop

REM ---- 启动后端 ---------------------------------------------------------------
:start_backend
call :port_in_use %BACKEND_PORT%
if %ERRORLEVEL%==0 (
    echo [start] 后端端口 %BACKEND_PORT% 已在监听，跳过启动
    exit /b 0
)
if not exist "%BACKEND_JAR%" (
    echo [fail] 找不到后端 jar: %BACKEND_JAR% 1>&2
    echo        请先在 blog-parent 跑 mvn package -pl blog-web/blog-web-admin -am 1>&2
    exit /b 1
)
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [fail] 未找到 java，请安装 JDK 并加入 PATH 1>&2
    exit /b 1
)

echo [start] 启动 blog-web-admin (port %BACKEND_PORT%, profile=%BLOG_ADMIN_PROFILE%) ...
start "blog-web-admin" /B java -jar "%BACKEND_JAR%" --spring.profiles.active=%BLOG_ADMIN_PROFILE% > "%BACKEND_LOG%" 2>&1
REM 抓最近启动的 java 进程 PID 存盘（粗略匹配，可能误判但 stop.sh 会按端口兜底）
powershell -NoProfile -Command "Get-Process java -ErrorAction SilentlyContinue | Sort-Object StartTime -Descending | Select-Object -First 1 -ExpandProperty Id" > "%BACKEND_PID_FILE%"

call :wait_port %BACKEND_PORT% 40
if %ERRORLEVEL%==0 (
    echo [ok] 后端已就绪  http://localhost:%BACKEND_PORT%  ^(日志: %BACKEND_LOG%^)
    exit /b 0
) else (
    echo [fail] 后端启动超时，请查看日志: %BACKEND_LOG% 1>&2
    exit /b 1
)

REM ---- 启动前端 ---------------------------------------------------------------
:start_ui
call :port_in_use %UI_PORT%
if %ERRORLEVEL%==0 (
    echo [start] 前端端口 %UI_PORT% 已在监听，跳过启动
    exit /b 0
)
if not exist "%NODE_BIN%" (
    echo [fail] 未找到 node 可执行文件: %NODE_BIN% 1>&2
    exit /b 1
)
if not exist "%SCRIPT_DIR%node_modules" (
    echo [fail] 缺少 node_modules，请先 npm install 1>&2
    exit /b 1
)

echo [start] 启动 blog-admin-ui Vite dev (port %UI_PORT%) ...
start "blog-admin-ui" /B "%NODE_BIN%" "%SCRIPT_DIR%node_modules\vite\bin\vite.js" > "%UI_LOG%" 2>&1
powershell -NoProfile -Command "Get-Process node -ErrorAction SilentlyContinue | Sort-Object StartTime -Descending | Select-Object -First 1 -ExpandProperty Id" > "%UI_PID_FILE%"

call :wait_port %UI_PORT% 30
if %ERRORLEVEL%==0 (
    echo [ok] 前端已就绪  http://localhost:%UI_PORT%  ^(日志: %UI_LOG%^)
    exit /b 0
) else (
    echo [fail] 前端启动超时，请查看日志: %UI_LOG% 1>&2
    exit /b 1
)

REM ---- 入口 -------------------------------------------------------------------
set "TARGET=%~1"
if "%TARGET%"=="" set "TARGET=all"

if /i "%TARGET%"=="backend" (call :start_backend & exit /b %ERRORLEVEL%)
if /i "%TARGET%"=="ui"     (call :start_ui     & exit /b %ERRORLEVEL%)
if /i "%TARGET%"=="all"    (call :start_backend & call :start_ui & exit /b %ERRORLEVEL%)

echo [fail] 未知目标: %TARGET% ^(可用: backend ^| ui ^| all^) 1>&2
exit /b 2
