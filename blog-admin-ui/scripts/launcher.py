"""
持久化启动 java/vite 进程：让它们彻底脱离当前 shell。
- DETACHED_PROCESS: 不继承父控制台
- CREATE_NEW_PROCESS_GROUP: 独立进程组
- 关闭 stdin/stdout/stderr 父管道，避免阻塞
"""
import os
import sys
import time
import subprocess

if len(sys.argv) < 5:
    print("usage: launcher.py <cwd> <log> <pid-file> <cmd...>", file=sys.stderr)
    sys.exit(2)

cwd = sys.argv[1]
log = sys.argv[2]
pid_file = sys.argv[3]
cmd = sys.argv[4:]

# 用追加模式打开日志（不覆盖，便于多看几次启动）
log_fp = open(log, "ab", buffering=0)
log_fp.write(f"\n[{time.strftime('%Y-%m-%d %H:%M:%S')}] launching: {cmd}\n".encode())

# 关键 flags：让进程彻底脱离父 shell
creationflags = (
    subprocess.DETACHED_PROCESS  # 0x00000008 - 脱离父控制台
    | subprocess.CREATE_NEW_PROCESS_GROUP  # 0x00000200 - 新进程组，免疫 Ctrl+C
)

p = subprocess.Popen(
    cmd,
    cwd=cwd,
    stdout=log_fp,
    stderr=subprocess.STDOUT,
    stdin=subprocess.DEVNULL,
    creationflags=creationflags,
    close_fds=True,
)
# 立刻把 PID 写盘（不要等进程启动完）
with open(pid_file, "w") as f:
    f.write(str(p.pid))
print(p.pid)
log_fp.write(f"[{time.strftime('%Y-%m-%d %H:%M:%S')}] launched PID {p.pid}\n".encode())
log_fp.flush()
