# frontend-firefly 动态博客前端重构实施文档

> 执行状态更新：2026-09-03。状态标记：`[x]` 已完成，`[~]` 进行中，`[ ]` 待处理。

## 执行状态总表

| 任务 | 状态 | 代码/说明 |
|---|---|---|
| 1. 重构目标与边界 | [x] | `frontend-firefly/REBUILD_SCOPE.md` 已建立，唯一后端来源、保留/删除范围和导航契约已固定 |
| 2. 应用壳与主导航 | [~] | 主导航和全捕获路由已清理；Live2D/Spine 特效按需求保留，旧侧栏、壁纸和布局配置仍待替换 |
| 3. 统一 API 层 | [x] | 文章、杂谈、归档、相册、友链、站点、评论、认证 API 已统一经过 `src/lib/api` |
| 4. 文章主链路 | [x] | 首页、文章列表、分页筛选、文章详情、Markdown、TOC 数据、评论、点赞已接入 |
| 5. 杂谈模块 | [x] | `/thoughts/`、`/thoughts/[id]/`、Markdown、评论、点赞已接入 |
| 6. 归档与搜索 | [x] | 时间线、分类、标签、搜索分页和筛选链接已接入，分类/标签使用 `categoryId/tagId` |
| 7. 相册与照片 | [x] | 相册列表、详情、照片详情和 EXIF 已接入 |
| 8. 友链、关于和站点组件 | [x] | 友链、申请表单、关于、公告、一言、天气、日程已接入 |
| 9. 认证与互动收口 | [x] | 登录、注册、认证状态、退出、修改密码、评论和点赞已接入 |
| 10. 删除旧实现 | [~] | 旧业务路由、动态兼容 API 和全局旧特效注入已删除；旧配置、旧组件、i18n 和资源生成器仍待清理 |
| 11. 发布验收 | [~] | TypeScript/Astro 检查通过；完整生产构建和 Playwright 视觉验收待完成 |

## 已完成文件范围

- `src/config/navBarConfig.ts`：主导航已改为首页、文章、杂谈、归档、相册、友链、关于。
- `src/pages/index.astro`、`posts/index.astro`、`thoughts/*`、`archive.astro`、`gallery/*`、`friends.astro`、`search.astro`、`login.astro`、`register.astro`、`settings/password.astro`：核心路由已建立。
- `src/components/pages/ArticleFeed.svelte`：文章分页、筛选、错误和重试已完成。
- `src/components/pages/SiteOverview.astro`：站点公共信息组件已完成。
- `src/components/interaction/LikeButton.svelte`、`src/components/comment/BackendComments.svelte`：互动闭环已完成。
- `src/components/auth/*`：登录、注册、认证状态和修改密码表单已完成。
- 旧 Bangumi、VNDB、MAL、书签、赞助、Bilibili、留言板、系列和动态路由已删除。

## 当前未完成事项

- 清理 `src/config/index.ts` 及其余旧功能配置、类型、i18n 文案和无引用组件。
- 从 `package.json` 和构建流程中移除不再使用的生成器，拆分可能阻塞的资源生成步骤。
- 保留旧 Firefly `MainGridLayout` 中的壁纸、侧栏、浮动控制、Live2D 和 Spine；仅移除与新博客无关的业务组件和播放器依赖。
- 完成完整 `pnpm build`，确认 SSR、动态路由和 `/api/*` 在目标部署平台可用。
- 使用 Playwright 完成桌面、平板、移动端冒烟和视觉验收。

## 1. 重构目标与边界

本次重构以 `blog-parent/blog-web/blog-web-app` 的前台展示接口为唯一业务事实来源，重新建设 `frontend-firefly`。目标是得到一个内容优先、可维护、可逐步交付的动态博客前端，而不是继续在现有 Firefly 主题上叠加页面。

重构原则：

1. 后端已有接口优先；没有后端契约的功能不进入主导航。
2. 页面、组件、数据请求和状态处理按业务模块拆分。
3. 所有接口统一经过一个 API 客户端，统一处理 `R<T>`、分页、错误、超时和认证刷新。
4. 先完成匿名浏览主链路，再接入评论、点赞和登录等交互能力。
5. 删除冗余页面前先完成路由替换和构建验证；删除动作分批提交，保证可回滚。

## 2. 后端能力清单

后端应用模块位于 `blog-parent/blog-web/blog-web-app`，统一前缀为 `/xuyuan`，成功响应结构为 `{ code: 200, message, data }`。

| 业务模块 | 接口 | 前端用途 | 认证 |
|---|---|---|---|
| 文章 | `GET /articles?page&pageSize&type&categoryId&tagId&keyword` | 首页、文章列表、筛选、搜索 | 否 |
| 文章详情 | `GET /articles/{id}` | Markdown 正文、目录、上下篇 | 否 |
| 热门文章 | `GET /articles/hot?limit` | 首页热门/推荐 | 否 |
| 杂谈 | `GET /thoughts?page&pageSize`、`GET /thoughts/{id}` | 杂谈列表和详情，后端限定 `type=2` | 否 |
| 归档 | `GET /categories`、`GET /tags`、`GET /archives/timeline` | 分类、标签、时间线归档 | 否 |
| 搜索 | `GET /search?keyword&page&pageSize` | 全站文章搜索 | 否 |
| 相册 | `GET /galleries?page&pageSize`、`GET /galleries/{id}` | 相册列表、相册详情 | 否 |
| 照片 | `GET /photos/{id}` | 照片详情和 EXIF | 否 |
| 友链 | `GET /friends?groupName`、`GET /friends/random` | 友链列表、随机推荐 | 否 |
| 友链申请 | `POST /friends/apply` | 友链申请表单 | 否（需限流） |
| 关于 | `GET /about` | 个人介绍、技能、里程碑 | 否 |
| 站点信息 | `GET /site/notice`、`/site/words/random`、`/site/weather`、`/site/schedules` | 公告、一言、天气、日程 | 否 |
| 评论 | `GET /comments?targetType&targetId&page&pageSize`、`POST /comments` | 文章/杂谈评论 | 创建评论按后端规则 |
| 点赞 | `POST /likes/toggle` | 文章/杂谈点赞 | 按用户/IP幂等切换 |
| 前台认证 | `GET /auth/captcha`、`POST /auth/register`、`/auth/login`、`/auth/refresh`、`GET /auth/userinfo`、`POST /auth/logout`、`PUT /auth/password` | 登录、注册、个人信息、改密 | 部分接口需要 |

### 2.1 统一数据约定

- 分页请求使用 `page`（从 1 开始）和 `pageSize`（建议默认 10，前端最多 50）。
- 分页响应使用 `records/total/page/pageSize/pages`。
- 文章 `type=1` 为技术文章，`type=2` 为杂谈；普通首页只请求 `type=1`。
- 文章发布条件由后端保证：`deleted=0` 且 `status=1`。
- 时间统一按后端返回的 `yyyy-MM-dd HH:mm:ss` 解析，显示层再格式化。
- 图片 URL 为空时使用统一占位图，不允许把空字符串直接传给 `<img>`。

## 3. 新的信息架构与导航

### 3.1 主导航

仅保留后端有明确业务支撑的入口：

1. 首页 `/`
2. 文章 `/posts/`
3. 杂谈 `/thoughts/`
4. 归档 `/archive/`
5. 相册 `/gallery/`
6. 友链 `/friends/`
7. 关于 `/about/`
8. 搜索按钮（跳转 `/search/`）

登录、注册不放入主导航，放在用户菜单或评论区的登录提示中。公告、一言、天气、日程作为首页或侧栏小组件，不单独占用导航位置。

### 3.2 明确移除的旧入口

下列页面没有对应的前台核心后端模块，重构后删除路由、导航项、专属组件和配置：

- `/bangumi/`
- `/vndb/`
- `/myanimelist/`
- `/booknav/`
- `/sponsor/`
- `/series/`
- 旧 RSS 页面（除非后续明确要求 SEO 订阅，再单独实现）
- 旧 Firefly 示例内容、示例文章、与业务无关的实验性小工具

`/dynamic/` 不直接保留旧实现；它的业务由后端 `moments` 提供，重构为 `/thoughts/`。旧动态组件、旧动态评论页和本地 Markdown 动态数据全部迁移完成后删除。

## 4. 目标目录结构

```text
frontend-firefly/src/
  pages/
    index.astro
    posts/index.astro
    posts/[id].astro
    thoughts/index.astro
    thoughts/[id].astro
    archive/index.astro
    archive/categories.astro
    archive/tags.astro
    gallery/index.astro
    gallery/[id].astro
    gallery/photo/[id].astro
    friends.astro
    about.astro
    search.astro
    login.astro
    register.astro
    settings/password.astro
    api/health.json.ts
  components/
    app/AppShell.astro
    app/AppNavbar.astro
    app/AppFooter.astro
    app/LoadingState.astro
    app/ErrorState.astro
    app/EmptyState.astro
    article/ArticleCard.astro
    article/ArticleList.astro
    article/ArticleDetail.astro
    article/ArticleMeta.astro
    article/ArticleToc.astro
    thought/ThoughtCard.astro
    gallery/GalleryCard.astro
    gallery/PhotoLightbox.svelte
    interaction/CommentList.svelte
    interaction/CommentForm.svelte
    interaction/LikeButton.svelte
    auth/LoginForm.svelte
    auth/RegisterForm.svelte
    widget/Notice.astro
    widget/RandomWord.astro
    widget/Weather.astro
    widget/Schedule.astro
  lib/api/
    client.ts
    types.ts
    articles.ts
    thoughts.ts
    archive.ts
    gallery.ts
    friends.ts
    about.ts
    site.ts
    comments.ts
    likes.ts
    auth.ts
  stores/
    auth.ts
    ui.ts
  utils/
    date-utils.ts
    image-utils.ts
    error-utils.ts
```

## 5. 分阶段实施步骤

### 阶段 0：冻结现状并建立基线

1. 记录当前 `pnpm check`、`pnpm type-check`、`pnpm build` 结果。
2. 保存当前生产配置和 API 地址，不把密钥写入仓库。
3. 新建 `rebuild` 分支或等价的可回滚提交点。
4. 确认后端启动在 8081，数据库和 Redis 可用，至少存在一篇 `status=1` 文章。

验收：基线命令结果可复现，首页和 `/api/allPostMeta.json` 可访问。

### 阶段 1：清理路由与应用壳

1. 重写 `AppShell`、导航栏、页脚和全局错误边界。
2. 导航只保留首页、文章、杂谈、归档、相册、友链、关于、搜索。
3. 暂时将旧页面改为 404 或重定向，确认新路由不再依赖旧 Firefly 页面。
4. 移除旧主题专属导航配置和未使用的页面开关。

验收：导航无冗余入口；任意页面刷新不会请求旧页面数据。

### 阶段 2：统一 API 层

1. `client.ts` 只负责请求、解析 `R<T>`、超时、重试和错误分类。
2. 将 API 基地址统一为 `PUBLIC_API_BASE_URL`，通过 `apiPath()` 拼接 `/xuyuan`。
3. 浏览接口默认不携带无效 token；401 只在存在 refreshToken 时尝试刷新。
4. 为每个后端模块建立独立 API 文件和 TypeScript 类型。
5. 所有页面统一处理 loading/error/empty 三种状态。
6. 日历、搜索等旧兼容接口改为复用同一 API 层，禁止组件自行拼接后端地址。

验收：浏览器 Network 中所有请求路径可追踪；接口失败不产生未处理 Promise rejection。

### 阶段 3：文章主链路

1. 首页请求 `GET /articles?type=1&page=1&pageSize=10`，展示置顶和最新文章。
2. 文章列表支持分页、关键词搜索入口、分类和标签筛选。
3. 文章详情请求 `/articles/{id}`，渲染 Markdown、代码高亮、目录、上下篇。
4. 详情页加入评论列表、评论表单和点赞按钮。
5. 文章卡片统一处理空封面、摘要、日期和统计数字。

验收：从首页点击文章可进入详情；详情刷新、错误 ID、接口超时均有明确界面。

### 阶段 4：杂谈模块

1. 列表调用 `/thoughts`，详情调用 `/thoughts/{id}`。
2. 禁止前端把文章接口伪装成杂谈接口；类型过滤由后端保证。
3. 杂谈正文与文章共用 Markdown、评论、点赞基础组件。

验收：文章和杂谈 URL、标题、正文和评论目标不会串数据。

### 阶段 5：归档与搜索

1. `/archive/` 展示时间线；分类和标签使用 `/categories`、`/tags`。
2. 分类点击回到文章列表并带 `categoryId`，标签点击带 `tagId`。
3. `/search/` 使用 `/search`，支持关键词、分页、空结果和失败重试。
4. 日历只作为归档辅助视图，不再自己请求后端或依赖本地文章索引。

验收：分类、标签、年份、月份和关键词筛选结果与后端记录一致。

### 阶段 6：相册与照片

1. 相册首页调用 `/galleries`，相册详情调用 `/galleries/{id}`。
2. 照片详情按需调用 `/photos/{id}` 获取 EXIF，不能在列表一次请求全部 EXIF。
3. Lightbox 支持键盘左右切换、Esc 关闭、移动端底部信息面板和图片加载失败。

验收：相册、照片、EXIF 和返回导航在桌面与移动端可用。

### 阶段 7：友链、关于和站点小组件

1. 友链列表按 `groupName` 分组，外链统一 `noopener noreferrer`。
2. 友链申请表单做前端格式校验、提交 loading、防重复提交和后端错误展示。
3. 关于页渲染 `AboutVO` 的简介、技能和里程碑，长文本不溢出。
4. 公告、一言、天气、日程作为可失败的小组件，单个失败不能阻断首页。

验收：任一小组件接口失败时首页主体仍可浏览。

### 阶段 8：认证与互动收口

1. 登录、注册、验证码、刷新 token、退出和改密全部接入 `/auth/*`。
2. token 仅存储在浏览器受控存储中，不出现在 URL、日志和页面正文。
3. 评论表单支持匿名昵称、邮箱、网站字段约束，失败保留输入。
4. 点赞按钮做防抖和乐观更新失败回滚，目标类型严格区分文章/杂谈。

验收：登录过期、刷新失败、评论失败、点赞重复点击均有稳定行为。

### 阶段 9：删除旧实现与发布

1. 删除已迁移的旧页面、旧组件、旧配置和本地示例数据。
2. 搜索 `rg` 确认不存在旧路由和无引用组件。
3. 清理 `dist` 后执行完整构建，避免旧静态产物掩盖问题。
4. 使用 Playwright 检查桌面 1440px、平板 768px、移动 390px。
5. 发布前保留上一版本产物和 API 配置，出现问题可快速回滚。

## 6. 删除清单

### 立即删除或隔离

- `src/pages/bangumi.astro`
- `src/pages/vndb.astro`
- `src/pages/myanimelist.astro`
- `src/pages/booknav.astro`
- `src/pages/sponsor.astro`
- `src/pages/series/index.astro`
- 对应 `components/pages/bangumi`、`vndb`、`mal` 目录
- 旧动态实现（迁移到 thoughts 后删除）
- 示例文章和与新业务无关的 Firefly 演示组件

### 暂缓删除

- `src/pages/rss*`：确认是否需要订阅/SEO 后再决定。
- 通用 Markdown、图片、分页、错误状态、评论、点赞、认证基础组件：迁移完成前保留。
- 图片、字体和主题基础资产：由新壳层实际引用后再清理。

## 7. 验收矩阵

| 场景 | 预期 |
|---|---|
| 后端不可用 | 页面显示错误状态和重试，不出现未处理 Promise |
| 空文章列表 | 显示空状态，不渲染空链接或空图片 |
| 文章详情不存在 | 显示 404 业务提示，可返回文章列表 |
| 文章封面为空 | 使用统一默认封面 |
| 评论提交失败 | 保留输入，展示后端 message |
| token 过期 | 尝试刷新，刷新失败清理凭证并跳转登录 |
| 移动端 390px | 无横向滚动，标题、按钮、图片和表单不溢出 |
| 生产构建 | `pnpm check`、`pnpm type-check`、`pnpm build` 全部通过 |

## 8. 推荐提交顺序

1. `chore: add frontend rebuild baseline and API contract`
2. `refactor: replace firefly shell and navigation`
3. `feat: implement article list and detail flow`
4. `feat: implement thoughts archive and search`
5. `feat: implement gallery friends and about`
6. `feat: add comments likes and app auth`
7. `chore: remove legacy firefly pages and assets`
8. `test: add frontend integration and visual smoke checks`

每个提交都应可独立构建；删除旧页面的提交必须放在新页面验收之后。

## 9. 需要补充的接口细节（开发前必须锁定）

### 9.1 评论、点赞和友链请求体

前端类型必须与后端 DTO 一致，禁止使用宽泛的 `Record<string, unknown>`：

```ts
type CommentCreateParam = {
  targetType: 1 | 2; // 1 文章，2 杂谈
  targetId: number;
  content: string;
  nickname: string;
  email?: string;
  website?: string;
};

type LikeToggleParam = { targetType: 1 | 2; targetId: number };

type FriendApplyParam = {
  name: string;
  url: string;
  avatar?: string;
  description?: string;
  email: string;
  group?: string;
};
```

评论和点赞必须在页面数据中明确传入 `targetType`，不能通过 URL 字符串猜测。友链申请的 `url`、`avatar`、`email` 需在浏览器端先校验，最终以服务端校验结果为准。

### 9.2 前台认证与后台认证不能混用

`blog-web-app` 的 `AppAuthController` 同时暴露 `/xuyuan/auth/*` 和 `/xuyuan/app/auth/*`。新前端统一使用 `/xuyuan/auth/*`，不要调用 `/xuyuan/admin/*`，也不要复用后台登录表单。前台登录和注册字段如下：

- 登录：`username`、`password`，前台登录不要求图形验证码。
- 注册：`username` 3-32 字符、`password` 6-32 字符、`nickname` 必填且不超过 64 字符、`email` 可选且不超过 128 字符。
- 修改密码：`oldPassword`、`newPassword`，新密码 6-32 字符。

AccessToken 有效期约 30 分钟，RefreshToken 有效期约 7 天。刷新失败时必须清理两个 token 并回到登录页。认证状态应在 SSR 与客户端 hydration 之间保持一致，禁止在服务端读取 `localStorage`。

### 9.3 文章和详情字段

文章列表项至少包含 `id/title/cover/summary/categoryId/categoryName/tags/viewCount/commentCount/likeCount/type/isTop/publishedAt`；详情在此基础上增加 `content/toc/prevArticle/nextArticle/updatedAt`。`toc` 只包含 H2/H3，前端应优先使用后端 `anchor`，不要自行重新生成锚点导致目录跳转不一致。

### 9.4 响应与 HTTP 行为

后端业务失败可能仍返回 HTTP 200，前端必须先判断 `payload.code`，不能只判断 `response.ok`。常见处理：

- `200`：返回 `data`。
- `400/202`：参数错误，绑定到表单字段或筛选器。
- `404/204`：资源不存在或数据异常，显示空/不存在状态。
- `401/601/602/603`：尝试刷新 token；刷新失败后退出登录。
- `409`：友链重复申请等冲突，直接展示后端 message。
- `5xx` 或网络超时：显示可重试错误状态。

## 10. 部署与同源策略

当前 Astro 配置为 `output: "server"`，但 `wrangler.jsonc` 只声明了 `assets.directory`。如果部署到 Cloudflare Pages/Workers，必须二选一：

1. 使用 Astro Cloudflare adapter，并配置 Functions/Worker，使 `src/pages/api/*.ts` 真正被执行；或
2. 使用 Node adapter 部署完整 SSR 服务，并通过反向代理把 `/api/*` 和页面统一到同一域名。

不能把仅包含静态 `dist` 的产物当成完整 SSR 应用发布，否则 `/api/allPostMeta.json`、动态详情和服务端 API 代理会在生产环境失效。生产环境建议使用同源 `/api/*` 代理后端，浏览器不直接访问 `PUBLIC_API_BASE_URL`，从根源上减少 CORS、预检和内网地址泄露问题。

发布前必须验证：

```text
GET /api/health.json
GET /api/allPostMeta.json
GET /posts/{existing-id}/
GET /posts/not-found/
```

## 11. 数据、缓存与 SEO 规则

1. 文章和杂谈详情默认 SSR，列表可按分页参数缓存 30-60 秒；评论、点赞和认证请求禁止静态缓存。
2. API 客户端应为 GET 请求设置 AbortController 超时，并在 UI 层提供显式重试按钮。
3. 页面 `<title>`、description、canonical、Open Graph 和 JSON-LD 由文章/杂谈详情数据生成；不存在的文章返回 404 状态而不是带 200 的错误页面。
4. Markdown 必须经过安全渲染，允许的 HTML 标签和外链协议需白名单过滤；评论内容一律文本渲染，禁止 `set:html`。
5. 图片 URL 统一通过 `resolveImageUrl()` 处理空值、协议和懒加载；首屏主图使用 eager，其余图片使用 lazy。

## 12. 接口契约补充与构建清理

实现前还需锁定以下细节：

- `/search` 接收 `keyword`，不是 `q`；展示层可使用 `q`，调用 API 前必须映射。
- `/galleries` 接收 `page/pageSize/keyword`；`/archives/timeline` 返回 `year -> months[] -> articles[]`，月份格式为 `yyyy-MM`。
- `/galleries/{id}` 已返回 `photos[]`，每项包含 `id/galleryId/original/thumbnail/shootTime/exif`；`/photos/{id}` 用于单张照片深链接或刷新，不应在相册列表批量调用。
- `/friends` 查询字段是 `groupName`，友链申请 DTO 字段是 `group`，不可混用。
- `/site/schedules` 返回数组；公告、一言、天气可能为空，组件必须支持空对象/空数组。

删除页面后还要同步检查 `package.json` 的构建脚本：移除不再使用的 VNDB、GitHub 卡片、Pio、LQIP 和本地动态内容生成器，避免删除页面后构建仍依赖旧功能。迁移完成后再删除 `/api/allPostMeta.json` 兼容端点。

## 13. 联调最小样例

至少保存以下真实请求/响应，作为 TypeScript 类型和 E2E fixture 的来源：

```text
GET  /xuyuan/articles?page=1&pageSize=10&type=1
GET  /xuyuan/articles/{id}
GET  /xuyuan/thoughts?page=1&pageSize=10
GET  /xuyuan/archives/timeline
GET  /xuyuan/galleries?page=1&pageSize=12
GET  /xuyuan/galleries/{id}
GET  /xuyuan/comments?targetType=1&targetId={id}&page=1&pageSize=20
POST /xuyuan/comments
POST /xuyuan/likes/toggle
```

每个样例记录 HTTP 状态、`code`、空数据、错误数据和字段类型；不要只根据 Swagger 截图手写前端类型。

## 14. 重构完成定义（Definition of Done）

只有同时满足以下条件，才可以删除旧 Firefly 页面：

- 新导航中的 7 个业务入口均可直接刷新访问。
- 文章、杂谈、归档、搜索、相册、友链、关于接口均有 loading/error/empty 状态。
- 评论、点赞、登录、注册和刷新 token 已完成真实后端联调。
- `rg` 搜索不到已删除路由、旧组件引用和旧数据源。
- `pnpm check`、`pnpm type-check`、`pnpm build` 通过。
- 桌面、平板、移动端冒烟测试通过，且没有未处理 Promise、横向溢出或控制台网络错误。
- 生产部署已验证 SSR 页面和 `/api/*` 路由，而不仅是本地 `dist` 文件存在。
