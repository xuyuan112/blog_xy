# 后台管理系统(blog-web-admin)开发任务书

> 版本: v1.3
> 创建日期: 2026-08-25
> 最后更新: 2026-08-25(对齐 API_SPEC.md v1.2.0)
> 适用模块: `blog-web-admin`(后台管理端)
> 前置依赖: `blog-common`(基础设施) + `blog-model`(数据模型)
> 接口规范: [API_SPEC.md](file:///f:/个人博客/API_SPEC.md) v1.2.0

---

## 一、项目背景与目标

### 1.1 项目定位

`blog-web-admin` 是个人全栈博客系统的**后台管理端**服务,运行在 **8080** 端口,面向博主/管理员。接口前缀统一为 `/xuyuan/admin`,认证模块前缀为 `/xuyuan/auth`(与前台共用前缀)。与前台展示端 `blog-web-app`(8081)共享同一套数据库,但接口契约独立。

### 1.2 接口规范对齐说明

本任务书所有接口路径、Header 约定、响应结构**严格遵循** [API_SPEC.md](file:///f:/个人博客/API_SPEC.md) v1.2.0:

- **路径前缀**: 后台管理接口 `/xuyuan/admin/*`,认证接口 `/xuyuan/auth/*`
- **Header**: `Authorization: Bearer <accessToken>`(标准 Bearer 方案)
- **状态码**: 三位数业务码风格(2xx 通用/3xx 后台/5xx 前台/6xx Token),HTTP 响应行统一 200,业务结果由 R.code 表达
- **响应结构**: `R<T>` 含 `code` / `message` / `data`,分页用 `PageResult<T>`

### 1.3 开发现状盘点

经全量代码扫描,当前资产清单如下:

| 层 | 已就绪 | 缺失 |
|---|---|---|
| 数据模型 | 17 个实体、9 个枚举、4 个 TypeHandler、3 个 JSON VO | — |
| 基础设施 | R、PageResult、BusinessException、GlobalExceptionHandler、JwtUtil、CaptchaUtil、5 个 Config | **JWT 鉴权过滤器、登录拦截器** |
| DTO/VO | 13 个 DTO、7 个 VO | **NoticeCreateParam、WordCreateParam、AboutUpdateParam、ScheduleCreateParam**(M8/M9 新增) |
| 业务层 Service | 无 | **全部 Service 接口与实现** |
| 控制层 Controller | 无 | **全部 Controller** |
| 数据访问层 Mapper | 无 | **全部 Mapper** |
| SQL 脚本 | `schema.sql` 已建库 + 17 张表 | **init-data.sql 初始数据** |

### 1.4 目标

按本任务书规划,完成 admin 端从**数据库初始化 → Mapper → Service → Controller → 鉴权链路**的端到端实现,最终交付一个可启动、可登录、可管理内容的后台系统。

---

## 二、技术栈与约定

### 2.1 技术栈

| 维度 | 选型 |
|---|---|
| JDK | 17 |
| Spring Boot | 3.x |
| ORM | MyBatis-Plus |
| 数据库 | MySQL 8 |
| 缓存 | Redis(Lettuce) |
| 对象存储 | MinIO |
| 认证 | JWT(JJWT) + Spring Security |
| 接口文档 | Knife4j 4.1.0 + springdoc-openapi |
| 工具库 | Hutool、EasyCaptcha、Lombok |
| 密码加密 | MD5(32 位十六进制小写) |

### 2.2 编码约定

- **包结构**: `com.xuyuan.blog.admin.{controller|service|service.impl|mapper}`
- **命名**: Controller `XxxController`、Service 接口 `XxxService`、实现 `XxxServiceImpl`、Mapper `XxxMapper`
- **统一响应**: 所有接口返回 `R<T>`,异常走 `GlobalExceptionHandler`
- **分页**: 用 `PageResult<T>`,MyBatis-Plus 的 `IPage` 转 `PageResult` 后返回
- **状态码**: 业务异常抛 `BusinessException(HttpStatus, message)`,不再用五位数业务码
- **逻辑删除**: 实体继承 `BaseEntity`,`deleted` 字段由 MyBatis-Plus 自动处理
- **审计字段**: `create_time`/`update_time`/`create_by`/`update_by` 由 `MyMetaObjectHandler` 自动填充

### 2.3 接口路径约定(对齐 API_SPEC)

| 模块 | 前缀 | 示例 |
|---|---|---|
| 认证 | `/xuyuan/auth` | `POST /xuyuan/auth/login` |
| 后台-文章 | `/xuyuan/admin/articles` | `GET /xuyuan/admin/articles` |
| 后台-分类 | `/xuyuan/admin/categories` | `POST /xuyuan/admin/categories` |
| 后台-标签 | `/xuyuan/admin/tags` | `PUT /xuyuan/admin/tags/{id}` |
| 后台-说说 | `/xuyuan/admin/moments` | `DELETE /xuyuan/admin/moments/{id}` |
| 后台-相册 | `/xuyuan/admin/galleries` | `POST /xuyuan/admin/galleries/{id}/photos` |
| 后台-照片 | `/xuyuan/admin/photos` | `DELETE /xuyuan/admin/photos/{id}` |
| 后台-友链 | `/xuyuan/admin/friends` | `PUT /xuyuan/admin/friends/{id}/audit` |
| 后台-评论 | `/xuyuan/admin/comments` | `PUT /xuyuan/admin/comments/{id}/status` |
| 后台-文件 | `/xuyuan/admin/files` | `POST /xuyuan/admin/files/upload` |
| 后台-统计 | `/xuyuan/admin/dashboard` | `GET /xuyuan/admin/dashboard/stats` |
| 后台-配置 | `/xuyuan/admin/configs` | `PUT /xuyuan/admin/configs` |
| 后台-公告 | `/xuyuan/admin/notices` | `POST /xuyuan/admin/notices` |
| 后台-一言 | `/xuyuan/admin/words` | `PUT /xuyuan/admin/words/{id}` |
| 后台-关于我 | `/xuyuan/admin/about` | `PUT /xuyuan/admin/about` |
| 后台-日程 | `/xuyuan/admin/schedules` | `POST /xuyuan/admin/schedules` |

### 2.4 JWT 传递约定(对齐 API_SPEC 1.2)

- **Header Key**: `Authorization`(HTTP 标准)
- **Header Value**: `Bearer <accessToken>`(带 Bearer 前缀)
- **请求示例**:
  ```
  GET /xuyuan/admin/articles HTTP/1.1
  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx.yyy
  ```
- **JwtAuthenticationFilter** 取值:从 `Authorization` Header 去掉 `Bearer ` 前缀取 token
- **Knife4j 界面**: Authorize 框输入 token 后自动带 `Authorization: Bearer xxx`
- **AccessToken 有效期**: 30 分钟
- **RefreshToken 有效期**: 7 天,仅用于刷新接口,不参与业务鉴权

### 2.5 验证码约定

- **工具**: EasyCaptcha(已实现 `CaptchaUtil`)
- **类型**: 算术验证码(`ArithmeticCaptcha`,3 个运算符)
- **存储**: Redis,Key 形如 `captcha:{uuid}`,5 分钟过期
- **校验成功后**: 立即删除缓存(一次性使用)
- **大小写**: 忽略大小写(verify 用 `equalsIgnoreCase`)

### 2.6 状态码约定(对齐 API_SPEC 23)

采用**三位数业务码**风格,按段划分业务域。HTTP 响应行统一返回 200,业务结果由 `R.code` 字段表达:

| 段 | code 范围 | 说明 |
|---|---|---|
| 2xx 通用段 | 200-207 | 系统级通用状态 |
| 3xx 后台管理端段 | 301-321 | blog-web-admin 业务专用 |
| 5xx 前台展示端段 | 501-506 | blog-web-app 业务专用 |
| 6xx Token 段 | 601-603 | 认证 token 相关 |

**后台管理端常用业务码速查**:

| code | 枚举名 | 使用场景 |
|---|---|---|
| 200 | SUCCESS | 成功 |
| 202 | PARAM_ERROR | 参数不正确 |
| 203 | SERVICE_ERROR | 服务异常 |
| 304 | ADMIN_CAPTCHA_CODE_NOT_FOUND | 未输入验证码 |
| 303 | ADMIN_CAPTCHA_CODE_EXPIRED | 验证码已过期 |
| 302 | ADMIN_CAPTCHA_CODE_ERROR | 验证码错误 |
| 305 | ADMIN_LOGIN_AUTH | 未登录 |
| 307 | ADMIN_ACCOUNT_ERROR | 用户名或密码错误(防枚举) |
| 308 | ADMIN_ACCOUNT_DISABLED_ERROR | 该用户已被禁用 |
| 309 | ADMIN_ACCESS_FORBIDDEN | 无访问权限 |
| 310-317 | ADMIN_*_NOT_FOUND | 各资源不存在 |
| 601 | TOKEN_EXPIRED | token 过期 |
| 602 | TOKEN_INVALID | token 非法 |
| 603 | TOKEN_MISSING | token 缺失 |

完整业务码定义见 [ResultCode.java](file:///f:/个人博客/blog-parent/blog-common/src/main/java/com/xuyuan/blog/common/result/ResultCode.java) 和 [API_SPEC 23 节](file:///f:/个人博客/API_SPEC.md)。

---

## 三、模块任务分解

按依赖顺序划分为 **5 个阶段、13 个任务模块**。每个模块给出依赖关系、产出物、接口清单、验收标准。

### 阶段一:基础设施补全(地基)

#### 任务 M0:数据库初始化脚本

| 项 | 内容 |
|---|---|
| 依赖 | 无 |
| 产出 | `f:\个人博客\schema.sql`(已存在,含建库 + 17 张表 DDL)<br>`blog-parent/sql/02-init-data.sql`(初始数据,待创建) |
| 现状 | schema.sql 已完成:含 `CREATE DATABASE blog` + `USE blog` + 17 张表;密码字段为 MD5(32 位) |
| 内容 | init-data.sql 含:1 个超级管理员账号(MD5 加密密码)、默认分类、默认站点配置、示例一言数据 |
| 验收 | 在 MySQL 空实例执行 schema.sql + init-data.sql 无报错;`SELECT * FROM t_user WHERE username='admin'` 返回 1 行,密码字段为 32 位 MD5 哈希 |

#### 任务 M1:JWT 鉴权链路

| 项 | 内容 |
|---|---|
| 依赖 | M0 |
| 产出 | `common/filter/JwtAuthenticationFilter.java`<br>`common/security/JwtAuthenticationEntryPoint.java`<br>`common/security/JwtAccessDeniedHandler.java`<br>修改 `common/config/SecurityConfig.java` |
| 内容 | ① JWT 过滤器:从 `Authorization` Header 取 `Bearer <token>` → 去前缀 → `JwtUtil.parseToken` → 构建 `Authentication` 存入 `SecurityContext`<br>② 未登录访问受保护接口 → `AuthenticationEntryPoint` 返回 `R.fail(401, "未登录或认证已过期")`<br>③ 权限不足 → `AccessDeniedHandler` 返回 `R.fail(403, "无权限访问")`<br>④ `SecurityConfig` 放行 `/xuyuan/auth/captcha`、`/xuyuan/auth/login`、`/xuyuan/auth/refresh`、Knife4j 相关路径,其余 `authenticated()` |
| 验收 | 不带 token 访问 `/xuyuan/admin/articles` 返回 401 JSON;带正确 Bearer token 返回数据 |

---

### 阶段二:核心业务模块(管理链路)

#### 任务 M2:认证模块(Auth)

| 项 | 内容 |
|---|---|
| 依赖 | M1 |
| 产出 | `admin/mapper/UserMapper.java`<br>`admin/service/AuthService.java` + Impl<br>`admin/controller/AuthController.java` |
| 接口清单 | `GET  /xuyuan/auth/captcha` 生成验证码<br>`POST /xuyuan/auth/login` 登录<br>`POST /xuyuan/auth/logout` 登出<br>`POST /xuyuan/auth/refresh` 刷新 token<br>`GET  /xuyuan/auth/userinfo` 当前用户信息 |
| 前端入参 | `LoginParam`: `username` / `password` / `captchaKey` / `captchaCode` |

**登录校验三步骤(AuthServiceImpl.login 实现)**:

按以下顺序逐步校验,**任一步失败立即抛 `BusinessException` 返回**,后续步骤不执行。使用 `ResultCode.toException()` 抛出对应业务码:

| 步骤 | 校验项 | 失败 ResultCode | code | message |
|---|---|---|---|---|
| 1.1 | 判断 `captchaCode` 是否为空 | `ADMIN_CAPTCHA_CODE_NOT_FOUND` | 304 | 未输入验证码 |
| 1.2 | 根据 `captchaKey` 从 Redis 查缓存 code,若为空 | `ADMIN_CAPTCHA_CODE_EXPIRED` | 303 | 验证码已过期 |
| 1.3 | 比较 `captchaCode` 与缓存 code(忽略大小写),不一致 | `ADMIN_CAPTCHA_CODE_ERROR` | 302 | 验证码错误 |
| 2.1 | 根据 `username` 查数据库,若为空 | `ADMIN_ACCOUNT_ERROR` | 307 | 用户名或密码错误(防账号枚举,合并) |
| 2.2 | 查看 `UserStatus`,若禁用 | `ADMIN_ACCOUNT_DISABLED_ERROR` | 308 | 该用户已被禁用 |
| 3.1 | MD5 比对 `password` 与数据库密码,不一致 | `ADMIN_ACCOUNT_ERROR` | 307 | 用户名或密码错误 |
| 4 | 全部通过 → `JwtUtil.createAccessToken` + `createRefreshToken`,返回 `LoginVO` | `SUCCESS` | 200 | success |

**代码示例**:

```java
// 1.1 验证码为空
if (StrUtil.isBlank(param.getCaptchaCode())) {
    throw ResultCode.ADMIN_CAPTCHA_CODE_NOT_FOUND.toException();
}
// 1.2 验证码已过期
String cachedCode = redisTemplate.opsForValue().get("captcha:" + param.getCaptchaKey());
if (StrUtil.isBlank(cachedCode)) {
    throw ResultCode.ADMIN_CAPTCHA_CODE_EXPIRED.toException();
}
// 1.3 验证码不正确
if (!cachedCode.equalsIgnoreCase(param.getCaptchaCode())) {
    throw ResultCode.ADMIN_CAPTCHA_CODE_ERROR.toException();
}
// 2.1 账号不存在(防枚举,与密码错合并返回同一业务码)
User user = userMapper.selectByUsername(param.getUsername());
if (user == null) {
    throw ResultCode.ADMIN_ACCOUNT_ERROR.toException();
}
// 2.2 账号被禁
if (UserStatus.DISABLED.getCode().equals(user.getStatus())) {
    throw ResultCode.ADMIN_ACCOUNT_DISABLED_ERROR.toException();
}
// 3.1 密码错误
if (!MD5(param.getPassword()).equals(user.getPassword())) {
    throw ResultCode.ADMIN_ACCOUNT_ERROR.toException();
}
// 4. 登录成功,生成 token
String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername());
String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUsername());
return new LoginVO(accessToken, refreshToken, UserInfoVO.from(user));
```

> **设计说明**:ResultCode 采用三位数业务码风格,每个业务码对应一种明确的业务场景。登录场景下"账号不存在"和"密码错误"统一返回 `307 ADMIN_ACCOUNT_ERROR`(防账号枚举),而 `306 ADMIN_ACCOUNT_NOT_EXIST_ERROR` 保留给其他查询场景使用(如后台查询指定用户详情)。

**验证码一次性**: `CaptchaUtil.verify` 已在成功后自动删除缓存,业务层无需处理。

**密码比对细节**:
- 不用 `BCryptPasswordEncoder.matches`,直接 `MD5(rawPassword).equals(user.getPassword())`
- 账号不存在和密码错统一返回"用户名或密码错误"(防账号枚举)
- 登出:服务端将 accessToken 加入 Redis 黑名单(剩余 TTL 过期)

| 验收 | Knife4j 上能调通 5 个接口;错密码返回 401"用户名或密码错误";账号禁用返回 401"账号已被禁用";验证码三步分别返回 400 对应错误 |

#### 任务 M3:文章管理模块(Article)

| 项 | 内容 |
|---|---|
| 依赖 | M2 |
| 产出 | `ArticleMapper`、`CategoryMapper`、`TagMapper`、`ArticleTagMapper`<br>`ArticleService` + Impl、`CategoryService` + Impl、`TagService` + Impl<br>`ArticleController`、`CategoryController`、`TagController` |
| 接口清单(API_SPEC 13、14) | **文章(7 个)**:<br>① `GET    /xuyuan/admin/articles` 分页(含草稿/全部状态)<br>② `POST   /xuyuan/admin/articles` 新增<br>③ `PUT    /xuyuan/admin/articles/{id}` 编辑(全量更新)<br>④ `GET    /xuyuan/admin/articles/{id}` 详情(含 tagIds 回显)<br>⑤ `DELETE /xuyuan/admin/articles` 批量删除(Body: `IdsParam`,逻辑删除进回收站)<br>⑥ `PUT    /xuyuan/admin/articles/{id}/status` 变更状态(Body: `ArticleStatusParam`)<br>⑦ `POST   /xuyuan/admin/articles/draft` 保存草稿(自动保存)<br>**分类(4 个)**:GET/POST `/xuyuan/admin/categories`、PUT/DELETE `/xuyuan/admin/categories/{id}`<br>**标签(4 个)**:GET/POST `/xuyuan/admin/tags`、PUT/DELETE `/xuyuan/admin/tags/{id}` |
| 关键点 | ① 文章-标签多对多关系走 `t_article_tag`<br>② 状态切换用 `ArticleStatus` 枚举<br>③ `ArticleCreateParam` 已含 `tagIds` 字段<br>④ 草稿保存:已存在 id 则更新,否则新建<br>⑤ 删除分类时校验无文章 |
| 验收 | 能创建文章、绑定标签、按状态/分类筛选分页;草稿自动保存生效;逻辑删除生效 |

#### 任务 M4:评论管理模块(Comment)

| 项 | 内容 |
|---|---|
| 依赖 | M2 |
| 产出 | `CommentMapper`、`CommentService` + Impl、`CommentController` |
| 接口清单(API_SPEC 18) | ① `GET    /xuyuan/admin/comments` 分页(Query: targetType、status)<br>② `PUT    /xuyuan/admin/comments/{id}/status` 审核/隐藏(Body: `CommentStatusParam`)<br>③ `DELETE /xuyuan/admin/comments/{id}` 删除评论 |
| 关键点 | 审核和隐藏合并为一个接口,通过 `status` 字段区分(1 已发布 / 2 已隐藏) |
| 验收 | 能批量审核/隐藏评论;删除走逻辑删除 |

#### 任务 M5:友链管理模块(Friend)

| 项 | 内容 |
|---|---|
| 依赖 | M2 |
| 产出 | `FriendMapper`、`FriendService` + Impl、`FriendController` |
| 接口清单(API_SPEC 17) | ① `GET    /xuyuan/admin/friends` 分页(Query: status)<br>② `POST   /xuyuan/admin/friends` 新增<br>③ `PUT    /xuyuan/admin/friends/{id}` 编辑<br>④ `DELETE /xuyuan/admin/friends/{id}` 删除<br>⑤ `PUT    /xuyuan/admin/friends/{id}/audit` 审核(Body: `FriendAuditParam`)<br>⑥ `PUT    /xuyuan/admin/friends/{id}/sort` 调整排序(Body: `SortParam`) |
| 关键点 | 审核通过/驳回合并为一个 audit 接口,通过 `status` 区分;支持调整排序 |
| 验收 | 能审核友链申请(通过/驳回);能调整友链排序;驳回后前台不再展示 |

---

### 阶段三:多媒体模块

#### 任务 M6:相册与照片模块(Gallery + Photo)

| 项 | 内容 |
|---|---|
| 依赖 | M2、M12(文件上传) |
| 产出 | `GalleryMapper`、`PhotoMapper`<br>`GalleryService` + Impl、`PhotoService` + Impl<br>`GalleryController`、`PhotoController` |
| 接口清单(API_SPEC 16) | **相册(4 个)**:GET/POST `/xuyuan/admin/galleries`、PUT/DELETE `/xuyuan/admin/galleries/{id}`<br>**照片(2 个)**:<br>① `POST   /xuyuan/admin/galleries/{id}/photos` 批量上传照片(multipart,异步生成缩略图 + EXIF)<br>② `DELETE /xuyuan/admin/photos/{id}` 删除单张照片 |
| 关键点 | ① 照片上传走 MinIO,缩略图用 `thumbnailator`<br>② EXIF 信息用 `metadata-extractor` 解析,存 `t_photo.exif` 字段(JSON,走 `ExifTypeHandler`)<br>③ 上传响应 `BatchPhotoUploadVO`(含 galleryId + photos 数组) |
| 验收 | 能批量上传图片,EXIF 自动解析入库;能按相册分页查看照片;能删除单张照片 |

#### 任务 M7:说说管理模块(Moment)

| 项 | 内容 |
|---|---|
| 依赖 | M2 |
| 产出 | `MomentMapper`、`MomentService` + Impl、`MomentController` |
| 接口清单(API_SPEC 15) | ① `GET    /xuyuan/admin/moments` 分页(含隐藏/私密)<br>② `POST   /xuyuan/admin/moments` 发布说说(文本 + 最多 9 图)<br>③ `PUT    /xuyuan/admin/moments/{id}` 编辑<br>④ `DELETE /xuyuan/admin/moments/{id}` 删除 |
| 关键点 | 说说可带图片(多图,走 MinIO);`MomentCreateParam` 含 visible 字段 |
| 验收 | 能发布带图说说;逻辑删除生效 |

---

### 阶段四:内容配置模块

#### 任务 M8:公告与一言模块(Notice + Word)

| 项 | 内容 |
|---|---|
| 依赖 | M2 |
| 产出 | `NoticeMapper`、`WordMapper`<br>`NoticeService` + Impl、`WordService` + Impl<br>`NoticeController`、`WordController`<br>**新增 DTO**: `NoticeCreateParam`、`WordCreateParam`<br>**新增 VO**: `NoticeVO`、`WordVO` |
| 接口清单(API_SPEC 21) | **公告(4 个)**:GET/POST `/xuyuan/admin/notices`、PUT/DELETE `/xuyuan/admin/notices/{id}`<br>**一言(4 个)**:GET/POST `/xuyuan/admin/words`、PUT/DELETE `/xuyuan/admin/words/{id}` |
| 关键点 | ① 公告/一言均为标准 CRUD<br>② 前台 `GET /xuyuan/site/words/random` 从该表随机取一条<br>③ 前台 `GET /xuyuan/site/notice` 取最新一条 |
| 验收 | 公告能新增/编辑/删除;一言 CRUD 正常,前台随机接口返回正常 |

#### 任务 M9:关于我与日程模块(About + Schedule)

| 项 | 内容 |
|---|---|
| 依赖 | M2 |
| 产出 | `AboutMapper`、`ScheduleMapper`<br>`AboutService` + Impl、`ScheduleService` + Impl<br>`AboutController`、`ScheduleController`<br>**新增 DTO**: `AboutUpdateParam`、`ScheduleCreateParam`<br>**新增 VO**: `ScheduleVO`(About 复用前台 AboutVO) |
| 接口清单(API_SPEC 22) | **关于我(2 个)**:GET/PUT `/xuyuan/admin/about`<br>**日程(4 个)**:GET/POST `/xuyuan/admin/schedules`、PUT/DELETE `/xuyuan/admin/schedules/{id}` |
| 关键点 | ① `About` 的 `milestones`/`skills` 是 JSON 字段,走对应 TypeHandler<br>② 关于我为单行配置,GET 返回当前记录,PUT 全量更新<br>③ `Schedule.type` 是 String,用 `ScheduleType` 枚举 |
| 验收 | 能编辑"关于我"页面的技能树和里程碑;日程能按倒计时/日程分类筛选 |

#### 任务 M10:站点配置模块(SiteConfig)

| 项 | 内容 |
|---|---|
| 依赖 | M2 |
| 产出 | `SiteConfigMapper`、`SiteConfigService` + Impl、`SiteConfigController` |
| 接口清单(API_SPEC 20.3) | ① `GET /xuyuan/admin/configs` 查询配置(返回 `Map<String, String>`)<br>② `PUT /xuyuan/admin/configs` 更新配置(Body: `Map<String, String>`) |
| 关键点 | 站点配置按 key-value 存储,可一次性查询所有,也可批量更新 |
| 验收 | 能修改站点名称、副标题、ICP、公告等全局配置 |

---

### 阶段五:数据统计与文件服务

#### 任务 M11:仪表盘统计模块(Dashboard)

| 项 | 内容 |
|---|---|
| 依赖 | M3、M4、M6、M7 全部完成 |
| 产出 | `DashboardMapper`(可聚合查询,不强求单表)<br>`DashboardService` + Impl、`DashboardController` |
| 接口清单(API_SPEC 20.1、20.2) | ① `GET /xuyuan/admin/dashboard/stats` 总览统计<br>② `GET /xuyuan/admin/dashboard/trend` 访问趋势(Query: days,默认 30) |
| 产出 VO | 已有 `DashboardStatsVO`(9 个字段)、`TrendPointVO` |
| 关键点 | ① 统计数据可缓存到 Redis(5 分钟)<br>② 趋势数据来源 `ArticleViewLog` 表<br>③ 统计维度:文章数、说说数、相册数、照片数、友链数、评论数、总访问量、待审核友链数、待审核评论数 |
| 验收 | 仪表盘页面能展示总数卡片 + 趋势折线图数据 |

#### 任务 M12:文件上传模块(File)

| 项 | 内容 |
|---|---|
| 依赖 | M1 |
| 产出 | `admin/service/FileService` + Impl、`admin/controller/FileController` |
| 接口清单(API_SPEC 19) | ① `POST /xuyuan/admin/files/upload` 通用文件上传(FormData: file + scene)<br>② `POST /xuyuan/admin/files/upload/image` 图片专用上传(编辑器拖拽/粘贴) |
| 关键点 | ① 调用 `MinioUtil` 上传<br>② 图片自动生成缩略图<br>③ 返回 `FileUploadVO`(含 url、fileName、size)<br>④ 图片上传接口仅限 `jpg/png/gif/webp`<br>⑤ scene 标识场景:`article`/`avatar`/`moment`/`gallery`/`comment` |
| 验收 | 能上传任意类型文件;图片返回原图 + 缩略图双 URL;非图片类型被拒绝 |

#### 任务 M13:前台用户管理模块(AppUser)

> 新增模块(v1.3): 配合前台展示端(blog-web-app)用户体系落地。前台用户独立存于 `t_app_user` 表,
> 由后台统一管理(分页查询、禁用/启用、重置密码、删除)。前台注册/登录/认证接口在 blog-web-app 实现,
> 本任务仅覆盖**后台管理端**对前台用户的管理接口。

| 项 | 内容 |
|---|---|
| 依赖 | M1(JWT 鉴权链路)、M2(后台管理员登录,需要 admin 角色才能操作) |
| 产出 | `admin/mapper/AppUserMapper` + XML(可选)、`admin/service/AppUserService` + Impl、`admin/controller/AppUserController` |
| 接口清单(API_SPEC 24) | ① `GET /xuyuan/admin/users` 分页查询(Query: current/size/username/nickname/status)<br>② `GET /xuyuan/admin/users/{id}` 详情<br>③ `PUT /xuyuan/admin/users/{id}/status` 禁用/启用(Body: AppUserStatusParam)<br>④ `PUT /xuyuan/admin/users/{id}/reset-password` 重置密码(Body: AppUserResetPasswordParam)<br>⑤ `DELETE /xuyuan/admin/users/{id}` 逻辑删除 |
| 产出 DTO | 已创建 `AppUserPageParam`、`AppUserStatusParam`、`AppUserResetPasswordParam` |
| 产出 VO | 已创建 `AppUserAdminVO`(8 字段: id/username/nickname/avatar/email/status/lastLoginAt/createTime) |
| 实体 | 已创建 `AppUser`(blog-model,继承 BaseEntity,映射 t_app_user) |
| 关键点 | ① 查询用 MyBatis-Plus `LambdaQueryWrapper` 拼接 username/nickname LIKE + status =<br>② 禁用/启用只更新 status 字段(`updateById`)<br>③ 重置密码: `MD5(newPassword)` 后 `updateById`<br>④ 删除走 `removeById`(逻辑删除,@TableLogic 自动加 deleted=1)<br>⑤ 删除用户后评论/点赞**保留**(user_id 仍可指向已删除记录),展示时显示"已注销用户"<br>⑥ 不返回 password 字段(VO 已排除) |
| 验收 | 后台能分页查询前台用户、切换状态、重置密码、删除;被禁用用户无法登录前台 |

**业务码使用**:

| 操作 | 成功 | 失败 |
|---|---|---|
| 查询/详情 | `SUCCESS(200)` | 用户不存在 → `APP_USER_NOT_FOUND(509)` |
| 禁用/启用 | `SUCCESS(200)` | 用户不存在 → `APP_USER_NOT_FOUND(509)` |
| 重置密码 | `SUCCESS(200)` | 用户不存在 → `APP_USER_NOT_FOUND(509)` |
| 删除 | `SUCCESS(200)` | 用户不存在 → `APP_USER_NOT_FOUND(509)` |

> **关联前台任务(非本任务书范围,但需协同)**: blog-web-app 需同步实现前台认证接口
> (注册 `POST /xuyuan/app/auth/register`、登录 `POST /xuyuan/app/auth/login` 等,详见 API_SPEC 23)。
> 后台管理动作(禁用/删除)后,前台已签发的 Token 处理策略:
> - **简单方案**: 等待 Token 自然过期(15 分钟内仍可用)
> - **严格方案**: 禁用/删除时把用户 ID 加入 Redis 黑名单,JWT 过滤器校验时检查

---

## 四、依赖关系图

```
M0 数据库 ──┬──> M1 鉴权链路 ──┬──> M2 认证模块 ──┬──> M3 文章 ─────┐
            │                  │                 ├──> M4 评论       │
            │                  │                 ├──> M5 友链       │  M11 仪表盘
            │                  │                 ├──> M6 相册 ←── M12 文件上传
            │                  │                 ├──> M7 说说       │  (依赖 M3/M4/M6/M7)
            │                  │                 ├──> M8 公告一言   │
            │                  │                 ├──> M9 关于我日程  │
            │                  │                 ├──> M10 站点配置  │
            │                  │                 └──> M13 前台用户管理
            │                  └──> M12 文件上传                     │
            └──────────────────────────────────────────────────────────┘
```

**关键路径(最长依赖链)**: M0 → M1 → M2 → M3 → M11

---

## 五、开发顺序建议

按"先打通主链路、再横向铺开"的原则,推荐顺序:

| 顺序 | 任务 | 理由 |
|---|---|---|
| 1 | M0 数据库 | 一切的前提 |
| 2 | M1 鉴权链路 | 全局安全基座 |
| 3 | M2 认证模块 | 登录是所有后续接口的钥匙 |
| 4 | M12 文件上传 | 文章/说说/相册都要用,提前就绪 |
| 5 | M3 文章管理 | 内容系统核心,验证整套链路 |
| 6 | M4 评论管理 | 与文章强关联 |
| 7 | M5 友链管理 | 独立模块,快速完成 |
| 8 | M7 说说管理 | 独立模块,可发布带图内容 |
| 9 | M13 前台用户管理 | 与 M5/M7 并行,CURD 简单,为前台登录铺路 |
| 10 | M6 相册模块 | 涉及 EXIF 解析,稍复杂 |
| 11 | M8 公告一言 | 轻量收尾 |
| 12 | M9 关于我日程 | JSON 字段处理 |
| 13 | M10 站点配置 | 全局配置 |
| 14 | M11 仪表盘 | 最后做,数据全了统计才有意义 |

---

## 六、全局约定与注意事项

### 6.1 统一异常处理

- 业务异常: `throw new BusinessException(HttpStatus.BAD_REQUEST, "验证码错误")`
- 参数校验: `@Valid` + `GlobalExceptionHandler` 捕获 `MethodArgumentNotValidException` 返回 400
- 权限异常: `JwtAuthenticationEntryPoint` / `JwtAccessDeniedHandler` 统一返回 `R`
- 状态码严格遵循 API_SPEC 21 节,使用 HTTP 标准码

### 6.2 分页约定

```java
// Service
Page<Article> page = new Page<>(current, size);
articleMapper.selectPage(page, wrapper);
return PageResult.of(page);
```

分页响应结构对齐 API_SPEC 1.3:`records` / `total` / `page` / `pageSize` / `pages`。

### 6.3 接口路径与 Knife4j 分组

Knife4jConfiguration 的 `pathsToMatch` 需对齐 API_SPEC 路径前缀:

| 分组 | pathsToMatch |
|---|---|
| 认证管理 | `/xuyuan/auth/**` |
| 文章管理 | `/xuyuan/admin/articles/**`, `/xuyuan/admin/categories/**`, `/xuyuan/admin/tags/**` |
| 评论管理 | `/xuyuan/admin/comments/**` |
| 友链管理 | `/xuyuan/admin/friends/**` |
| 相册管理 | `/xuyuan/admin/galleries/**`, `/xuyuan/admin/photos/**` |
| 说说管理 | `/xuyuan/admin/moments/**` |
| 仪表盘统计 | `/xuyuan/admin/dashboard/**` |
| 文件管理 | `/xuyuan/admin/files/**` |
| 公告一言 | `/xuyuan/admin/notices/**`, `/xuyuan/admin/words/**` |
| 关于我日程 | `/xuyuan/admin/about`, `/xuyuan/admin/schedules/**` |
| 站点配置 | `/xuyuan/admin/configs` |

### 6.4 数据库字段映射

- 实体驼峰 `createTime` ↔ 数据库下划线 `create_time`(已配 `map-underscore-to-camel-case: true`)
- 逻辑删除字段 `deleted`(已配 `logic-delete-field: deleted`)
- JSON 字段(`about.milestones`、`photo.exif` 等)走对应 TypeHandler

### 6.5 密码与敏感信息

- 密码用 **MD5 加密**后存储(32 位十六进制小写),**不存明文**
- 校验密码:`MD5(rawPassword).equals(user.getPassword())`,封装到 `blog-common/util/PasswordUtil.java`(待创建)
- 数据库密码、JWT secret、MinIO key 当前硬编码在 yml,上线前改 `${ENV_VAR:default}` 注入(已记入 TODO)

---

## 七、验收里程碑

| 里程碑 | 完成任务 | 验收标准 |
|---|---|---|
| **MS-1 鉴权打通** | M0 + M1 + M2 | 能登录、拿 token、带 Bearer token 访问受保护接口 |
| **MS-2 内容核心** | + M3 + M12 | 能登录后创建文章、上传图片、草稿自动保存 |
| **MS-3 互动闭环** | + M4 + M5 + M7 | 能管理评论、友链、说说 |
| **MS-4 多媒体完整** | + M6 + M8 + M9 + M10 | 相册、公告、关于我、日程、站点配置全通 |
| **MS-5 全量交付** | + M11 | 仪表盘统计可用,Knife4j 文档完整,接口对齐 API_SPEC |

---

## 八、后续待决项(TODO)

| 项 | 说明 | 优先级 |
|---|---|---|
| 登录失败锁定 | 连续错 N 次锁定账号 X 分钟(Redis 计数) | 中 |
| refresh token 轮换 | 刷新时是否发新的 refresh token | 中 |
| 登出黑名单 | 登出把 token 加入 Redis 黑名单(任务书已写入) | 高(已规划) |
| 操作日志 | 是否需要 AOP 记录管理员操作 | 低 |
| 接口限流 | 是否对登录接口加 IP 限流(429) | 中 |
| 敏感信息环境变量化 | yml 里密码/secret/key 改 `${ENV_VAR:default}` | 高(上线前必做) |
| RSA 密码加密传输 | API_SPEC 2.2 建议"前端 RSA 加密传输",是否实现 | 低 |

---

## 九、附录:已就绪资产索引

### 9.1 实体清单(17 个)

| 实体 | 表 | 用途 |
|---|---|---|
| User | t_user | 用户 |
| Article | t_article | 文章 |
| ArticleTag | t_article_tag | 文章-标签关联 |
| ArticleViewLog | t_article_view_log | 文章访问日志 |
| Category | t_category | 分类 |
| Tag | t_tag | 标签 |
| Comment | t_comment | 评论 |
| Friend | t_friend | 友链 |
| Gallery | t_gallery | 相册 |
| Photo | t_photo | 照片 |
| Moment | t_moment | 说说 |
| Like | t_like | 点赞 |
| Notice | t_notice | 公告 |
| Word | t_word | 一言 |
| About | t_about | 关于我 |
| Schedule | t_schedule | 日程 |
| SiteConfig | t_site_config | 站点配置 |

### 9.2 DTO 清单(13 个已有 + 4 个待新增)

**已有(13 个)**:
`ArticleCreateParam`、`ArticleStatusParam`、`CategoryCreateParam`、`CommentStatusParam`、`FriendAuditParam`、`FriendCreateParam`、`GalleryCreateParam`、`IdsParam`、`LoginParam`、`MomentCreateParam`、`RefreshParam`、`SortParam`、`TagCreateParam`

**待新增(M8/M9 任务)**:
`NoticeCreateParam`、`WordCreateParam`、`AboutUpdateParam`、`ScheduleCreateParam`

### 9.3 VO 清单(7 个已有 + 2 个待新增)

**已有(7 个)**:
| 包 | VO |
|---|---|
| `vo/auth/` | LoginVO、UserInfoVO、CaptchaVO |
| `vo/dashboard/` | DashboardStatsVO、TrendPointVO |
| `vo/article/` | ArticleAdminVO |
| `vo/file/` | FileUploadVO |

**待新增(M8 任务)**: `NoticeVO`、`WordVO`、`ScheduleVO`

### 9.4 基础设施清单(common)

| 类型 | 文件 |
|---|---|
| Config | JacksonConfig、MinioConfig、MybatisPlusConfig、RedisConfig、SecurityConfig |
| Util | CaptchaUtil、JwtUtil |
| Exception | BusinessException、GlobalExceptionHandler |
| Result | R、PageResult |
| Handler | MyMetaObjectHandler |
| Entity | BaseEntity |

### 9.5 枚举清单(9 个)

| 枚举 | code 类型 | 说明 |
|---|---|---|
| `ArticleStatus` | Integer | 文章状态(0 草稿/1 已发布/2 私密/3 回收站) |
| `ArticleType` | Integer | 文章类型(1 技术/2 杂谈) |
| `CommentStatus` | Integer | 评论状态(0 待审核/1 已发布/2 已隐藏) |
| `FriendAuditStatus` | Integer | 友链审核状态(0 待审核/1 通过/2 驳回) |
| `ScheduleType` | String | 日程类型(countdown 倒计时/schedule 日程) |
| `TargetType` | Integer | 评论/点赞目标类型(1 文章/2 说说) |
| `UserStatus` | Integer | 用户状态(0 禁用/1 正常) |
| `YesNo` | Integer | 通用是否(0 否/1 是) |
| **`ResultCode`** | Integer | **三位数业务码(2xx 通用/3xx 后台/5xx 前台/6xx Token, 共 39 个枚举值)** |

> **ResultCode 使用方式**: 业务层调用 `ResultCode.ADMIN_ARTICLE_NOT_FOUND.toException()` 抛业务异常, 每个枚举值对应一种明确的业务场景, 严格遵循 API_SPEC 25 节。

### 9.6 接口总数统计(对齐 API_SPEC)

| 模块 | 接口数 |
|---|---|
| 认证(M2) | 5 |
| 文章+分类+标签(M3) | 7+4+4 = 15 |
| 评论(M4) | 3 |
| 友链(M5) | 6 |
| 相册+照片(M6) | 4+2 = 6 |
| 说说(M7) | 4 |
| 公告+一言(M8) | 4+4 = 8 |
| 关于我+日程(M9) | 2+4 = 6 |
| 站点配置(M10) | 2 |
| 仪表盘(M11) | 2 |
| 文件上传(M12) | 2 |
| 前台用户管理(M13) | 5 |
| **合计** | **64 个接口** |

---

**文档结束**

本任务书 v1.3 基于 API_SPEC.md v1.2.0 编写,所有接口路径、Header、状态码严格对齐。实施过程中如遇架构调整或需求变更,请更新本文档版本号并记录变更原因。

**变更记录**:
- v1.0 (2026-08-25): 初版,基于代码扫描规划
- v1.1 (2026-08-25): 对齐 API_SPEC v1.1.0,补全 M8/M9 后台接口,修正路径前缀为 `/xuyuan/admin`,Header 改为 `Authorization: Bearer`,合并审核接口,删除多余接口
- v1.2 (2026-08-25): ResultCode 改为三位数业务码风格(意图 ①),按段划分(2xx 通用/3xx 后台/5xx 前台/6xx Token),共 39 个枚举值。HTTP 响应行统一 200,业务结果由 R.code 表达。GlobalExceptionHandler 同步改用 ResultCode 业务码
- v1.3 (2026-08-25): 新增 M13 前台用户管理模块(5 个接口),配套新增 t_app_user 表、AppUser 实体、AppUserAdminVO + 3 个 DTO、7 个 APP 段业务码(507-513)。对齐 API_SPEC v1.2.0(新增第 23 节前台用户认证、第 24 节后台-前台用户管理)。接口总数 59 → 64
