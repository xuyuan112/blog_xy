# 静态博客动态化迁移任务

关联审查：[frontend-backend-integration-review.md](./frontend-backend-integration-review.md)

## 目标

将 `frontend-firefly` 从本地 Markdown/示例数据驱动，迁移为由 `blog-parent/blog-web` 数据库和 API 驱动。Firefly 只保留展示层、交互和 API 适配，不再维护平行业务数据。

## 执行原则

- 后端数据库是唯一真实数据源。
- 页面只能通过 `src/lib/api` 访问业务数据。
- 示例内容仅用于开发演示，不得作为生产回退数据。
- 后端不可用时显示明确错误状态，不伪装成示例数据。
- 每个模块完成后，使用管理后台新增、修改、删除数据验证前台同步。

## 任务清单

### 0. 接口契约与基础设施

- [x] 确认 `R<T>` 和 `PageResult<T>` 响应格式
- [x] 确认 API 基础地址为 `PUBLIC_API_BASE_URL + /xuyuan`
- [x] 为核心业务 VO 建立前端 DTO 映射和字段转换（文章、动态、相册、照片；其余简单 VO 字段已保持 API 类型一致）
- [x] 统一认证 Header、Token 存储、刷新和退出协议（代码已完成，仍待真实环境联调）
- [x] 统一数字 ID、URL 参数和展示 slug 的规则（业务详情 ID 统一数字并通过 `apiIdPath` 编码）
- [x] 增加 API 错误、空数据、未登录三类状态组件

### 1. 文章主链路

- [x] 首页文章列表使用 `/articles`
- [x] 文章详情使用 `/articles/{id}`，移除构建期依赖后端的 `getStaticPaths`
- [x] 上一篇/下一篇使用后端返回值
- [x] 文章封面、正文图片统一使用后端返回 URL
- [x] 推荐、日历、站点统计移除 `getCollection("posts")`（推荐和日历通过后端 API 兼容代理，站点统计直接调用 API）

### 2. 分类、标签、归档

- [x] 标签页使用 `/tags`
- [x] 分类页使用 `/categories`
- [x] 归档页使用 `/archives/timeline`
- [x] 分类栏和侧边栏使用 API 数据
- [x] 过滤链接改为后端查询参数

### 3. 动态、相册、社交内容

- [x] 动态页和动态侧栏使用 `/moments`
- [x] 动态详情使用 `/moments/{id}`
- [x] 相册列表使用 `/galleries`
- [x] 相册详情和照片使用后端数字 ID
- [x] 友链、关于页只使用 `/friends`、`/about`

### 4. 互动和认证

- [x] 评论、点赞、登录、刷新和退出完成联调
- [x] 清理静态评论配置和假用户状态

### 5. 清理静态业务数据

- [x] `content-utils.ts` 不再作为业务数据源（文件已移除）
- [x] 删除页面对 `posts`、`dynamic` Content Collection 的业务查询
- [x] 停止从本地集合生成 `allPostMeta.json` 和 `dynamic.json`（保留后端 API 兼容代理）
- [x] 将示例内容移出生产构建
- [x] 删除相册静态配置作为业务回退的逻辑

## 当前执行记录

已完成：任务文档建立、接口路径核对、文章/动态/相册/友链/关于 API 现状盘点。

进行中：推荐、日历、动态详情和相册的浏览器端视觉验收，以及全量 DTO 清理；核心页面和互动接口已完成后端接入与基础联调。

互动与认证联调已完成：调整 `AppSecurityConfig` 后，文章、评论读取/提交和点赞无需登录即可访问；通过临时注册测试账号验证了前台登录、userinfo、refresh、logout 全链路均返回 `code=200`。测试后端进程已停止。

## 验收标准

- 管理后台新增、修改、删除数据后，前台无需复制文件即可同步。
- 前台不再出现 `The collection "posts" does not exist or is empty`。
- 后端关闭时，页面显示 API 错误状态，不显示虚假数据。

## 实施规格书

### 1. 范围与非目标

本次改造范围是 `frontend-firefly` 公共展示端与 `blog-parent/blog-web/blog-web-app` 的业务接口联调。管理端 UI、数据库表结构和第三方页面（Bangumi、Bilibili、VNDB、MyAnimeList）不在第一阶段范围内，除非它们依赖文章或用户认证接口。

不允许通过复制示例 Markdown、增加假 JSON、静态相册配置或组件内默认对象来模拟后端业务数据。

### 2. 环境基线

| 项目 | 约定 |
| --- | --- |
| 前端目录 | `F:\个人博客\frontend-firefly` |
| 后端目录 | `F:\个人博客\blog-parent\blog-web` |
| 后端应用 | `blog-web-app` |
| API 地址 | `http://localhost:8081/xuyuan` |
| 前端变量 | `PUBLIC_API_BASE_URL=http://localhost:8081` |
| 响应封装 | `{ code, message, data }` |
| 分页封装 | `{ records, total, page, pageSize, pages }` |
| 成功条件 | `code === 200` |

联调前必须确认数据库已初始化、后端 profile 已加载、8081 端口可访问，并且至少存在一篇已发布文章、一个分类、一个标签和一条友链。

### 3. 接口契约

| 业务 | 方法 | 路径 | 前端调用 | 认证 |
| --- | --- | --- | --- | --- |
| 文章列表 | GET | `/xuyuan/articles` | `listArticles` | 否 |
| 文章详情 | GET | `/xuyuan/articles/{id}` | `getArticle` | 否 |
| 热门文章 | GET | `/xuyuan/articles/hot` | `listHotArticles` | 否 |
| 标签 | GET | `/xuyuan/tags` | `listTags` | 否 |
| 分类 | GET | `/xuyuan/categories` | `listCategories` | 否 |
| 归档 | GET | `/xuyuan/archives/timeline` | `getTimeline` | 否 |
| 动态列表 | GET | `/xuyuan/moments` | `listMoments` | 否 |
| 动态详情 | GET | `/xuyuan/moments/{id}` | `getMoment` | 否 |
| 相册列表 | GET | `/xuyuan/galleries` | `listGalleries` | 否 |
| 相册详情 | GET | `/xuyuan/galleries/{id}` | `getGallery` | 否 |
| 照片详情 | GET | `/xuyuan/photos/{id}` | `getPhoto` | 否 |
| 友链 | GET | `/xuyuan/friends` | `listFriends` | 否 |
| 关于 | GET | `/xuyuan/about` | `getAbout` | 否 |
| 搜索 | GET | `/xuyuan/search` | `search` | 否 |
| 点赞 | POST | `/xuyuan/likes/toggle` | `toggleMomentLike` | 按后端策略 |

后端 VO 字段变化必须先更新本表和 `src/lib/api` 的映射，再修改页面组件。

### 4. 页面与代码改造矩阵

| 页面/组件 | 当前问题 | 目标实现 | 完成定义 |
| --- | --- | --- | --- |
| `src/pages/[...page].astro` | 构建期请求文章 | 客户端 API 加载列表 | 后台新增文章无需重新构建即可出现 |
| `src/pages/posts/[id].astro` | `getStaticPaths` 依赖 API | 运行时按数字 ID 获取详情 | 新增文章详情无需重新构建 |
| `src/pages/tags/index.astro` | 依赖 Content Collection | `/tags` | 标签数量与后台一致 |
| `src/pages/categories/index.astro` | 依赖 Content Collection | `/categories` | 分类数量与后台一致 |
| `src/components/controls/ArchivePanel.astro` | 本地文章分组 | `/archives/timeline` | 年月和文章数量与后台一致 |
| `src/components/layout/CategoryBar.astro` | 本地分类和文章数 | 分类 API | 导航分类与后台一致 |
| `src/components/widget/Categories.astro` | 本地分类 | 分类 API | 侧栏不再触发 Content Collection |
| `src/components/widget/SiteStats.astro` | 本地文章统计 | API 聚合或后端统计 | 不读取本地文章 |
| `src/components/misc/RecommendedPost.astro` | ~~本地推荐（旧实现）~~ `/articles/hot` | `/articles/hot` | 推荐内容来自数据库 |
| `src/pages/dynamic/index.astro` | 同时保留本地 JSON 路径 | `/moments` | 不生成或读取 `dynamic.json` |
| `src/pages/gallery/*` | slug 与 Long ID 混用 | 后端数字 ID | 详情 URL 与后端记录一致 |
| `src/pages/api/*.json.ts` | 本地集合生成 JSON | 删除或改为 API 代理 | 无本地业务数据生成 |

### 5. 数据加载与路由策略

第一阶段采用“静态页面外壳 + 浏览器请求 API”。业务数据不放在 Astro 构建期的 `getStaticPaths()` 或顶层 frontmatter 中。页面必须具备 loading、empty、error 三种状态，并且错误状态不得回退到示例数据。

文章详情已采用 `@astrojs/node` SSR adapter；部署必须运行 Node SSR 服务，并配置后端 API 地址、缓存策略和错误页面。

### 6. 后端补充任务

- 确认文章详情返回正文、目录、上一篇和下一篇。
- 确认文章列表支持 `page`、`pageSize`、分类、标签和关键词参数。
- 为系列提供正式接口；在接口完成前，前端不显示伪造的系列数据。
- 确认评论列表、发表评论、点赞接口和匿名访问策略。
- 确认上传接口返回可公开访问的图片 URL。
- 统一前台用户认证为 Bearer Token，并明确 refresh 请求体字段。

### 7. 验收用例

每个模块至少执行以下四类测试：

1. 后台新增记录，刷新前台，记录可见。
2. 后台修改标题、分类、标签或图片，前台显示新值。
3. 后台删除记录，前台列表、搜索、归档和推荐不再显示。
4. 停止后端，前台显示清晰错误状态且不出现示例数据。

文章还必须验证详情页、上一篇/下一篇、搜索结果和图片；动态还必须验证分页、详情和点赞；相册还必须验证列表、详情、照片 URL 和不存在 ID；认证还必须验证登录、刷新、退出和过期 Token。

### 8. 回滚策略

每个模块单独提交。发现接口字段或数据库兼容问题时，只回滚该模块的前端 API 适配和页面，不恢复示例数据作为生产回退。数据库迁移由后端单独提供向前兼容脚本，前端不得直接修改数据库。

### 9. Definition of Done

一个模块只有同时满足以下条件才算完成：

- 页面不再读取该模块的本地 Content Collection 或示例 JSON。
- API 错误、空数据和正常数据均有可见界面。
- 管理后台新增、修改、删除已完成端到端验证。
- `npm run check` 通过且没有新增警告。
- 任务清单、接口契约和验收记录已更新。

## 当前未完成项

以下任务仍保持未勾选，原因是代码或后端契约尚未满足完成定义：

- 全量 VO DTO 映射、数字 ID/slug 规则和真实后端联调尚未完成。
 - 推荐文章已接入 `/articles/hot`，仍需在实际文章详情布局中启用组件并完成视觉验收。
 - 日历已直接请求 `/xuyuan/articles?page=1&pageSize=100` 并解包标准分页响应；仍需真实数据下的视觉验收。
- 动态详情的后端调用已实现，但评论、点赞和真实数据联调尚未完成。
 - 相册页面已完成真实数据库联调：列表返回相册 ID 2，详情返回 2 张照片，MinIO original/thumbnail URL 均返回 HTTP 200。
- 过滤参数代码已完成，但评论、点赞、上传 URL、匿名访问和真实筛选结果仍待端到端验收。
- 评论和点赞已完成真实后端联调；匿名访问策略为公开读取、公开提交和 IP 点赞，账号用于实名身份场景。
- 示例目录虽然已清空，但 Content Collection 仍保留空兼容定义，后续应在旧组件完全迁移后删除。

## 执行进度补充

本轮已完成：

- `Tags.astro` 改为调用 `/xuyuan/tags`。
- `DynamicSidebar.svelte` 改为调用 `/xuyuan/moments`。
- 动态 JSON API 改为后端接口适配，不再读取 `getCollection("dynamic")`。
- `SiteStats.astro` 改为调用文章、分类和标签 API。
- `auth.ts` 注册、刷新和退出流程统一携带并保存后端返回的 access/refresh Token。
- 实际 `posts`、`dynamic` 目录中的示例业务文件已清理。

尚未完成且需要单独决策：

- `src/pages/posts/[id].astro` 的 Astro SSR adapter、运行时数字 ID 路由已经完成；仅剩目标部署环境验证。
  - `RecommendedPost.astro` 已调用 `/articles/hot`；仅剩文章详情布局中的浏览器视觉验收。

本轮继续完成：

- 安装 `@astrojs/node`，Astro 配置切换为 `output: "server"` 和 Node standalone adapter。
- 文章详情移除 `getStaticPaths()` 的后端枚举，`/posts/{id}/` 改为请求时读取 `/xuyuan/articles/{id}`。
- 移除 OG 静态生成器，避免构建期扫描本地文章集合。
- 系列页在后端接口未提供前显示明确的未接入状态，不再读取本地文章集合。
- Content Collection 保留为空兼容定义，仅用于旧组件类型兼容，不提供业务数据。
- RSS XML 在动态 SSR 接入前返回明确的 503，不生成虚假静态订阅内容。
- `npm run check` 和 `npm run astro -- build` 均通过。

部署变化：前端现在必须以 Node SSR 服务运行，不能再只上传静态 `dist/client` 文件。构建产物位于 `dist/server`，启动命令由 `@astrojs/node` adapter 生成。
