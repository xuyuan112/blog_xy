# 个人全栈博客系统 API 接口规范文档 (API SPEC)

> 版本：v1.2.0
> 基础路径（Base URL）：
> - 前台（blog-web）：`https://xuyuan.example.com/xuyuan`
> - 后台（blog-admin）：`https://xuyuan.example.com/xuyuan/admin`
> 数据格式：`application/json; charset=utf-8`
> 规范：RESTful API（遵循 HTTP 语义：GET 查询 / POST 新增 / PUT 全量更新 / DELETE 删除）

---

## 目录

1. [全局约定](#1-全局约定)
   - [1.1 统一响应数据结构](#11-统一响应数据结构)
   - [1.2 公共请求 Header](#12-公共请求-header)
   - [1.3 通用分页模型](#13-通用分页模型)
   - [1.4 通用枚举与状态码](#14-通用枚举与状态码)
   - [1.5 命名与时间约定](#15-命名与时间约定)
2. [认证模块 Auth](#2-认证模块-auth)
3. [文章模块 Article（前台）](#3-文章模块-article前台)
4. [归档模块 Archive（分类 / 标签 / 时间线）](#4-归档模块-archive)
5. [说说模块 Moment](#5-说说模块-moment)
6. [照片墙模块 Gallery](#6-照片墙模块-gallery)
7. [杂谈模块 Thought](#7-杂谈模块-thought)
8. [友情链接模块 Friend](#8-友情链接模块-friend)
9. [关于我模块 About](#9-关于我模块-about)
10. [评论模块 Comment](#10-评论模块-comment)
11. [点赞模块 Like](#11-点赞模块-like)
12. [站点公共服务模块](#12-站点公共服务模块)
13. [后台-文章管理](#13-后台-文章管理)
14. [后台-分类 / 标签管理](#14-后台-分类--标签管理)
15. [后台-说说管理](#15-后台-说说管理)
16. [后台-相册 / 照片管理](#16-后台-相册--照片管理)
17. [后台-友链管理](#17-后台-友链管理)
18. [后台-评论管理](#18-后台-评论管理)
19. [后台-文件上传](#19-后台-文件上传)
20. [后台-系统配置与监控统计](#20-后台-系统配置与监控统计)
21. [后台-公告/一言管理](#21-后台-公告--一言管理)
22. [后台-关于我/日程管理](#22-后台-关于我--日程管理)
23. [前台用户认证模块](#23-前台用户认证模块)
24. [后台-前台用户管理](#24-后台-前台用户管理)
25. [状态码与错误码规范](#25-状态码与错误码规范)

---

## 1. 全局约定

### 1.1 统一响应数据结构

所有接口（无论成功或失败）均返回统一的 JSON 结构。`code` 采用**三位数业务码**：`200` 表示成功，失败时返回对应业务段的三位数状态码（2xx 通用 / 3xx 后台 / 5xx 前台 / 6xx Token）。

| 字段      | 类型     | 说明                                                                                   |
| --------- | -------- | -------------------------------------------------------------------------------------- |
| `code`    | integer  | 三位数业务码，`200` 表示成功；`202`/`301`/`305`/`501`/`601` 等表示失败（详见 [25 状态码规范](#25-状态码与错误码规范)） |
| `message` | string   | 提示信息，成功时为 `"success"`，失败时为具体错误描述（可直接用于前端提示）             |
| `data`    | T        | 业务数据，可为对象、数组或 `null`；失败时恒为 `null`                                   |

> 说明：**HTTP 响应行统一返回 `200 OK`**，业务结果由 `R.code` 字段表达。前端按 `code` 精确判断业务类型，按 `message` 进行用户提示。详细状态码定义见 [25 状态码与错误码规范](#25-状态码与错误码规范)。

**Java 定义（blog-common）**

```java
public class R<T> implements Serializable {
    private Integer code;      // 三位数业务码
    private String message;
    private T data;

    public static <T> R<T> ok() { return build(200, "success", null); }
    public static <T> R<T> ok(T data) { return build(200, "success", data); }
    public static <T> R<T> fail(int code, String message) { return build(code, message, null); }

    public static <T> R<T> build(int code, String message, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(data);
        return r;
    }
    // getter / setter 省略
}
```

**TypeScript 定义（前端）**

```typescript
export interface ApiResult<T = unknown> {
  code: number;   // 200 表示成功，其余业务码表示失败
  message: string;
  data: T;
}
```

**成功响应示例（200 OK）**

```json
{
  "code": 200,
  "message": "success",
  "data": { "id": 1, "title": "Spring Boot 3 实战" }
}
```

**失败响应示例（307 用户名或密码错误）**

```json
{
  "code": 307,
  "message": "用户名或密码错误",
  "data": null
}
```

### 1.2 公共请求 Header

| Header             | 是否必填 | 说明                                                                 |
| ------------------ | -------- | -------------------------------------------------------------------- |
| `Authorization`    | 条件必填 | 认证令牌，格式 `Bearer <accessToken>`，需要登录态的接口必填          |
| `X-Client-Type`    | 否       | 客户端标识：`web`（前台）/ `admin`（后台），用于区分流量与埋点       |
| `X-Request-Id`     | 否       | 请求追踪 ID，用于链路日志串联，缺失时由网关自动生成                  |
| `Content-Type`     | 是       | `application/json`（文件上传使用 `multipart/form-data`）             |
| `Accept-Language`  | 否       | 国际化语言，默认 `zh-CN`                                             |

> 说明：AccessToken 有效期建议 30 分钟；RefreshToken 有效期 7 天，仅用于调用刷新接口，不参与业务鉴权。

### 1.3 通用分页模型

**请求（Query 参数）**

| 参数       | 类型    | 必填 | 默认 | 说明             |
| ---------- | ------- | ---- | ---- | ---------------- |
| `page`     | integer | 否   | 1    | 页码，从 1 开始  |
| `pageSize` | integer | 否   | 10   | 每页条数，最大 100 |

```typescript
export interface PageQuery {
  page?: number;
  pageSize?: number;
}
```

**响应（data 内统一分页结构）**

| 字段       | 类型      | 说明           |
| ---------- | --------- | -------------- |
| `records`  | T[]       | 当前页数据     |
| `total`    | integer   | 总记录数       |
| `page`     | integer   | 当前页码       |
| `pageSize` | integer   | 每页条数       |
| `pages`    | integer   | 总页数         |

```java
public class PageResult<T> implements Serializable {
    private List<T> records;
    private long total;
    private long page;
    private long pageSize;
    private long pages;
    // getter / setter 省略
}
```

```typescript
export interface PageResult<T> {
  records: T[];
  total: number;
  page: number;
  pageSize: number;
  pages: number;
}
```

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 120,
    "page": 1,
    "pageSize": 10,
    "pages": 12
  }
}
```

### 1.4 通用枚举与状态码

**文章状态 `ArticleStatus`**

| 值   | 说明   |
| ---- | ------ |
| 0    | 草稿   |
| 1    | 已发布 |
| 2    | 私密   |
| 3    | 回收站 |

**内容类型 `ContentType`（区分技术文章 / 杂谈）**

| 值   | 说明     |
| ---- | -------- |
| 1    | 技术文章 |
| 2    | 人文杂谈 |

**友链审核状态 `FriendStatus`**

| 值   | 说明   |
| ---- | ------ |
| 0    | 待审核 |
| 1    | 已通过 |
| 2    | 已拒绝 |

**评论状态 `CommentStatus`**

| 值   | 说明     |
| ---- | -------- |
| 0    | 待审核   |
| 1    | 已发布   |
| 2    | 已隐藏   |

### 1.5 命名与时间约定

- URL 统一使用复数名词 + 小写 + 连字符（`kebab-case`）；避免动词，动作通过 HTTP 方法表达。
- 字段命名统一 `camelCase`（Java DTO 使用 `@JsonProperty` 或 Jackson 默认驼峰映射）。
- 时间字段统一使用 ISO-8601 字符串 `yyyy-MM-dd HH:mm:ss`，由 Jackson `ObjectMapper` 全局序列化。
- 布尔字段使用 `boolean`，避免 `isXxx` 命名歧义。
- 列表查询返回 `PageResult<T>`；单条查询返回对象；无返回值的操作返回 `data: null`。

---

## 2. 认证模块 Auth

> 前缀：`/xuyuan/auth`

### 2.1 获取图形验证码

- **URL**：`GET /xuyuan/auth/captcha`
- **说明**：获取后台登录用图形验证码，同时下发 `captchaKey` 用于校验。

**请求参数**：无

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "captchaKey": "e3b0c44298fc1c149afbf4c8996fb924",
    "captchaImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  }
}
```

**TypeScript / Java 类型**

```typescript
export interface CaptchaVO {
  captchaKey: string;
  captchaImage: string; // base64 图片
}
```

```java
public class CaptchaVO implements Serializable {
    private String captchaKey;
    private String captchaImage;
}
```

---

### 2.2 登录

- **URL**：`POST /xuyuan/auth/login`
- **说明**：账号密码 + 验证码登录，返回双 Token。

**请求 Body**

| 参数         | 类型   | 必填 | 说明                          |
| ------------ | ------ | ---- | ----------------------------- |
| `username`   | string | 是   | 用户名                        |
| `password`   | string | 是   | 密码（建议前端 RSA 加密传输） |
| `captchaKey`  | string | 是   | 验证码 Key（来自 2.1）        |
| `captchaCode` | string | 是   | 验证码内容                    |

```typescript
export interface LoginParam {
  username: string;
  password: string;
  captchaKey: string;
  captchaCode: string;
}
```

```java
public class LoginParam {
    @NotBlank private String username;
    @NotBlank private String password;
    @NotBlank private String captchaKey;
    @NotBlank private String captchaCode;
}
```

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 1800,
    "tokenType": "Bearer",
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "nickname": "站长",
      "avatar": "https://minio.example.com/avatar.png"
    }
  }
}
```

```typescript
export interface LoginVO {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: 'Bearer';
  userInfo: UserInfoVO;
}
```

**异常响应示例**

```json
{ "code": 400, "message": "验证码错误或已过期", "data": null }
```

```json
{ "code": 401, "message": "用户名或密码错误", "data": null }
```

---

### 2.3 刷新 Token

- **URL**：`POST /xuyuan/auth/refresh`
- **说明**：使用 RefreshToken 换取新的 AccessToken（可返回单 Token 或双 Token）。

**请求 Body**

```typescript
export interface RefreshParam {
  refreshToken: string;
}
```

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 1800,
    "tokenType": "Bearer"
  }
}
```

**异常响应示例**

```json
{ "code": 401, "message": "RefreshToken 已过期，请重新登录", "data": null }
```

---

### 2.4 登出

- **URL**：`POST /xuyuan/auth/logout`
- **Header**：`Authorization: Bearer <accessToken>`
- **说明**：使当前 Token 失效（服务端加入黑名单或前端清理）。

**成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

---

### 2.5 获取当前用户信息

- **URL**：`GET /xuyuan/auth/userinfo`
- **Header**：`Authorization: Bearer <accessToken>`

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "nickname": "站长",
    "avatar": "https://minio.example.com/avatar.png",
    "roles": ["ROLE_ADMIN"]
  }
}
```

---

## 3. 文章模块 Article（前台）

> 前缀：`/xuyuan/articles`
> 说明：本模块同时承载首页技术文章与杂谈（通过 `type` 区分，见 [1.4 内容类型](#14-通用枚举与状态码)）。

### 3.1 文章分页列表

- **URL**：`GET /xuyuan/articles`
- **说明**：时间倒序，支持分页/滚动加载、分类/标签/关键词筛选。

**请求 Query**

| 参数         | 类型    | 必填 | 说明                                  |
| ------------ | ------- | ---- | ------------------------------------- |
| `page`       | integer | 否   | 页码，默认 1                          |
| `pageSize`   | integer | 否   | 每页条数，默认 10                     |
| `categoryId` | integer | 否   | 分类 ID 筛选                          |
| `tagId`      | integer | 否   | 标签 ID 筛选                          |
| `keyword`    | string  | 否   | 标题/摘要模糊搜索                     |
| `type`       | integer | 否   | 1=技术文章，2=杂谈，不传则返回全部    |

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 10,
        "title": "Spring Boot 3 实战入门",
        "cover": "https://minio.example.com/cover/10.jpg",
        "summary": "从零开始搭建 Spring Boot 3 项目...",
        "categoryId": 1,
        "categoryName": "后端",
        "tags": [{ "id": 3, "name": "Spring Boot" }, { "id": 4, "name": "Java" }],
        "viewCount": 1024,
        "commentCount": 12,
        "likeCount": 88,
        "type": 1,
        "isTop": true,
        "publishedAt": "2026-08-20 10:30:00"
      }
    ],
    "total": 120,
    "page": 1,
    "pageSize": 10,
    "pages": 12
  }
}
```

```typescript
export interface ArticleListItemVO {
  id: number;
  title: string;
  cover: string;
  summary: string;
  categoryId: number;
  categoryName: string;
  tags: TagVO[];
  viewCount: number;
  commentCount: number;
  likeCount: number;
  type: number;      // 1 技术 / 2 杂谈
  isTop: boolean;
  publishedAt: string;
}
```

---

### 3.2 文章详情

- **URL**：`GET /xuyuan/articles/{id}`
- **说明**：返回 Markdown 原文与解析后的 TOC 目录树；访问自动累加阅读量（配合 Redis 防刷）。

**请求 Path**

| 参数 | 类型    | 必填 | 说明    |
| ---- | ------- | ---- | ------- |
| `id` | integer | 是   | 文章 ID |

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "title": "Spring Boot 3 实战入门",
    "cover": "https://minio.example.com/cover/10.jpg",
    "content": "# 标题\n正文 Markdown...",
    "categoryId": 1,
    "categoryName": "后端",
    "tags": [{ "id": 3, "name": "Spring Boot" }],
    "toc": [
      { "level": 1, "text": "标题", "anchor": "标题" },
      { "level": 2, "text": "小节", "anchor": "小节" }
    ],
    "viewCount": 1025,
    "likeCount": 88,
    "commentCount": 12,
    "isTop": true,
    "type": 1,
    "prevArticle": { "id": 9, "title": "上一篇文章" },
    "nextArticle": { "id": 11, "title": "下一篇文章" },
    "publishedAt": "2026-08-20 10:30:00",
    "updatedAt": "2026-08-21 09:00:00"
  }
}
```

```typescript
export interface ArticleDetailVO {
  id: number;
  title: string;
  cover: string;
  content: string;
  categoryId: number;
  categoryName: string;
  tags: TagVO[];
  toc: TocItem[];
  viewCount: number;
  likeCount: number;
  commentCount: number;
  isTop: boolean;
  type: number;
  prevArticle?: ArticleNavVO | null;
  nextArticle?: ArticleNavVO | null;
  publishedAt: string;
  updatedAt: string;
}

export interface TocItem { level: number; text: string; anchor: string; }
export interface ArticleNavVO { id: number; title: string; }
```

**异常响应示例**

```json
{ "code": 404, "message": "文章不存在或已被删除", "data": null }
```

```json
{ "code": 403, "message": "该文章为私密文章，无权访问", "data": null }
```

---

### 3.3 热门文章 Top N

- **URL**：`GET /xuyuan/articles/hot`
- **说明**：左侧边栏热门榜单，基于 Redis ZSet 权重。

**请求 Query**

| 参数    | 类型    | 必填 | 默认 | 说明 |
| ------- | ------- | ---- | ---- | ---- |
| `limit` | integer | 否   | 5    | 条数 |

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 10, "title": "Spring Boot 3 实战入门", "viewCount": 1025 }
  ]
}
```

---

## 4. 归档模块 Archive

> 前缀：`/xuyuan/categories`、`/xuyuan/tags`、`/xuyuan/archives`

### 4.1 分类列表

- **URL**：`GET /xuyuan/categories`

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 1, "name": "后端", "description": "服务端技术", "articleCount": 45 }
  ]
}
```

```typescript
export interface CategoryVO { id: number; name: string; description?: string; articleCount: number; }
```

### 4.2 标签列表

- **URL**：`GET /xuyuan/tags`
- **说明**：按文章密集度返回权重，前端据此渲染字号/颜色。

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 3, "name": "Spring Boot", "weight": 98, "articleCount": 32 }
  ]
}
```

```typescript
export interface TagVO { id: number; name: string; weight?: number; articleCount: number; }
```

### 4.3 时间线归档

- **URL**：`GET /xuyuan/archives/timeline`

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "year": "2026",
      "months": [
        {
          "month": "08",
          "articles": [
            { "id": 10, "title": "Spring Boot 3 实战入门", "publishedAt": "2026-08-20 10:30:00" }
          ]
        }
      ]
    }
  ]
}
```

```typescript
export interface TimelineYearVO {
  year: string;
  months: TimelineMonthVO[];
}
export interface TimelineMonthVO {
  month: string;
  articles: ArticleNavVO[];
}
```

---

## 5. 说说模块 Moment

> 前缀：`/xuyuan/moments`

### 5.1 说说分页列表

- **URL**：`GET /xuyuan/moments`

**请求 Query**

| 参数       | 类型    | 必填 | 说明     |
| ---------- | ------- | ---- | -------- |
| `page`     | integer | 否   | 页码     |
| `pageSize` | integer | 否   | 每页条数 |

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 5,
        "content": "今天学习了分布式事务",
        "images": [
          "https://minio.example.com/moment/1.jpg",
          "https://minio.example.com/moment/2.jpg"
        ],
        "location": "杭州",
        "mood": "开心",
        "likeCount": 32,
        "commentCount": 5,
        "createdAt": "2026-08-20 18:00:00"
      }
    ],
    "total": 30,
    "page": 1,
    "pageSize": 10,
    "pages": 3
  }
}
```

```typescript
export interface MomentVO {
  id: number;
  content: string;
  images: string[];
  location?: string;
  mood?: string;
  likeCount: number;
  commentCount: number;
  createdAt: string;
}
```

### 5.2 说说详情

- **URL**：`GET /xuyuan/moments/{id}`

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 5,
    "content": "今天学习了分布式事务",
    "images": ["https://minio.example.com/moment/1.jpg"],
    "location": "杭州",
    "mood": "开心",
    "likeCount": 32,
    "commentCount": 5,
    "createdAt": "2026-08-20 18:00:00"
  }
}
```

**异常响应示例**

```json
{ "code": 404, "message": "说说不存在", "data": null }
```

---

## 6. 照片墙模块 Gallery

> 前缀：`/xuyuan/galleries`、`/xuyuan/photos`

### 6.1 相册列表（含搜索）

- **URL**：`GET /xuyuan/galleries`

**请求 Query**

| 参数       | 类型    | 必填 | 说明             |
| ---------- | ------- | ---- | ---------------- |
| `page`     | integer | 否   | 页码             |
| `pageSize` | integer | 否   | 每页条数         |
| `keyword`  | string  | 否   | 相册名/描述筛选  |

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 2,
        "name": "2026 夏日旅行",
        "description": "海边与山野",
        "cover": "https://minio.example.com/album/2/cover.jpg",
        "photoCount": 18,
        "shootDate": "2026-07-15",
        "createdAt": "2026-07-16 12:00:00"
      }
    ],
    "total": 8,
    "page": 1,
    "pageSize": 12,
    "pages": 1
  }
}
```

```typescript
export interface GalleryVO {
  id: number;
  name: string;
  description?: string;
  cover: string;
  photoCount: number;
  shootDate?: string;
  createdAt: string;
}
```

### 6.2 相册详情（含照片列表）

- **URL**：`GET /xuyuan/galleries/{id}`

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2,
    "name": "2026 夏日旅行",
    "description": "海边与山野",
    "shootDate": "2026-07-15",
    "photos": [
      {
        "id": 101,
        "thumbnail": "https://minio.example.com/album/2/thumb/1.jpg",
        "original": "https://minio.example.com/album/2/1.jpg",
        "shootTime": "2026-07-15 14:20:00"
      }
    ]
  }
}
```

### 6.3 照片详情（含 EXIF）

- **URL**：`GET /xuyuan/photos/{id}`

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 101,
    "galleryId": 2,
    "original": "https://minio.example.com/album/2/1.jpg",
    "thumbnail": "https://minio.example.com/album/2/thumb/1.jpg",
    "shootTime": "2026-07-15 14:20:00",
    "exif": {
      "cameraModel": "Sony A7M4",
      "iso": 100,
      "shutterSpeed": "1/250",
      "aperture": "f/2.8",
      "focalLength": "35mm",
      "lensModel": "FE 35mm F1.8"
    }
  }
}
```

```typescript
export interface PhotoVO {
  id: number;
  galleryId: number;
  original: string;
  thumbnail: string;
  shootTime?: string;
  exif?: ExifVO;
}
export interface ExifVO {
  cameraModel?: string;
  iso?: number;
  shutterSpeed?: string;
  aperture?: string;
  focalLength?: string;
  lensModel?: string;
}
```

---

## 7. 杂谈模块 Thought

> 前缀：`/xuyuan/thoughts`

### 7.1 杂谈分页列表

- **URL**：`GET /xuyuan/thoughts`
- **说明**：复用文章表，`type = 2`，排版风格与首页技术文章区分。

**请求 Query**：同 [3.1 文章列表](#31-文章分页列表)（`type` 固定为 2）

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      { "id": 20, "title": "读《人类简史》有感", "cover": "...", "summary": "...", "viewCount": 500, "publishedAt": "2026-08-10 09:00:00" }
    ],
    "total": 15,
    "page": 1,
    "pageSize": 10,
    "pages": 2
  }
}
```

### 7.2 杂谈详情

- **URL**：`GET /xuyuan/thoughts/{id}`
- **说明**：复用文章详情结构，前端进入沉浸式阅读模式（字号/护眼/羊皮纸背景为纯前端能力）。

**成功响应**：结构同 [3.2 文章详情](#32-文章详情)。

---

## 8. 友情链接模块 Friend

> 前缀：`/xuyuan/friends`

### 8.1 友链列表

- **URL**：`GET /xuyuan/friends`
- **说明**：返回已通过审核的友链，含分组信息。

**请求 Query**

| 参数    | 类型    | 必填 | 说明                   |
| ------- | ------- | ---- | ---------------------- |
| `group` | string  | 否   | 分组名筛选，不传返回全部 |

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 12,
    "groups": [
      {
        "name": "我的伙伴们",
        "friends": [
          { "id": 1, "name": "张三的博客", "avatar": "...", "description": "前端工程师", "url": "https://zhangsan.dev" }
        ]
      },
      {
        "name": "其它链接",
        "friends": [
          { "id": 2, "name": "开源中国", "avatar": "...", "description": "开源社区", "url": "https://oschina.net" }
        ]
      }
    ]
  }
}
```

```typescript
export interface FriendVO {
  id: number;
  name: string;
  avatar: string;
  description?: string;
  url: string;
}
export interface FriendGroupVO {
  name: string;
  friends: FriendVO[];
}
export interface FriendListVO {
  total: number;
  groups: FriendGroupVO[];
}
```

### 8.2 随机访问友链

- **URL**：`GET /xuyuan/friends/random`
- **说明**：返回一条随机友链，前端跳转。

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": { "id": 5, "name": "李四的空间", "url": "https://lisi.dev" }
}
```

### 8.3 提交友链申请

- **URL**：`POST /xuyuan/friends/apply`
- **说明**：访客在线申请，提交后进入待审核状态。

**请求 Body**

```typescript
export interface FriendApplyParam {
  name: string;
  url: string;
  avatar?: string;
  description?: string;
  email: string;      // 用于通知
  group?: string;
}
```

**成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

**异常响应示例**

```json
{ "code": 409, "message": "该站点已在申请或已存在", "data": null }
```

---

## 9. 关于我模块 About

> 前缀：`/xuyuan/about`

### 9.1 获取关于我信息

- **URL**：`GET /xuyuan/about`

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "nickname": "站长",
    "avatar": "https://minio.example.com/avatar.png",
    "signature": "用代码改变世界",
    "intro": "一名热爱技术的全栈开发者...",
    "skills": [
      { "name": "Java", "level": 90, "category": "后端" },
      { "name": "Vue", "level": 85, "category": "前端" }
    ],
    "milestones": [
      { "year": "2020", "title": "入职某公司", "description": "..." }
    ]
  }
}
```

```typescript
export interface AboutVO {
  nickname: string;
  avatar: string;
  signature?: string;
  intro: string;
  skills: SkillVO[];
  milestones: MilestoneVO[];
}
export interface SkillVO { name: string; level: number; category: string; }
export interface MilestoneVO { year: string; title: string; description?: string; }
```

---

## 10. 评论模块 Comment

> 前缀：`/xuyuan/comments`

### 10.1 评论列表（单层）

- **URL**：`GET /xuyuan/comments`
- **说明**：说说与文章共用，通过 `targetType` + `targetId` 定位。

**请求 Query**

| 参数         | 类型    | 必填 | 说明                          |
| ------------ | ------- | ---- | ----------------------------- |
| `targetType` | integer | 是   | 1=文章，2=说说               |
| `targetId`   | integer | 是   | 目标 ID                       |
| `page`       | integer | 否   | 页码                          |
| `pageSize`   | integer | 否   | 每页条数                      |

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "targetType": 1,
        "targetId": 10,
        "content": "写得很好，收藏了",
        "nickname": "访客A",
        "avatar": "https://minio.example.com/comment/1.png",
        "createdAt": "2026-08-20 20:00:00"
      }
    ],
    "total": 12,
    "page": 1,
    "pageSize": 10,
    "pages": 2
  }
}
```

```typescript
export interface CommentVO {
  id: number;
  targetType: number;
  targetId: number;
  content: string;
  nickname: string;
  avatar?: string;
  createdAt: string;
}
```

### 10.2 发表评论

- **URL**：`POST /xuyuan/comments`

**请求 Body**

```typescript
export interface CommentCreateParam {
  targetType: number;   // 1 文章 / 2 说说
  targetId: number;
  content: string;
  nickname: string;
  email?: string;
  website?: string;
}
```

**成功响应**

```json
{ "code": 200, "message": "success", "data": { "id": 13 } }
```

**异常响应示例**

```json
{ "code": 400, "message": "评论内容不能为空", "data": null }
```

---

## 11. 点赞模块 Like

> 前缀：`/xuyuan/likes`

### 11.1 点赞 / 取消点赞

- **URL**：`POST /xuyuan/likes/toggle`
- **说明**：防刷点赞（结合 Redis 计数 + IP 限流），重复调用在点赞/取消间切换。

**请求 Body**

```typescript
export interface LikeToggleParam {
  targetType: number;   // 1 文章 / 2 说说
  targetId: number;
}
```

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": { "targetId": 10, "liked": true, "likeCount": 89 }
}
```

```typescript
export interface LikeToggleVO {
  targetId: number;
  liked: boolean;
  likeCount: number;
}
```

---

## 12. 站点公共服务模块

> 前缀：`/xuyuan/site`

### 12.1 站长公告

- **URL**：`GET /xuyuan/site/notice`

```json
{ "code": 200, "message": "success", "data": { "id": 1, "content": "欢迎访问我的博客", "updatedAt": "2026-08-01 00:00:00" } }
```

### 12.2 一言（每日金句）

- **URL**：`GET /xuyuan/site/words/random`

```json
{ "code": 200, "message": "success", "data": { "content": "路漫漫其修远兮，吾将上下而求索。", "source": "《离骚》" } }
```

### 12.3 天气小组件

- **URL**：`GET /xuyuan/site/weather`
- **说明**：代理第三方天气接口，或返回缓存的实时天气。

```json
{ "code": 200, "message": "success", "data": { "city": "杭州", "temp": "28", "weather": "多云", "icon": "cloudy" } }
```

### 12.4 日程 / 倒计时事件

- **URL**：`GET /xuyuan/site/schedules`

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 1, "title": "博客上线一周年", "targetTime": "2026-10-01 00:00:00", "type": "countdown" }
  ]
}
```

---

## 13. 后台-文章管理

> 前缀：`/xuyuan/admin/articles`
> 鉴权：需要 `Authorization`，以下接口均需管理员登录态。

### 13.1 分页查询（含草稿/全部状态）

- **URL**：`GET /xuyuan/admin/articles`

**请求 Query**

| 参数         | 类型    | 必填 | 说明                  |
| ------------ | ------- | ---- | --------------------- |
| `page`       | integer | 否   | 页码                  |
| `pageSize`   | integer | 否   | 每页条数              |
| `status`     | integer | 否   | 文章状态（见 1.4）    |
| `type`       | integer | 否   | 1 技术 / 2 杂谈       |
| `categoryId` | integer | 否   | 分类 ID               |
| `keyword`    | string  | 否   | 标题搜索              |

**成功响应**：`data` 为 `PageResult<ArticleAdminVO>`，其中 `ArticleAdminVO` 在 `ArticleListItemVO` 基础上增加 `status`、`isPrivate` 等字段。

### 13.2 新增文章

- **URL**：`POST /xuyuan/admin/articles`

**请求 Body**

```typescript
export interface ArticleCreateParam {
  title: string;
  cover?: string;
  summary?: string;
  content: string;         // Markdown 原文
  categoryId?: number;
  tagIds?: number[];
  type: number;            // 1 技术 / 2 杂谈
  status: number;          // 0 草稿 / 1 发布 / 2 私密
  isTop?: boolean;
}
```

**成功响应**

```json
{ "code": 200, "message": "success", "data": { "id": 11 } }
```

### 13.3 编辑文章

- **URL**：`PUT /xuyuan/admin/articles/{id}`
- **请求 Body**：同 `ArticleCreateParam`（全量更新）。
- **成功响应**：`{ "code": 200, "message": "success", "data": null }`

### 13.4 文章详情

- **URL**：`GET /xuyuan/admin/articles/{id}`
- **成功响应**：`data` 为 `ArticleDetailVO`（含 `tagIds` 便于回显）。

### 13.5 删除文章（可批量）

- **URL**：`DELETE /xuyuan/admin/articles`
- **说明**：支持批量，逻辑删除进回收站。

**请求 Body**

```typescript
export interface IdsParam { ids: number[]; }
```

**成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

### 13.6 变更文章状态

- **URL**：`PUT /xuyuan/admin/articles/{id}/status`

**请求 Body**

```typescript
export interface ArticleStatusParam { status: number; isTop?: boolean; }
```

**成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

### 13.7 保存草稿（自动保存）

- **URL**：`POST /xuyuan/admin/articles/draft`
- **说明**：编辑器定时自动保存；已存在 `id` 则更新，否则新建草稿。

**请求 Body**：同 `ArticleCreateParam`（`status` 固定 0）。

**成功响应**

```json
{ "code": 200, "message": "success", "data": { "id": 11 } }
```

---

## 14. 后台-分类 / 标签管理

> 前缀：`/xuyuan/admin/categories`、`/xuyuan/admin/tags`

### 14.1 分类列表 / 新增 / 编辑 / 删除

| 方法   | URL                              | 说明                     | 请求体                              |
| ------ | -------------------------------- | ------------------------ | ----------------------------------- |
| GET    | `/xuyuan/admin/categories`          | 分页/全量分类列表         | Query：`page`、`pageSize`、`keyword` |
| POST   | `/xuyuan/admin/categories`          | 新增分类                 | `CategoryCreateParam`                |
| PUT    | `/xuyuan/admin/categories/{id}`     | 编辑分类                 | `CategoryCreateParam`                |
| DELETE | `/xuyuan/admin/categories/{id}`     | 删除分类（校验无文章）   | —                                    |

```typescript
export interface CategoryCreateParam {
  name: string;
  description?: string;
  sort?: number;
}
```

### 14.2 标签列表 / 新增 / 编辑 / 删除

| 方法   | URL                          | 说明             | 请求体           |
| ------ | ---------------------------- | ---------------- | ---------------- |
| GET    | `/xuyuan/admin/tags`            | 分页/全量标签    | Query：`page`、`pageSize`、`keyword` |
| POST   | `/xuyuan/admin/tags`            | 新增标签         | `TagCreateParam` |
| PUT    | `/xuyuan/admin/tags/{id}`       | 编辑标签         | `TagCreateParam` |
| DELETE | `/xuyuan/admin/tags/{id}`       | 删除标签         | —                 |

```typescript
export interface TagCreateParam { name: string; color?: string; }
```

---

## 15. 后台-说说管理

> 前缀：`/xuyuan/admin/moments`

| 方法   | URL                         | 说明                              | 请求体             |
| ------ | --------------------------- | --------------------------------- | ------------------ |
| GET    | `/xuyuan/admin/moments`        | 分页列表（含隐藏/私密）           | Query：`page`、`pageSize` |
| POST   | `/xuyuan/admin/moments`        | 发布说说（文本 + 最多 9 图）      | `MomentCreateParam` |
| PUT    | `/xuyuan/admin/moments/{id}`   | 编辑说说                          | `MomentCreateParam` |
| DELETE | `/xuyuan/admin/moments/{id}`   | 删除说说                          | —                   |

```typescript
export interface MomentCreateParam {
  content: string;
  images?: string[];   // 最多 9 张
  location?: string;
  mood?: string;
  visible?: number;    // 0 公开 / 1 隐藏 / 2 私密
}
```

**成功响应**

```json
{ "code": 200, "message": "success", "data": { "id": 6 } }
```

---

## 16. 后台-相册 / 照片管理

> 前缀：`/xuyuan/admin/galleries`、`/xuyuan/admin/photos`

### 16.1 相册 CRUD

| 方法   | URL                            | 说明       | 请求体               |
| ------ | ------------------------------ | ---------- | -------------------- |
| GET    | `/xuyuan/admin/galleries`         | 分页列表   | Query：`page`、`pageSize`、`keyword` |
| POST   | `/xuyuan/admin/galleries`         | 新建相册   | `GalleryCreateParam` |
| PUT    | `/xuyuan/admin/galleries/{id}`    | 编辑相册   | `GalleryCreateParam` |
| DELETE | `/xuyuan/admin/galleries/{id}`    | 删除相册   | —                     |

```typescript
export interface GalleryCreateParam {
  name: string;
  description?: string;
  cover?: string;
  shootDate?: string;
}
```

### 16.2 批量上传照片

- **URL**：`POST /xuyuan/admin/galleries/{id}/photos`
- **说明**：`multipart/form-data` 上传原图，后端异步生成缩略图并解析 EXIF。

**请求 Form-Data**

| 参数     | 类型 | 必填 | 说明                       |
| -------- | ---- | ---- | -------------------------- |
| `files`  | File[] | 是 | 多张原图，`Content-Type: image/*` |

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "galleryId": 2,
    "photos": [
      { "id": 101, "original": "https://minio.example.com/album/2/1.jpg", "thumbnail": "https://minio.example.com/album/2/thumb/1.jpg" }
    ]
  }
}
```

### 16.3 删除照片

| 方法   | URL                     | 说明           |
| ------ | ----------------------- | -------------- |
| DELETE | `/xuyuan/admin/photos/{id}` | 删除单张照片   |

**成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

---

## 17. 后台-友链管理

> 前缀：`/xuyuan/admin/friends`

| 方法   | URL                                | 说明                 | 请求体              |
| ------ | ---------------------------------- | -------------------- | ------------------- |
| GET    | `/xuyuan/admin/friends`               | 分页列表（含待审核） | Query：`page`、`pageSize`、`status` |
| POST   | `/xuyuan/admin/friends`               | 新增友链             | `FriendCreateParam` |
| PUT    | `/xuyuan/admin/friends/{id}`          | 编辑友链             | `FriendCreateParam` |
| DELETE | `/xuyuan/admin/friends/{id}`          | 删除友链             | —                    |
| PUT    | `/xuyuan/admin/friends/{id}/audit`    | 审核友链             | `FriendAuditParam`   |
| PUT    | `/xuyuan/admin/friends/{id}/sort`     | 调整排序             | `SortParam`          |

```typescript
export interface FriendCreateParam {
  name: string;
  url: string;
  avatar?: string;
  description?: string;
  group?: string;
  status?: number;   // 0 待审核 / 1 已通过 / 2 已拒绝
}
export interface FriendAuditParam { status: number; reason?: string; }
export interface SortParam { sort: number; }
```

**审核成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

---

## 18. 后台-评论管理

> 前缀：`/xuyuan/admin/comments`

| 方法   | URL                                   | 说明         | 请求体            |
| ------ | ------------------------------------- | ------------ | ----------------- |
| GET    | `/xuyuan/admin/comments`                 | 分页列表     | Query：`page`、`pageSize`、`targetType`、`status` |
| PUT    | `/xuyuan/admin/comments/{id}/status`     | 审核/隐藏    | `CommentStatusParam` |
| DELETE | `/xuyuan/admin/comments/{id}`            | 删除评论     | —                  |

```typescript
export interface CommentStatusParam { status: number; }
```

---

## 19. 后台-文件上传

> 前缀：`/xuyuan/admin/files`

### 19.1 通用文件上传（MinIO）

- **URL**：`POST /xuyuan/admin/files/upload`
- **Header**：`Content-Type: multipart/form-data`

**请求 Form-Data**

| 参数    | 类型 | 必填 | 说明                       |
| ------- | ---- | ---- | -------------------------- |
| `file`  | File | 是   | 待上传文件                 |
| `scene` | string | 否 | 场景：`article`/`avatar`/`moment`/`gallery`/`comment` |

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "url": "https://minio.example.com/article/2026/08/xxx.png",
    "fileName": "xxx.png",
    "size": 204800
  }
}
```

```typescript
export interface FileUploadVO { url: string; fileName: string; size: number; }
```

### 19.2 图片上传（文章编辑器拖拽 / 粘贴）

- **URL**：`POST /xuyuan/admin/files/upload/image`
- **说明**：与 19.1 一致，仅限图片类型（`jpg/png/gif/webp`），返回可直接嵌入 Markdown 的 URL。

**成功响应**

```json
{ "code": 200, "message": "success", "data": { "url": "https://minio.example.com/article/2026/08/yyy.png" } }
```

---

## 20. 后台-系统配置与监控统计

> 前缀：`/xuyuan/admin`

### 20.1 仪表盘统计

- **URL**：`GET /xuyuan/admin/dashboard/stats`

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "articleCount": 120,
    "momentCount": 30,
    "galleryCount": 8,
    "photoCount": 350,
    "friendCount": 12,
    "commentCount": 500,
    "totalViewCount": 204800,
    "pendingFriendCount": 2,
    "pendingCommentCount": 5
  }
}
```

```typescript
export interface DashboardStatsVO {
  articleCount: number;
  momentCount: number;
  galleryCount: number;
  photoCount: number;
  friendCount: number;
  commentCount: number;
  totalViewCount: number;
  pendingFriendCount: number;
  pendingCommentCount: number;
}
```

### 20.2 访问趋势

- **URL**：`GET /xuyuan/admin/dashboard/trend`
- **请求 Query**：`days`（近 N 天，默认 30）

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "date": "2026-08-20", "viewCount": 1200, "commentCount": 20 }
  ]
}
```

### 20.3 站点配置查询 / 更新

| 方法 | URL                  | 说明     | 请求体          |
| ---- | -------------------- | -------- | --------------- |
| GET  | `/xuyuan/admin/configs` | 查询配置 | —                |
| PUT  | `/xuyuan/admin/configs` | 更新配置 | `Map<string, string>` |

**成功响应（GET）**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "siteName": "我的博客",
    "siteDesc": "技术分享与生活记录",
    "icp": "浙ICP备XXXXXXXX号",
    "notice": "欢迎访问"
  }
}
```

---

## 21. 后台-公告 / 一言管理

> 前缀：`/xuyuan/admin/notices`、`/xuyuan/admin/words`
> 鉴权：需要 `Authorization`，以下接口均需管理员登录态。

### 21.1 公告 CRUD

| 方法   | URL                              | 说明           | 请求体               |
| ------ | -------------------------------- | -------------- | -------------------- |
| GET    | `/xuyuan/admin/notices`             | 分页列表       | Query：`page`、`pageSize` |
| POST   | `/xuyuan/admin/notices`             | 新增公告       | `NoticeCreateParam`  |
| PUT    | `/xuyuan/admin/notices/{id}`        | 编辑公告       | `NoticeCreateParam`  |
| DELETE | `/xuyuan/admin/notices/{id}`        | 删除公告       | —                    |

```typescript
export interface NoticeCreateParam {
  content: string;
}
```

**成功响应（GET 分页）**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      { "id": 1, "content": "欢迎访问我的博客", "createdAt": "2026-08-01 00:00:00", "updatedAt": "2026-08-01 00:00:00" }
    ],
    "total": 3,
    "page": 1,
    "pageSize": 10,
    "pages": 1
  }
}
```

```typescript
export interface NoticeVO {
  id: number;
  content: string;
  createdAt: string;
  updatedAt: string;
}
```

### 21.2 一言 CRUD

| 方法   | URL                          | 说明           | 请求体              |
| ------ | ---------------------------- | -------------- | ------------------- |
| GET    | `/xuyuan/admin/words`           | 分页列表       | Query：`page`、`pageSize` |
| POST   | `/xuyuan/admin/words`           | 新增一言       | `WordCreateParam`  |
| PUT    | `/xuyuan/admin/words/{id}`      | 编辑一言       | `WordCreateParam`   |
| DELETE | `/xuyuan/admin/words/{id}`      | 删除一言       | —                   |

```typescript
export interface WordCreateParam {
  content: string;
  source?: string;
}
```

**成功响应（GET 分页）**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      { "id": 1, "content": "路漫漫其修远兮，吾将上下而求索。", "source": "《离骚》", "createdAt": "2026-08-01 00:00:00" }
    ],
    "total": 20,
    "page": 1,
    "pageSize": 10,
    "pages": 2
  }
}
```

```typescript
export interface WordVO {
  id: number;
  content: string;
  source?: string;
  createdAt: string;
}
```

---

## 22. 后台-关于我 / 日程管理

> 前缀：`/xuyuan/admin/about`、`/xuyuan/admin/schedules`
> 鉴权：需要 `Authorization`，以下接口均需管理员登录态。

### 22.1 关于我（单行配置）

| 方法 | URL                    | 说明         | 请求体           |
| ---- | ---------------------- | ------------ | ---------------- |
| GET  | `/xuyuan/admin/about`     | 获取关于我   | —                |
| PUT  | `/xuyuan/admin/about`     | 更新关于我   | `AboutUpdateParam` |

**请求 Body（PUT）**

```typescript
export interface AboutUpdateParam {
  nickname?: string;
  avatar?: string;
  signature?: string;
  intro?: string;
  skills?: SkillVO[];        // [{name, level, category}]
  milestones?: MilestoneVO[]; // [{year, title, description}]
}
```

**成功响应（GET / PUT 返回）**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "nickname": "站长",
    "avatar": "https://minio.example.com/avatar.png",
    "signature": "用代码改变世界",
    "intro": "一名热爱技术的全栈开发者...",
    "skills": [
      { "name": "Java", "level": 90, "category": "后端" },
      { "name": "Vue", "level": 85, "category": "前端" }
    ],
    "milestones": [
      { "year": "2020", "title": "入职某公司", "description": "..." }
    ]
  }
}
```

### 22.2 日程 / 倒计时 CRUD

| 方法   | URL                              | 说明           | 请求体                 |
| ------ | -------------------------------- | -------------- | ---------------------- |
| GET    | `/xuyuan/admin/schedules`           | 分页列表       | Query：`page`、`pageSize`、`type` |
| POST   | `/xuyuan/admin/schedules`           | 新增日程       | `ScheduleCreateParam`  |
| PUT    | `/xuyuan/admin/schedules/{id}`      | 编辑日程       | `ScheduleCreateParam`  |
| DELETE | `/xuyuan/admin/schedules/{id}`      | 删除日程       | —                      |

```typescript
export interface ScheduleCreateParam {
  title: string;
  targetTime: string;        // yyyy-MM-dd HH:mm:ss
  type: 'countdown' | 'schedule';
}
```

**成功响应（GET 分页）**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      { "id": 1, "title": "博客上线一周年", "targetTime": "2026-10-01 00:00:00", "type": "countdown", "createdAt": "2026-08-01 00:00:00" }
    ],
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "pages": 1
  }
}
```

```typescript
export interface ScheduleVO {
  id: number;
  title: string;
  targetTime: string;
  type: 'countdown' | 'schedule';
  createdAt: string;
}
```

---

## 23. 前台用户认证模块

> 前缀：`/xuyuan/app/auth`
>
> 前台展示端用户注册/登录/登出/刷新Token/获取当前用户信息。与后台认证（第 2 节 `/xuyuan/auth/*`）独立分离，使用独立的 `t_app_user` 表，密码同样采用 MD5 加密。
>
> 与后台登录不同，**前台登录不使用图形验证码**，以提升用户体验；但保留登录失败限流能力。

### 23.1 用户注册

- **URL**：`POST /xuyuan/app/auth/register`
- **说明**：前台用户注册，注册成功后直接返回双 Token，无需再次登录。

**请求 Body**

| 参数       | 类型   | 必填 | 说明                                   |
| ---------- | ------ | ---- | -------------------------------------- |
| `username` | string | 是   | 用户名（3-32 字符，唯一）              |
| `password` | string | 是   | 密码明文（6-32 字符，后端 MD5 加密存储）|
| `nickname` | string | 是   | 昵称（最多 64 字符）                   |
| `email`    | string | 否   | 邮箱（最多 128 字符）                 |

```typescript
export interface RegisterParam {
  username: string;
  password: string;
  nickname: string;
  email?: string;
}
```

**成功响应**（同 23.2 登录响应结构 `AppLoginVO`）

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 1800,
    "tokenType": "Bearer",
    "userInfo": {
      "userId": 2,
      "username": "newuser",
      "nickname": "新用户",
      "avatar": null,
      "email": "newuser@example.com"
    }
  }
}
```

**异常响应示例**

```json
{ "code": 507, "message": "账号已存在", "data": null }
```

```json
{ "code": 202, "message": "用户名长度必须在 3-32 个字符之间", "data": null }
```

---

### 23.2 用户登录

- **URL**：`POST /xuyuan/app/auth/login`
- **说明**：用户名 + 密码登录，返回双 Token。不使用图形验证码。

**请求 Body**

| 参数       | 类型   | 必填 | 说明         |
| ---------- | ------ | ---- | ------------ |
| `username` | string | 是   | 用户名       |
| `password` | string | 是   | 密码（明文） |

```typescript
export interface AppLoginParam {
  username: string;
  password: string;
}
```

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 1800,
    "tokenType": "Bearer",
    "userInfo": {
      "userId": 1,
      "username": "demo",
      "nickname": "示例用户",
      "avatar": null,
      "email": "demo@example.com"
    }
  }
}
```

```typescript
export interface AppLoginVO {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: 'Bearer';
  userInfo: AppUserInfoVO;
}

export interface AppUserInfoVO {
  userId: number;
  username: string;
  nickname: string;
  avatar: string | null;
  email: string | null;
}
```

**异常响应示例**

```json
{ "code": 508, "message": "用户名或密码错误", "data": null }
```

```json
{ "code": 502, "message": "该用户已被禁用", "data": null }
```

> **设计说明**：登录场景下"账号不存在"和"密码错误"统一返回 `508 APP_ACCOUNT_ERROR`（防账号枚举）。

---

### 23.3 刷新 Token

- **URL**：`POST /xuyuan/app/auth/refresh`
- **说明**：使用 RefreshToken 换取新的 AccessToken。

**请求 Body**

```typescript
export interface AppRefreshParam {
  refreshToken: string;
}
```

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 1800,
    "tokenType": "Bearer"
  }
}
```

**异常响应示例**

```json
{ "code": 601, "message": "token过期", "data": null }
```

---

### 23.4 获取当前用户信息

- **URL**：`GET /xuyuan/app/auth/userinfo`
- **Header**：`Authorization: Bearer <accessToken>`

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "demo",
    "nickname": "示例用户",
    "avatar": null,
    "email": "demo@example.com"
  }
}
```

---

### 23.5 登出

- **URL**：`POST /xuyuan/app/auth/logout`
- **Header**：`Authorization: Bearer <accessToken>`
- **说明**：服务端将当前 Token 加入 Redis 黑名单（可选），前端清理本地凭证。

**成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

---

### 23.6 修改密码

- **URL**：`PUT /xuyuan/app/auth/password`
- **Header**：`Authorization: Bearer <accessToken>`
- **说明**：用户自行修改密码，需校验原密码。

**请求 Body**

| 参数           | 类型   | 必填 | 说明                   |
| -------------- | ------ | ---- | ---------------------- |
| `oldPassword`  | string | 是   | 原密码（明文）         |
| `newPassword`  | string | 是   | 新密码（6-32 字符，明文）|

```typescript
export interface AppChangePasswordParam {
  oldPassword: string;
  newPassword: string;
}
```

**成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

**异常响应示例**

```json
{ "code": 510, "message": "原密码错误", "data": null }
```

---

## 24. 后台-前台用户管理

> 前缀：`/xuyuan/admin/users`
> Header：`Authorization: Bearer <accessToken>`（后台管理员 Token）
>
> 后台管理前台用户的接口，支持分页查询、详情、禁用/启用、重置密码、删除。
> 注意：与第 13-22 节的后台业务接口同属 blog-web-admin，走同一鉴权链路。

### 24.1 分页查询前台用户

- **URL**：`GET /xuyuan/admin/users`
- **说明**：分页查询前台用户列表，支持按用户名、昵称、状态筛选。

**请求参数（Query）**

| 参数        | 类型    | 必填 | 说明                                  |
| ----------- | ------- | ---- | ------------------------------------- |
| `current`   | integer | 否   | 当前页码，默认 1                      |
| `size`      | integer | 否   | 每页条数，默认 10                     |
| `username`  | string  | 否   | 用户名（模糊查询）                    |
| `nickname`  | string  | 否   | 昵称（模糊查询）                      |
| `status`    | integer | 否   | 状态：0 禁用 / 1 正常 / null 查全部   |

**成功响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "demo",
        "nickname": "示例用户",
        "avatar": null,
        "email": "demo@example.com",
        "status": 1,
        "lastLoginAt": "2026-08-25 10:30:00",
        "createTime": "2026-08-25 10:00:00"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10
  }
}
```

```typescript
export interface AppUserAdminVO {
  id: number;
  username: string;
  nickname: string;
  avatar: string | null;
  email: string | null;
  status: number;        // 0 禁用 / 1 正常
  lastLoginAt: string | null;
  createTime: string;
}

export interface AppUserPageParam {
  current?: number;
  size?: number;
  username?: string;
  nickname?: string;
  status?: number | null;
}
```

---

### 24.2 查询前台用户详情

- **URL**：`GET /xuyuan/admin/users/{id}`
- **说明**：查询指定 ID 的前台用户详情。

**路径参数**

| 参数 | 类型    | 必填 | 说明      |
| ---- | ------- | ---- | --------- |
| `id` | integer | 是   | 用户 ID   |

**成功响应**（同 24.1 中的 `AppUserAdminVO`）

**异常响应示例**

```json
{ "code": 509, "message": "用户不存在", "data": null }
```

---

### 24.3 禁用 / 启用前台用户

- **URL**：`PUT /xuyuan/admin/users/{id}/status`
- **说明**：切换前台用户状态。禁用后用户无法登录/评论/点赞，已签发的 Token 可立即失效（可选实现）。

**路径参数**

| 参数 | 类型    | 必填 | 说明    |
| ---- | ------- | ---- | ------- |
| `id` | integer | 是   | 用户 ID |

**请求 Body**

| 参数     | 类型    | 必填 | 说明                  |
| -------- | ------- | ---- | --------------------- |
| `status` | integer | 是   | 0 禁用 / 1 正常       |

```typescript
export interface AppUserStatusParam {
  status: number;  // 0 禁用 / 1 正常
}
```

**成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

**异常响应示例**

```json
{ "code": 509, "message": "用户不存在", "data": null }
```

---

### 24.4 重置前台用户密码

- **URL**：`PUT /xuyuan/admin/users/{id}/reset-password`
- **说明**：后台管理员重置前台用户密码为指定值（MD5 加密后存储）。用户下次登录使用新密码。

**路径参数**

| 参数 | 类型    | 必填 | 说明    |
| ---- | ------- | ---- | ------- |
| `id` | integer | 是   | 用户 ID |

**请求 Body**

| 参数          | 类型   | 必填 | 说明                                   |
| ------------- | ------ | ---- | -------------------------------------- |
| `newPassword` | string | 是   | 新密码明文（6-32 字符，后端 MD5 加密存储）|

```typescript
export interface AppUserResetPasswordParam {
  newPassword: string;
}
```

**成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

**异常响应示例**

```json
{ "code": 509, "message": "用户不存在", "data": null }
```

---

### 24.5 删除前台用户

- **URL**：`DELETE /xuyuan/admin/users/{id}`
- **说明**：逻辑删除前台用户（`deleted = 1`）。

> **关联数据处理约定**：删除用户后，该用户的评论、点赞记录**保留**（`user_id` 仍可指向已删除记录），但展示时用户名显示为"已注销用户"。如需级联删除，请在 service 层显式处理 `t_comment` / `t_like`。

**路径参数**

| 参数 | 类型    | 必填 | 说明    |
| ---- | ------- | ---- | ------- |
| `id` | integer | 是   | 用户 ID |

**成功响应**

```json
{ "code": 200, "message": "success", "data": null }
```

**异常响应示例**

```json
{ "code": 509, "message": "用户不存在", "data": null }
```

---

## 25. 状态码与错误码规范

### 25.1 设计原则

本系统 `code` 字段采用**三位数业务码**风格，按段划分业务域，**HTTP 响应行统一返回 `200 OK`**，业务结果由 `R.code` 字段表达：

1. **业务码分段**：`2xx` 通用 / `3xx` 后台管理端 / `5xx` 前台展示端 / `6xx` Token 认证。
2. **成功统一为 `200`**：一切成功操作的 `code` 返回 `200`，`message` 返回 `"success"`。
3. **一码一义**：每个业务码对应一种明确的业务场景，前端可按 `code` 精确判断业务类型。
4. **HTTP 行统一 200**：业务失败时 HTTP 响应行仍为 `200`，失败信息由 `R.code` + `R.message` 表达。

### 25.2 业务码总表

#### 2xx 通用段

| code | 枚举名               | 使用场景                               |
| ---- | -------------------- | -------------------------------------- |
| 200  | SUCCESS              | 成功                                   |
| 201  | FAIL                 | 通用失败                               |
| 202  | PARAM_ERROR          | 参数不正确（校验失败、格式错误）       |
| 203  | SERVICE_ERROR        | 服务异常（系统内部错误）               |
| 204  | DATA_ERROR           | 数据异常                               |
| 205  | ILLEGAL_REQUEST      | 非法请求                               |
| 206  | REPEAT_SUBMIT        | 重复提交                               |
| 207  | DELETE_ERROR         | 请先删除子集                           |

#### 3xx 后台管理端段（blog-web-admin）

| code | 枚举名                          | 使用场景                                       |
| ---- | ------------------------------- | ---------------------------------------------- |
| 301  | ADMIN_ACCOUNT_EXIST_ERROR       | 账号已存在                                     |
| 302  | ADMIN_CAPTCHA_CODE_ERROR        | 验证码错误                                     |
| 303  | ADMIN_CAPTCHA_CODE_EXPIRED      | 验证码已过期                                   |
| 304  | ADMIN_CAPTCHA_CODE_NOT_FOUND    | 未输入验证码                                   |
| 305  | ADMIN_LOGIN_AUTH                | 未登录                                         |
| 306  | ADMIN_ACCOUNT_NOT_EXIST_ERROR   | 账号不存在（查询场景）                         |
| 307  | ADMIN_ACCOUNT_ERROR             | 用户名或密码错误（登录场景，防账号枚举）       |
| 308  | ADMIN_ACCOUNT_DISABLED_ERROR   | 该用户已被禁用                                 |
| 309  | ADMIN_ACCESS_FORBIDDEN          | 无访问权限                                     |
| 310  | ADMIN_ARTICLE_NOT_FOUND         | 文章不存在                                     |
| 311  | ADMIN_CATEGORY_NOT_FOUND        | 分类不存在                                     |
| 312  | ADMIN_TAG_NOT_FOUND             | 标签不存在                                     |
| 313  | ADMIN_CATEGORY_HAS_ARTICLES     | 分类下有文章，无法删除                         |
| 314  | ADMIN_COMMENT_NOT_FOUND         | 评论不存在                                     |
| 315  | ADMIN_MOMENT_NOT_FOUND          | 说说不存在                                     |
| 316  | ADMIN_GALLERY_NOT_FOUND          | 相册不存在                                     |
| 317  | ADMIN_FRIEND_NOT_FOUND          | 友链不存在                                     |
| 318  | ADMIN_ARTICLE_STATUS_ERROR      | 文章状态不允许该操作                           |
| 319  | ADMIN_FILE_UPLOAD_ERROR         | 文件上传失败                                   |
| 320  | ADMIN_FILE_TYPE_ERROR           | 不支持的文件类型                               |
| 321  | ADMIN_FILE_SIZE_ERROR           | 文件大小超限                                   |

#### 5xx 前台展示端段（blog-web-app）

| code | 枚举名                        | 使用场景                     |
| ---- | ----------------------------- | ---------------------------- |
| 501  | APP_LOGIN_AUTH                | 未登录（游客访问需登录接口） |
| 502  | APP_ACCOUNT_DISABLED_ERROR    | 该用户已被禁用               |
| 503  | APP_COMMENT_CONTENT_EMPTY     | 评论内容为空                 |
| 504  | APP_COMMENT_TOO_OFTEN         | 评论过于频繁                 |
| 505  | APP_LIKE_TOO_OFTEN           | 点赞过于频繁                 |
| 506  | APP_FRIEND_APPLY_TOO_OFTEN    | 友链申请过于频繁             |
| 507  | APP_ACCOUNT_EXIST_ERROR       | 账号已存在（注册场景）       |
| 508  | APP_ACCOUNT_ERROR             | 用户名或密码错误（防枚举）   |
| 509  | APP_USER_NOT_FOUND            | 用户不存在（后台管理场景）   |
| 510  | APP_OLD_PASSWORD_ERROR        | 原密码错误（修改密码场景）   |
| 511  | APP_USERNAME_EMPTY            | 用户名不能为空               |
| 512  | APP_PASSWORD_EMPTY            | 密码不能为空                 |
| 513  | APP_NICKNAME_EMPTY            | 昵称不能为空                 |

#### 6xx Token 段

| code | 枚举名        | 使用场景                          |
| ---- | ------------- | --------------------------------- |
| 601  | TOKEN_EXPIRED | token 过期                        |
| 602  | TOKEN_INVALID | token 非法                        |
| 603  | TOKEN_MISSING | token 缺失（请求头未携带 token）  |

### 25.3 异常响应通用结构

```json
{
  "code": 310,
  "message": "文章不存在",
  "data": null
}
```

```json
{
  "code": 504,
  "message": "评论过于频繁",
  "data": null
}
```

```json
{
  "code": 601,
  "message": "token过期",
  "data": null
}
```

### 25.4 前端处理约定

```typescript
import axios from 'axios';

const service = axios.create({ baseURL: '/xuyuan' });

service.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data;
    // 200 成功，直接返回 data
    if (code === 200) return data;

    // 3xx 后台段 / 5xx 前台段 / 6xx Token 段统一处理
    // 未登录或 token 相关错误，清理凭证并跳转登录
    if ([305, 601, 602, 603].includes(code)) {
      // 清理本地凭证并跳转登录页
      localStorage.removeItem('token');
      router.push('/login');
    }

    // 展示错误提示
    ElMessage.error(message);
    return Promise.reject(new Error(message));
  },
  (error) => {
    // 网络层错误（HTTP 非 200 响应行）
    ElMessage.error('网络异常，请稍后重试');
    return Promise.reject(error);
  }
);
```

---

## 附：接口清单速查

| 模块         | 方法 | 路径                                          | 鉴权 |
| ------------ | ---- | --------------------------------------------- | ---- |
| 认证         | GET  | `/xuyuan/auth/captcha`                           | 否   |
| 认证         | POST | `/xuyuan/auth/login`                             | 否   |
| 认证         | POST | `/xuyuan/auth/refresh`                           | 否   |
| 认证         | POST | `/xuyuan/auth/logout`                            | 是   |
| 认证         | GET  | `/xuyuan/auth/userinfo`                          | 是   |
| 文章         | GET  | `/xuyuan/articles`                               | 否   |
| 文章         | GET  | `/xuyuan/articles/{id}`                          | 否   |
| 文章         | GET  | `/xuyuan/articles/hot`                           | 否   |
| 归档         | GET  | `/xuyuan/categories`                             | 否   |
| 归档         | GET  | `/xuyuan/tags`                                   | 否   |
| 归档         | GET  | `/xuyuan/archives/timeline`                      | 否   |
| 说说         | GET  | `/xuyuan/moments`                                | 否   |
| 说说         | GET  | `/xuyuan/moments/{id}`                           | 否   |
| 照片墙       | GET  | `/xuyuan/galleries`                              | 否   |
| 照片墙       | GET  | `/xuyuan/galleries/{id}`                         | 否   |
| 照片墙       | GET  | `/xuyuan/photos/{id}`                            | 否   |
| 杂谈         | GET  | `/xuyuan/thoughts`                               | 否   |
| 杂谈         | GET  | `/xuyuan/thoughts/{id}`                          | 否   |
| 友链         | GET  | `/xuyuan/friends`                                | 否   |
| 友链         | GET  | `/xuyuan/friends/random`                         | 否   |
| 友链         | POST | `/xuyuan/friends/apply`                          | 否   |
| 关于我       | GET  | `/xuyuan/about`                                  | 否   |
| 评论         | GET  | `/xuyuan/comments`                               | 否   |
| 评论         | POST | `/xuyuan/comments`                               | 否   |
| 点赞         | POST | `/xuyuan/likes/toggle`                           | 否   |
| 站点服务     | GET  | `/xuyuan/site/notice`                            | 否   |
| 站点服务     | GET  | `/xuyuan/site/words/random`                      | 否   |
| 站点服务     | GET  | `/xuyuan/site/weather`                           | 否   |
| 站点服务     | GET  | `/xuyuan/site/schedules`                         | 否   |
| 后台-文章    | GET  | `/xuyuan/admin/articles`                         | 是   |
| 后台-文章    | POST | `/xuyuan/admin/articles`                         | 是   |
| 后台-文章    | PUT  | `/xuyuan/admin/articles/{id}`                    | 是   |
| 后台-文章    | GET  | `/xuyuan/admin/articles/{id}`                    | 是   |
| 后台-文章    | DELETE | `/xuyuan/admin/articles`                        | 是   |
| 后台-文章    | PUT  | `/xuyuan/admin/articles/{id}/status`             | 是   |
| 后台-文章    | POST | `/xuyuan/admin/articles/draft`                   | 是   |
| 后台-分类    | GET/POST | `/xuyuan/admin/categories`                    | 是   |
| 后台-分类    | PUT/DELETE | `/xuyuan/admin/categories/{id}`              | 是   |
| 后台-标签    | GET/POST | `/xuyuan/admin/tags`                           | 是   |
| 后台-标签    | PUT/DELETE | `/xuyuan/admin/tags/{id}`                     | 是   |
| 后台-说说    | GET/POST | `/xuyuan/admin/moments`                        | 是   |
| 后台-说说    | PUT/DELETE | `/xuyuan/admin/moments/{id}`                  | 是   |
| 后台-相册    | GET/POST | `/xuyuan/admin/galleries`                      | 是   |
| 后台-相册    | PUT/DELETE | `/xuyuan/admin/galleries/{id}`                | 是   |
| 后台-照片    | POST | `/xuyuan/admin/galleries/{id}/photos`           | 是   |
| 后台-照片    | DELETE | `/xuyuan/admin/photos/{id}`                    | 是   |
| 后台-友链    | GET/POST | `/xuyuan/admin/friends`                        | 是   |
| 后台-友链    | PUT/DELETE | `/xuyuan/admin/friends/{id}`                  | 是   |
| 后台-友链    | PUT  | `/xuyuan/admin/friends/{id}/audit`              | 是   |
| 后台-友链    | PUT  | `/xuyuan/admin/friends/{id}/sort`               | 是   |
| 后台-评论    | GET  | `/xuyuan/admin/comments`                        | 是   |
| 后台-评论    | PUT  | `/xuyuan/admin/comments/{id}/status`            | 是   |
| 后台-评论    | DELETE | `/xuyuan/admin/comments/{id}`                  | 是   |
| 后台-文件    | POST | `/xuyuan/admin/files/upload`                    | 是   |
| 后台-文件    | POST | `/xuyuan/admin/files/upload/image`              | 是   |
| 后台-统计    | GET  | `/xuyuan/admin/dashboard/stats`                 | 是   |
| 后台-统计    | GET  | `/xuyuan/admin/dashboard/trend`                 | 是   |
| 后台-配置    | GET/PUT | `/xuyuan/admin/configs`                        | 是   |
| 后台-公告    | GET/POST | `/xuyuan/admin/notices`                       | 是   |
| 后台-公告    | PUT/DELETE | `/xuyuan/admin/notices/{id}`                | 是   |
| 后台-一言    | GET/POST | `/xuyuan/admin/words`                         | 是   |
| 后台-一言    | PUT/DELETE | `/xuyuan/admin/words/{id}`                  | 是   |
| 后台-关于我  | GET/PUT | `/xuyuan/admin/about`                          | 是   |
| 后台-日程    | GET/POST | `/xuyuan/admin/schedules`                      | 是   |
| 后台-日程    | PUT/DELETE | `/xuyuan/admin/schedules/{id}`               | 是   |
