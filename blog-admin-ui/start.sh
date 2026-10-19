#!/usr/bin/env bash
# ----------------------------------------------------------------------------
#  blog-admin-ui 启动脚本
#  作用：拉起 blog-web-admin 后端 (8080) + 当前 Vite dev 服务 (5174)
#  用法：./start.sh           # 启动全部（已运行会跳过）
#        ./start.sh backend  # 只启动后端
#        ./start.sh ui       # 只启动前端
#  说明：进程通过 scripts/launcher.py 启动（DETACHED_PROCESS + CREATE_NEW_PROCESS_GROUP），
#        彻底脱离当前 shell 进程组，shell 退出不会被一起杀掉。
# ----------------------------------------------------------------------------
set -u

# ---- 路径常量 ---------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT/blog-parent/blog-web/blog-web-admin"
BACKEND_JAR="$BACKEND_DIR/target/blog-web-admin-1.0.0.jar"
BACKEND_LOG_DIR="$BACKEND_DIR/logs"
BACKEND_LOG="$BACKEND_LOG_DIR/app.log"
BACKEND_PORT=8080
BACKEND_PROFILE="${BLOG_ADMIN_PROFILE:-local}"

UI_PORT=5174
UI_LOG_DIR="$SCRIPT_DIR/logs"
UI_LOG="$UI_LOG_DIR/ui.log"
PID_DIR="$SCRIPT_DIR/.pids"
BACKEND_PID_FILE="$PID_DIR/backend.pid"
UI_PID_FILE="$PID_DIR/ui.pid"
LAUNCHER="$SCRIPT_DIR/scripts/launcher.py"

# 优先用隔离托管的 Node（避免 PATH 上的版本漂移）
MANAGED_NODE="/c/Users/许袁/.workbuddy/binaries/node/versions/22.22.2-2/node.exe"
[ -x "$MANAGED_NODE" ] && NODE_BIN="$MANAGED_NODE" || NODE_BIN="$(command -v node)"

# Python 解释器（用本机 PATH 里的 python 即可）
PYTHON_BIN="${PYTHON_BIN:-python}"

# ---- 工具函数 ---------------------------------------------------------------
log()  { printf '\033[1;36m[start]\033[0m %s\n' "$*"; }
ok()   { printf '\033[1;32m[ ok ]\033[0m %s\n' "$*"; }
warn() { printf '\033[1;33m[warn]\033[0m %s\n' "$*"; }
err()  { printf '\033[1;31m[fail]\033[0m %s\n' "$*" >&2; }

ensure_dirs() {
    mkdir -p "$BACKEND_LOG_DIR" "$UI_LOG_DIR" "$PID_DIR"
}

# 通过 PowerShell 查端口监听（跨 shell 都能跑）
port_in_use() {
    local port=$1
    powershell.exe -NoProfile -Command "
        \$c = Get-NetTCPConnection -State Listen -LocalPort $port -ErrorAction SilentlyContinue
        if (\$c) { exit 0 } else { exit 1 }
    " >/dev/null 2>&1
}

# 按 PID 文件判断是否还活着
pid_alive() {
    local pid_file=$1
    [ -f "$pid_file" ] || return 1
    local pid
    pid=$(cat "$pid_file" 2>/dev/null) || return 1
    powershell.exe -NoProfile -Command "
        try { Get-Process -Id $pid -ErrorAction Stop | Out-Null; exit 0 } catch { exit 1 }
    " >/dev/null 2>&1
}

# 等待端口上线（最多 N 秒）
wait_port() {
    local port=$1 timeout=${2:-30}
    for ((i=0; i<timeout; i++)); do
        if port_in_use "$port"; then return 0; fi
        sleep 1
    done
    return 1
}

# 用 launcher.py 真正脱离 shell 启动一个进程
launch_detached() {
    local cwd=$1 log=$2 pid_file=$3
    shift 3
    ensure_dirs
    "$PYTHON_BIN" "$LAUNCHER" "$(cygpath -w "$cwd")" "$(cygpath -w "$log")" \
        "$(cygpath -w "$pid_file")" "$@"
}

# ---- 启动后端 ---------------------------------------------------------------
start_backend() {
    if port_in_use "$BACKEND_PORT"; then
        ok "后端端口 $BACKEND_PORT 已在监听，跳过启动"
        return 0
    fi
    if pid_alive "$BACKEND_PID_FILE"; then
        warn "检测到残留 PID 文件但端口未起，清理后重试"
        rm -f "$BACKEND_PID_FILE"
    fi
    if [ ! -f "$BACKEND_JAR" ]; then
        err "找不到后端 jar: $BACKEND_JAR"
        err "请先在 blog-parent 跑 mvn package -pl blog-web/blog-web-admin -am"
        return 1
    fi
    if ! command -v java >/dev/null 2>&1; then
        err "未找到 java，请安装 JDK 并加入 PATH"
        return 1
    fi
    if [ ! -f "$LAUNCHER" ]; then
        err "找不到 launcher: $LAUNCHER"
        return 1
    fi

    log "启动 blog-web-admin (port $BACKEND_PORT, profile=$BACKEND_PROFILE) ..."
    # Java 命令中需要 Windows 路径
    local WIN_JAR
    WIN_JAR=$(cygpath -w "$BACKEND_JAR")
    launch_detached "$BACKEND_DIR" "$BACKEND_LOG" "$BACKEND_PID_FILE" \
        java -jar "$WIN_JAR" --spring.profiles.active="$BACKEND_PROFILE"

    if wait_port "$BACKEND_PORT" 50; then
        ok "后端已就绪  http://localhost:$BACKEND_PORT  (日志: $BACKEND_LOG)"
    else
        err "后端启动超时，请查看日志: $BACKEND_LOG"
        return 1
    fi
}

# ---- 启动前端 ---------------------------------------------------------------
start_ui() {
    if port_in_use "$UI_PORT"; then
        ok "前端端口 $UI_PORT 已在监听，跳过启动"
        return 0
    fi
    if pid_alive "$UI_PID_FILE"; then
        warn "检测到残留 PID 文件但端口未起，清理后重试"
        rm -f "$UI_PID_FILE"
    fi
    if [ ! -x "$NODE_BIN" ]; then
        err "未找到 node 可执行文件: $NODE_BIN"
        return 1
    fi
    if [ ! -d "$SCRIPT_DIR/node_modules" ]; then
        err "缺少 node_modules，请先 npm install"
        return 1
    fi

    log "启动 blog-admin-ui Vite dev (port $UI_PORT) ..."
    local WIN_NODE WIN_VITE
    WIN_NODE=$(cygpath -w "$NODE_BIN")
    WIN_VITE=$(cygpath -w "$SCRIPT_DIR/node_modules/vite/bin/vite.js")
    launch_detached "$SCRIPT_DIR" "$UI_LOG" "$UI_PID_FILE" \
        "$WIN_NODE" "$WIN_VITE"

    if wait_port "$UI_PORT" 30; then
        ok "前端已就绪  http://localhost:$UI_PORT  (日志: $UI_LOG)"
    else
        err "前端启动超时，请查看日志: $UI_LOG"
        return 1
    fi
}

# ---- 入口 -------------------------------------------------------------------
target="${1:-all}"
case "$target" in
    backend) start_backend ;;
    ui)      start_ui ;;
    all)     start_backend && start_ui ;;
    *)       err "未知目标: $target（可用: backend | ui | all）"; exit 2 ;;
esac
exit $?
