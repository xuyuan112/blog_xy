# Firefly 前端与 blog-web 后端集成审查

> 状态更新：2026-09-03。本文保留初始审查结论作为设计依据；“当前数据源对照”和“已确认的风险”已按现行代码修订，未完成项以迁移任务文档为准。

## 结论

`frontend-firefly` 已按展示层定位接入 `blog-parent/blog-web`，核心业务数据通过 `src/lib/api` 访问后端；仍有旧配置清理、DTO 收口和生产验收工作。

早期版本的问题是数据来源不统一。当前核心页面已切换到后端 API，Content Collection 仅保留为空兼容定义；遗留配置和组件仍需按 backlog 清理。

目标应是：保留 Firefly 的视觉和交互层，逐步替换所有业务数据读取逻辑，使 `blog-web` 数据库成为唯一真实数据源。

## 当前数据源对照

| 模块 | 当前前端来源 | blog-web 接口 | 状态 |
| --- | --- | --- | --- |
| 文章列表 | `/articles` API | `/xuyuan/articles` | 已通过 API 运行时加载 |
| 文章详情 | `/articles/{id}` API | `/xuyuan/articles/{id}` | Node SSR 运行时加载，已移除 `getStaticPaths()` |
| 动态 | `/moments` API | `/xuyuan/moments` | 已接入后端 |
| 标签 | `/tags` API | `/xuyuan/tags` | 已接入后端 |
| 分类 | `/categories` API | `/xuyuan/categories` | 已接入后端 |
| 归档 | `/archives/timeline` API | `/xuyuan/archives/timeline` | 已接入后端 |
| 系列 | 本地文章集合 | 暂无明确接口 | 需要新增接口或重新定义功能 |
| 相册 | `/galleries` API | `/xuyuan/galleries` | 已接入后端，数字 ID 已统一 |
| 友链 | `/friends` API | `/xuyuan/friends` | 接口方向正确 |
| 关于 | `/about` API | `/xuyuan/about` | 接口方向正确 |
| 搜索 | `/search` API | `/xuyuan/search` | 接口方向正确，但字段需统一 |
| 留言板 | 评论组件 | 评论接口 | 已按评论接口处理 |
| 首页统计/推荐/日历 | 文章 API 适配层 | 文章 API | 已改为后端数据；推荐使用 `/articles/hot` |

## 已确认的风险

### 1. 示例数据会被误认为真实内容

`src/content/posts` 和 `src/content/dynamic` 原本只有 `.gitkeep`，示例内容位于 `posts.example` 和 `dynamic.example`。依赖 `getCollection()` 的页面因此为空，复制示例文件只能作为临时演示，不能作为正式数据方案。

正式环境不应依赖以下目录提供业务数据：

- `src/content/posts`
- `src/content/dynamic`
- `src/content/posts.example`
- `src/content/dynamic.example`

### 2. SSR 部署与动态后台

当前 Astro 已配置 `@astrojs/node` standalone adapter 并使用 `output: "server"`。文章详情页已移除 `getStaticPaths()`，请求时按数字 ID 读取后端，因此后台新增或修改文章无需重新构建即可生效。部署必须运行 Node SSR 服务，不能只发布静态 `dist/client`。

### 3. 本地内容工具已退出业务链路

标签、分类、归档、推荐和统计已改为 API 数据，当前源码不再通过 `getCollection("posts")` 查询业务内容。空的 Content Collection 仅为旧组件类型兼容保留，待旧组件清理后可删除。

### 4. 认证协议收口情况

前端 `src/lib/api/client.ts` 使用 `Authorization: Bearer`，并统一处理登录、刷新、退出和当前用户接口；后端兼容旧 Header 的部分仍属于部署兼容项。

### 5. ID 和字段模型没有统一

后端文章、相册、照片详情使用 Long ID；前端 API 类型大量使用 `string | number`，相册配置还使用 slug。文章、相册、照片应明确区分数字主键、URL 参数和展示 slug，不能在组件中隐式转换。

## 目标架构

```text
blog-admin-ui
        |
        v
blog-web Java API
        |
        v
数据库 / 文件存储
        ^
        |
frontend-firefly
        |
        +-- 只负责展示、交互和 API 调用
```

`blog-web` 数据库是唯一真实数据源。Firefly 只保留页面结构、主题、组件、交互和 API 适配，不再维护一套平行的文章或相册数据。

## 推荐迁移方案

### 阶段一：冻结并记录接口契约

建立后端接口与前端页面的映射表，明确：

- 请求路径、请求参数和认证要求
- `R<T>` 响应封装和分页字段
- 文章、动态、相册、友链、关于、评论的 VO 字段
- 错误码、空数据和未登录行为
- 图片上传、访问 URL 和资源权限

前端只允许通过 `src/lib/api` 访问后端，不在 Astro 组件里拼接 API URL。

### 阶段二：统一前端模型和 API 适配层

在 `src/lib/api` 中定义稳定的前端模型，例如 `ArticleCardModel`、`ArticleDetailModel`、`MomentModel` 和 `GalleryModel`。后端 VO 字段变化时，只修改适配层，不让页面直接依赖后端响应细节。

同时统一：

- `records / total / page / pageSize / pages` 分页结构
- `code / message / data` 错误结构
- 数字 ID 的处理规则
- `Authorization: Bearer <token>` 认证规则
- Token 的存储和刷新策略

### 阶段三：按页面替换本地读取

推荐顺序：

1. 首页文章列表和文章详情
2. 标签、分类、归档
3. 动态（改用 `/moments`）
4. 相册和照片详情
5. 搜索
6. 友链和关于
7. 评论、点赞、登录
8. 日历、推荐、统计等侧边栏组件

`content-utils.ts` 应保留纯格式化工具，删除基于 `getCollection("posts")` 的业务查询。标签、分类、归档和推荐全部改为 API 数据。

### 阶段四：选择数据加载方式

对于后台可变数据，列表和侧栏继续使用“页面外壳 + API 请求”；需要 SEO 的文章详情页已经使用 Astro SSR。生产环境应配置同源 `/api/*` 代理、缓存策略和错误页面。

不要在 `getStaticPaths()` 中要求后端返回完整文章列表，除非明确接受重新构建才能看到新内容。

### 阶段五：处理 Firefly 特有能力

以下能力依赖本地 Markdown 或构建期扫描，不能直接照搬到 API 模式：

- 本地 Markdown 文件和相对路径图片
- 文件名生成 slug
- 本地文章加密
- Pagefind 构建期搜索
- 本地系列排序
- LQIP 和文章扫描

对应替代方案是：正文由后端返回 Markdown，上一篇/下一篇由后端计算，搜索使用 `/search`，图片走文件上传接口，系列由后端提供接口或明确取消。

## 是否继续使用 Firefly

建议继续使用 Firefly 的页面框架，但把它定位为“展示层模板”，不要继续对现有混合实现做零散修补。

如果目标是快速得到可维护的动态博客，最稳妥的路线是：

1. 保留 Firefly 的布局和视觉组件。
2. 以 `blog-web` API 为唯一业务数据源。
3. 删除页面对本地文章集合的业务依赖。
4. 为缺失的后端能力补充接口，而不是继续添加前端模拟数据。
5. 每完成一个模块，就用管理后台新增、修改、删除数据验证前台是否同步。

## 验收标准

- 管理后台新建文章后，前台无需复制文件即可显示。
- 管理后台修改文章标题、分类、标签后，首页和专题页同步变化。
- 删除数据库文章后，文章详情、归档、搜索和推荐不再显示旧内容。
- 新增动态、相册、照片和友链后，前台可以直接读取。
- 后端未启动时，页面显示明确的 API 错误状态，不伪装成示例数据。
- 前端不再出现 `The collection "posts" does not exist or is empty` 警告。
- 登录、刷新和退出流程在浏览器和后端日志中使用同一种 Token 协议。
