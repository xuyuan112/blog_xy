# 个人博客项目长期记忆

## frontend-firefly（前端，Astro 7 / Firefly 主题）
- **包管理器固定为 pnpm@11.24.0**（`packageManager` 字段），仓库只有 `pnpm-lock.yaml`，`package-lock.json` 已被 gitignore。
- **部署/安装一律用 `pnpm install --frozen-lockfile`**，禁止 `npm i`——npm 无锁文件会把 `@astrojs/starlight` 等解析到更新版本，与 `astro@7.2.4` 锁死的 `@astrojs/markdown-remark@7.2.4` 冲突（ERESOLVE）。
- Node 要求 ≥22.23.0（engines 字段；astro 7 最低 ^20.19 || ≥22.12）。
- 部署方式：传源码到服务器构建（dist 里有硬编码 Windows 路径，不能直接传 dist）。构建：`pnpm build`，启动：`pnpm start`（node ./dist/server/entry.mjs，端口 4321）。
- 服务器 nginx：`/xuyuan/` 反代到 127.0.0.1:8081（后端）；站点 URL http://120.26.119.28:4321。
- 用户不想动本地 node_modules，不要在本地跑 pnpm install。
