#!/usr/bin/env bash
# ----------------------------------------------------------------------------
#  blog-admin-ui 停止脚本
#  作用：停掉 start.sh 启动的后端 + 前端
#  用法：./stop.sh           # 停全部
#        ./stop.sh backend   # 只停后端
#        ./stop.sh ui        # 只停前端
#  注意：Windows 下用 PowerShell Stop-Process -Force 跨会话杀进程
# ----------------------------------------------------------------------------
set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_DIR="$SCRIPT_DIR/.pids"
BACKEND_PORT=8080
UI_PORT=5174

log()  { printf '\033[1;36m[stop]\033[0m %s\n' "$*"; }
ok()   { printf '\033[1;32m[ ok ]\033[0m %s\n' "$*"; }
warn() { printf '\033[1;33m[warn]\033[0m %s\n' "$*"; }
err()  { printf '\033[1;31m[fail]\033[0m %s\n' "$*" >&2; }

# PowerShell 杀指定 PID（兼容后台/无 TTY 进程）
kill_pid() {
    local pid=$1
    powershell.exe -NoProfile -Command "
        try { Stop-Process -Id $pid -Force -ErrorAction Stop; exit 0 } catch { exit 1 }
    " >/dev/null 2>&1
}

# PowerShell 按端口杀进程
kill_port() {
    local port=$1
    powershell.exe -NoProfile -Command "
        Get-NetTCPConnection -State Listen -LocalPort $port -ErrorAction SilentlyContinue |
            ForEach-Object { try { Stop-Process -Id \$_.OwningProcess -Force -ErrorAction SilentlyContinue } catch {} }
    " >/dev/null 2>&1
}

stop_one() {
    local name=$1 port=$2 pid_file=$3
    if [ -f "$pid_file" ]; then
        local pid
        pid=$(cat "$pid_file" 2>/dev/null)
        if [ -n "$pid" ] && kill_pid "$pid"; then
            ok "$name (PID $pid) 已停止"
        else
            warn "$name PID $pid 已不在，尝试按端口清理"
            kill_port "$port"
        fi
        rm -f "$pid_file"
    else
        # 没有 PID 文件，兜底按端口杀
        kill_port "$port" && warn "$name 端口 $port 上有进程，已清理" || true
    fi
}

target="${1:-all}"
case "$target" in
    backend) stop_one "blog-web-admin 后端" $BACKEND_PORT "$PID_DIR/backend.pid" ;;
    ui)      stop_one "blog-admin-ui 前端"   $UI_PORT      "$PID_DIR/ui.pid" ;;
    all)
        stop_one "blog-admin-ui 前端"   $UI_PORT      "$PID_DIR/ui.pid"
        stop_one "blog-web-admin 后端" $BACKEND_PORT "$PID_DIR/backend.pid"
        ;;
    *) err "未知目标: $target（可用: backend | ui | all）"; exit 2 ;;
esac
