# 用户展示端剩余模块开发任务书

## 1. 当前基线

- **模块**：`blog-web-app`（Spring Boot 3.0.5）+ `blog-web-ui`（Vue 3 + TypeScript）。
- **已完成**：基础工程、三栏响应式布局、Axios/Pinia、Knife4j 分组、首页站点服务、文章分页/详情/热门、分类/标签/时间线接口。
- **开发约定**：Service 按业务域拆包；Controller 只做参数接收和响应封装；SQL 放 Mapper/XML；VO 不直接暴露 Entity；所有接口返回 `R<T>`，分页返回 `PageResult<T>`。
- **接口前缀**：展示端 `/xuyuan`；认证接口 `/xuyuan/app/auth/*`。当前阶段已关闭 JWT 自动配置，后续启用时只在互动和用户接口增加鉴权。

## 2. 剩余范围与优先级

| 编号 | 模块 | 后端包 | 前端页面 | 优先级 | 依赖 |
|---|---|---|---|---|---|
| M01 | 说说 | `controller.moment/service.moment/mapper` | `views/moment` | P0 | 评论/点赞公共组件 |
| M02 | 相册与照片墙 | `controller.gallery/service.gallery/mapper` | `views/gallery` | P0 | 图片资源域名、Lightbox |
| M03 | 杂谈 | `controller.thought/service.thought` | `views/thought` | P1 | 文章查询能力（type=2） |
| M04 | 友情链接 | `controller.friend/service.friend/mapper` | `views/friend` | P1 | 表单校验、邮件字段 |
| M05 | 关于我 | `controller.about/service.about/mapper` | `views/about` | P1 | About VO 技能/履历结构 |
| M06 | 评论 | `controller.comment/service.comment/mapper` | 文章/说说详情内嵌 | P0 | 目标类型约定 |
| M07 | 点赞 | `controller.like/service.like/mapper` | 文章/说说详情内嵌 | P0 | 防重复提交/限流 |
| M08 | 前台认证 | `controller.auth/service.auth/mapper` | `views/auth` | P1 | 当前 JWT 开关策略 |
| M09 | 公共体验与质量 | `components/composables/test` | 全站 | P0 | M01-M08 完成 |

## 3. M01 说说模块

### 后端任务

1. 新建 `MomentMapper extends BaseMapper<Moment>`，查询仅允许展示状态（隐藏/私密记录不得返回）。分页 SQL 按 `created_at DESC, id DESC`，图片字段按 JSON 类型转换。
2. 新建 `MomentService`：`page(MomentPageQuery)`、`detail(Long)`；校验 page/pageSize 上限 50；空数据返回空分页，不抛异常。
3. 新建 `MomentController`：
   - `GET /xuyuan/moments`：`page/pageSize`。
   - `GET /xuyuan/moments/{id}`：不存在返回业务 404。
4. 增加 `@Tag`、`@Operation`、`@ParameterObject`，VO 使用 `MomentVO`，不返回 Entity。

### 前端任务

- 时间轴卡片展示 content、images（1-9 张九宫格）、location、mood、统计信息。
- 图片点击进入 Lightbox；列表支持分页/IntersectionObserver；详情可复用 `TargetInteraction`。
- 实现空态、加载态、接口失败重试；路由 query 保存页码。

### 验收

- 私密/隐藏数据不可通过接口获取；九宫格在 1、4、9 张图片下均不变形。
- `/moments/:id` 刷新可恢复，非法 ID 显示 404 页面而非白屏。

## 4. M02 相册与照片墙

### 后端任务

1. `GalleryMapper`：分页查询相册（`keyword` 匹配 name/description），详情查询相册基本信息及照片列表；`PhotoMapper` 查询单张照片和 EXIF JSON。
2. `GalleryService` 方法：`page(GalleryPageQuery)`、`detail(Long)`、`photo(Long)`；详情按照片 sort 升序。
3. Controller 接口：
   - `GET /xuyuan/galleries?page&pageSize&keyword`
   - `GET /xuyuan/galleries/{id}`
   - `GET /xuyuan/photos/{id}`
4. 图片地址只返回后端生成的 URL；EXIF 缺失时返回 `null`，不得拼接空字符串。

### 前端任务

- 相册列表：搜索防抖 300ms、拍立得卡片轻微随机旋转、封面懒加载。
- 相册详情：缩略图网格、Lightbox、Filmstrip、`Esc`/左右方向键、焦点陷阱。
- EXIF 面板展示 cameraModel、ISO、快门、光圈、焦段、镜头；移动端改为底部抽屉。

### 验收

- 相册搜索不会重复请求；键盘操作可切换上一张/下一张；原图加载失败回退缩略图。
- 大图不阻塞首屏，缩略图具备宽高占位避免布局跳动。

## 5. M03 杂谈模块

### 后端任务

- 复用 `ArticleService`，新增 `ThoughtController`，固定 `type=2`，禁止客户端覆盖 type。
- `GET /xuyuan/thoughts` 复用文章分页 VO；`GET /xuyuan/thoughts/{id}` 复用详情 VO。
- 详情校验文章 type=2 且 status=1，其他情况统一 404。

### 前端任务

- 独立于技术文章的排版主题：阅读宽度、字号、护眼背景存入 `ui` store。
- 列表卡片不显示技术分类筛选，仅支持关键词和分页。

## 6. M04 友情链接模块

### 后端任务

1. `FriendMapper`：只查询审核通过且未删除记录，按 group/sort/id 排序；随机接口使用数据库随机或缓存抽样。
2. `FriendService`：`list(String group)`、`random()`、`apply(FriendApplyParam)`；申请校验 URL、邮箱、重复站点，写入待审核状态。
3. Controller：`GET /friends`、`GET /friends/random`、`POST /friends/apply`；重复申请返回 409。

### 前端任务与验收

- 分组 Tab 与总数展示；随机访问前确认外链并使用 `noopener,noreferrer`。
- 申请表单校验 name/url/email，提交按钮有 loading；409 显示后端 message。

## 7. M05 关于我模块

### 后端任务

- `AboutService.get()` 查询单例 About，转换 `AboutVO`；skills 按 category/sort，milestones 按 year 倒序。
- `GET /xuyuan/about` 增加 Knife4j 响应说明；无数据返回空结构而非 500。

### 前端任务与验收

- 名片、intro、技能进度条/Badge、履历时间轴；技能 level 限制 0-100。
- 长文本和技能名称在 320px 宽度下不溢出，图片提供 alt。

## 8. M06 评论模块

### 后端任务

1. `CommentMapper` 查询指定 `targetType(1 文章/2 说说)+targetId` 的已审核单层评论，按 created_at ASC。
2. `CommentService.list(targetType,targetId,page,pageSize)`、`create(CommentCreateParam)`：校验目标存在、内容 1-1000 字、昵称长度和 URL 格式；敏感词/频率限制沿用公共策略。
3. Controller：`GET /xuyuan/comments`、`POST /xuyuan/comments`；参数错误返回 400，目标不存在返回 404。

### 前端任务与验收

- `CommentList`、`CommentForm` 作为跨页面组件；支持匿名昵称、邮箱、网站；提交后刷新列表。
- 防重复提交、失败保留输入、成功清空；键盘可提交且错误信息与字段关联。

## 9. M07 点赞模块

### 后端任务

- `LikeMapper` 以 targetType/targetId + 用户或 IP 维度查询；`LikeService.toggle` 使用事务/唯一约束保证幂等，更新目标 likeCount。
- `POST /xuyuan/likes/toggle` 返回 `LikeToggleVO`；重复请求必须是切换而非累加。
- 增加限流（IP + 目标，建议 Redis 2 小时窗口），异常时回滚计数。

### 前端任务与验收

- `LikeButton` 防抖并显示 liked/likeCount；请求失败回滚乐观状态。
- 快速连续点击只产生一次有效切换；刷新后状态与服务端一致。

## 10. M08 前台用户认证

### 后端任务

- 独立 `AppAuthController/AppAuthService`，接口严格使用 `/xuyuan/app/auth/*`：register、login、refresh、userinfo、logout、password。
- 注册/登录密码按 API_SPEC 约定处理；账号错误统一 508，不暴露账号是否存在。
- 恢复 JWT 前，Security 白名单保留 `/doc.html`、`/v3/api-docs/**`、静态资源；公开浏览接口 permitAll，互动接口按需求加鉴权。

### 前端任务与验收

- `auth` store 单飞刷新 Token；refresh 失败清理状态并回跳登录前页面。
- 登录、注册、改密表单校验；Token 不出现在 URL、日志和页面文本中。

## 11. M09 公共体验、测试与发布

### 公共组件

- `ArticleCard`、`PaginationLoader`、`TargetInteraction`、`Lightbox`、`ErrorState`、`EmptyState`、`Skeleton`、`ExternalLink`。
- composables：`usePagination`、`useLightbox`、`useAsyncState`、`useCountdown`、`useMediaQuery`。
- 统一 `formatDate`、`formatCount`、`resolveImageUrl`，组件禁止重复实现格式化。

### 测试清单

- 后端：Mapper SQL、Service 参数边界、404/409/400、点赞幂等、评论目标校验。
- 前端：路由刷新、空/错/加载态、Lightbox 键盘、表单校验、Token 刷新队列。
- E2E：匿名浏览全流程、评论、点赞、友链申请、相册预览、移动端断点。

### 发布门禁

- `mvn -pl blog-web/blog-web-app -am test` 通过。
- `npm run build` 通过，部署在 `/xuyuan/` 子路径资源正常。
- Lighthouse 首屏性能 ≥85；无未处理 Promise、控制台错误和横向溢出。

## 12. 里程碑与交付物

1. **S1（P0 互动）**：M01 + M06 + M07；交付说说、评论、点赞 API 与页面组件。
2. **S2（P0 内容）**：M02；交付相册、照片详情、Lightbox/EXIF。
3. **S3（P1 内容）**：M03 + M04 + M05；交付杂谈、友链、关于我。
4. **S4（质量发布）**：M08 + M09；交付认证、测试报告、部署说明和接口联调记录。

每个模块合并请求必须包含：接口/VO 变更说明、Knife4j 截图或 JSON、单元测试结果、异常场景说明及前端联调记录。
