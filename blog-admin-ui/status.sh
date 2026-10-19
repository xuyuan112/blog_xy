#!/usr/bin/env bash
# ----------------------------------------------------------------------------
#  blog-admin-ui 状态查询
#  作用：检查后端 (8080) + 前端 (5174) 是否在跑
# ----------------------------------------------------------------------------
set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_DIR="$SCRIPT_DIR/.pids"
BACKEND_PORT=8080
UI_PORT=5174

# 通过 PowerShell 拿端口对应的 PID
port_pid() {
    local port=$1
    powershell.exe -NoProfile -Command "
        (Get-NetTCPConnection -State Listen -LocalPort $port -ErrorAction SilentlyContinue |
            Select-Object -First 1 -ExpandProperty OwningProcess)
    " 2>/dev/null | tr -d '\r' | grep -E '^[0-9]+$' || true
}

check() {
    local name=$1 port=$2
    local pid
    pid=$(port_pid "$port")
    if [ -n "$pid" ]; then
        printf '  \033[1;32m●\033[0m  %-22s  http://localhost:%-5s  PID=%s\n' \
            "$name" "$port" "$pid"
    else
        printf '  \033[1;31m○\033[0m  %-22s  http://localhost:%-5s  未运行\n' \
            "$name" "$port"
    fi
}

echo "blog-admin-ui 状态："
check "blog-web-admin 后端" $BACKEND_PORT
check "blog-admin-ui 前端"  $UI_PORT
echo
echo "日志："
echo "  后端  $SCRIPT_DIR/../blog-parent/blog-web/blog-web-admin/logs/app.log"
echo "  前端  $SCRIPT_DIR/logs/ui.log"
echo "PID 文件目录: $PID_DIR"
