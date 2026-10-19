# frontend-firefly 后续重构 Backlog

更新时间：2026-09-03

## 总体进度

当前已完成核心业务链路约 75%。文章、杂谈、归档、搜索、相册、友链、关于、站点信息、评论、点赞和前台认证已经可以工作；剩余工作集中在旧主题壳层清理、配置收敛、生产构建和发布验收。

## 已完成

- [x] 固定后端唯一来源：`blog-parent/blog-web/blog-web-app`
- [x] 主导航收敛为首页、文章、杂谈、归档、相册、友链、关于、搜索
- [x] 删除全捕获 `[...page].astro`，增加显式首页和文章列表路由
- [x] 文章列表接入分页、分类和标签筛选
- [x] 文章详情接入 Markdown 安全渲染、TOC 数据、上下篇、评论和点赞
- [x] 杂谈列表/详情接入后端 moments，并复用 Markdown、评论、点赞
- [x] 归档接入时间线、分类和标签接口
- [x] 搜索接入 `keyword`、分页、空状态和重试
- [x] 相册列表、相册详情、照片详情和 EXIF
- [x] 友链列表和友链申请表单
- [x] 关于页和公告、一言、天气、日程组件
- [x] 登录、注册、认证状态、退出、修改密码
- [x] 删除旧 Bangumi、VNDB、MAL、书签、赞助、Bilibili、留言板、系列和动态路由
- [x] 保留壁纸、侧栏、浮动控制、Live2D 和 Spine 效果

## P0：必须优先完成

### P0-1 应用壳依赖收敛

状态：进行中

保留：壁纸、侧栏、浮动控制、Live2D、Spine。

处理：

- 检查 `MainGridLayout.astro` 和 `Layout.astro`，移除不再需要的旧页面专属插槽和逻辑。
- 保证 Navbar、WallpaperSection、SideBar、FloatingControls 的运行时行为不变。
- 为首页、文章详情、杂谈详情、相册详情确认壳层在 SSR 和客户端 hydration 后一致。

验收：以上公共能力仍可用，核心页面无旧业务文字或入口。

### P0-2 配置和类型清理

状态：未开始

检查并分批处理：

- `src/config/siteConfig.ts` 中 Bangumi/VNDB/MAL/Bilibili/赞助/书签字段
- `src/config/index.ts` 中对应导出
- `src/types/config.ts`、`src/types/bangumi.ts`、`src/types/vndb.ts`、`src/types/bilibili.ts`
- `src/config/booknavConfig.ts`、`sponsorConfig.ts`、相关工具文件

注意：不得删除壁纸、侧栏、浮动控制、Live2D、Spine 和评论配置。

验收：`rg` 搜索旧业务只剩历史文档，不再出现在运行时代码或构建入口。

### P0-3 构建流程拆分

状态：未完成

当前 `package.json` 的 build 仍包含多个旧主题资源生成步骤。需要：

1. 保留核心页面实际使用的 LQIP、字体、Pagefind 和必要图标生成。
2. 移除无用的 VNDB、旧动态、旧页面专属生成器。
3. 每个生成步骤单独可执行，失败时输出明确阶段名称。
4. 解决构建阶段长时间无输出的问题，必要时增加进度日志和超时保护。

验收：完整 `pnpm build` 在可接受时间内结束，失败时能定位具体步骤。

### P0-4 生产部署验证

状态：未完成

当前 Astro 为 `output: server`，必须确认 Cloudflare Worker/Pages 或 Node adapter 的真实部署方式。

验证地址：

- `/`
- `/posts/`
- `/posts/{id}/`
- `/thoughts/`
- `/gallery/`
- `/api/allPostMeta.json`
- `/api/health.json`

验收：SSR 动态页面、API 路由、后端代理和静态资源在目标域名均可访问。

## P1：核心体验完善

### P1-1 全局认证状态

状态：部分完成

- 将 `AuthBar` 的局部状态抽为可复用 auth store。
- 处理 SSR 与客户端 hydration 差异。
- 增加登录过期事件和统一跳转。
- 增加注册成功后的明确反馈页。

### P1-2 文章和杂谈交互

状态：部分完成

- 文章详情补充目录悬浮定位和移动端目录。
- 评论支持分页加载、邮箱/网站回填和提交后定位。
- 点赞状态刷新后从服务端恢复，而不是只保留本地瞬时状态。
- 文章列表补充筛选条件可见性和清除筛选按钮。

### P1-3 相册交互

状态：部分完成

- 增加 Lightbox，而不是仅跳转到照片详情页。
- 支持键盘左右切换、Esc 关闭、移动端底部 EXIF 面板。
- 图片加载失败和原图超时提供替代操作。

### P1-4 统一视觉系统

状态：未完成

- 统一文章卡片、杂谈卡片、相册卡片、错误状态、空状态和分页控件。
- 清理旧 Firefly 页面遗留的中英文乱码文案。
- 统一日期、数字、图片 URL 和外链处理工具。

## P2：质量与清理

### P2-1 删除无引用组件和资源

状态：未开始

只有在 P0-2 完成后执行。候选目录包括：

- `src/components/pages/bangumi`
- `src/components/pages/vndb`
- `src/components/pages/mal`
- 旧动态组件目录
- 旧书签、赞助、Bilibili 专属组件
- 对应图片、LQIP、字体和图标资源

删除前必须用 `rg` 和 TypeScript 检查确认无引用。

### P2-2 测试和可观测性

状态：未开始

- API client 单元测试：成功、业务失败、401 刷新、超时、重试。
- 页面 E2E：文章浏览、评论、点赞、友链申请、登录注册。
- 控制台错误检查：无未处理 Promise、无旧 API 请求、无跨域失败。
- 生产错误日志包含请求路径和状态，不记录 token 或用户密码。

### P2-3 响应式验收

状态：未开始

至少验证：390px、768px、1440px。

- 无横向滚动
- 标题和按钮不溢出
- 壁纸、侧栏、浮动控制、Live2D、Spine 不遮挡正文
- 评论、登录、注册和友链表单可操作
- 图片懒加载不导致首屏空白

## 推荐后续执行顺序

1. P0-1 应用壳依赖收敛
2. P0-2 配置和类型清理
3. P0-3 构建流程拆分
4. P0-4 生产部署验证
5. P1-1 全局认证状态
6. P1-2 文章/杂谈交互完善
7. P1-3 相册 Lightbox
8. P1-4 统一视觉系统
9. P2-1 删除无引用组件和资源
10. P2-2/P2-3 测试与响应式验收

## 每一步的完成门槛

每个任务完成后必须：

1. 更新本文件状态。
2. 记录实际修改文件。
3. 运行 `node_modules/.bin/tsc --noEmit --pretty false`。
4. 运行 `node_modules/.bin/astro check`。
5. 涉及构建或部署时，额外运行 `pnpm build` 和目标环境冒烟测试。

