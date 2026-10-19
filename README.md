# 个人全栈博客系统

## 项目结构

```
blog-parent/          # 后端 Maven 多模块工程
├── blog-common/      # 公共模块: 工具类、常量/异常、BaseEntity、统一响应、JWT/MP/Redis/MinIO/Knife4j 配置
├── blog-model/       # 数据模型: 实体类(PO)、JSON TypeHandler
└── blog-web/         # Web 模块聚合
    ├── blog-web-admin/  # 后台管理系统 (mapper/service/controller, 端口 8081)
    └── blog-web-app/    # 前台展示端 (mapper/service/controller, 端口 8080)

blog-ui/              # 前端 Vue3 + Vite 工程 (端口 5173)
```

## 技术栈

- 后端：JDK 17 / Spring Boot 3.0.5 / MyBatis-Plus 3.5.3.1 / MySQL 8 / Redis / MinIO / JJWT
- 前端：Vue 3 / TypeScript / Vite 5 / Element Plus / Pinia / Vue Router / Axios / SCSS

## 快速开始

### 后端

```bash
# 1. 执行 schema.sql 初始化数据库
# 2. 修改各模块 application.yml 中的数据库/Redis/MinIO 配置
cd blog-parent
mvn clean install -DskipTests

# 启动前台
mvn -pl blog-web/blog-web-app spring-boot:run

# 启动后台
mvn -pl blog-web/blog-web-admin spring-boot:run
```

### 前端

```bash
cd blog-ui
npm install
npm run dev
```

## 接口规范

详见根目录 [API_SPEC.md](../API_SPEC.md)。
