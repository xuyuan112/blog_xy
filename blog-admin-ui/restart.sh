#!/usr/bin/env bash
# ----------------------------------------------------------------------------
#  blog-admin-ui 重启脚本
# ----------------------------------------------------------------------------
set -u
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
"$SCRIPT_DIR/stop.sh" "$@"
sleep 1
"$SCRIPT_DIR/start.sh" "$@"
