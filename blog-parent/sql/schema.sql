-- ============================================================================
-- 个人全栈博客系统 数据库结构定义 (schema.sql)
-- 依据文档: API_SPEC.md v1.1.0
-- 数据库:   MySQL 8.x
-- 字符集:   utf8mb4 / utf8mb4_unicode_ci
-- 存储引擎: InnoDB
--
-- 约定:
--   1. 主键统一使用 BIGINT 自增 (MyBatis-Plus IdType.AUTO)
--   2. 所有表包含通用审计字段 (create_time / update_time / create_by / update_by / deleted)
--   3. deleted 为逻辑删除标记 (0 未删除, 1 已删除), 由 MyBatis-Plus @TableLogic 维护
--   4. JSON 类型字段使用 MySQL JSON 类型, 由 JacksonTypeHandler 自动转换
--   5. 用户密码使用 MD5 加密后存储(32 位十六进制小写)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 0. 创建数据库
-- ----------------------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS `blog`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `blog`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
-- 1. 用户表 (t_user)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`      VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    `password`      VARCHAR(64)  NOT NULL COMMENT '密码(MD5 加密, 32 位十六进制)',
    `nickname`      VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`        VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `email`         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0 禁用, 1 正常',
    `last_login_at` DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`     BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`     BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------------------------------------------------------
-- 2. 分类表 (t_category)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(64)  NOT NULL COMMENT '分类名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序值, 越小越靠前',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- ----------------------------------------------------------------------------
-- 3. 标签表 (t_tag)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(64)  NOT NULL COMMENT '标签名称',
    `color`       VARCHAR(32)  DEFAULT NULL COMMENT '标签颜色(如 #409EFF)',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';

-- ----------------------------------------------------------------------------
-- 4. 文章表 (t_article)  -- 技术文章与人文杂谈共用, 通过 type 区分
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_article`;
CREATE TABLE `t_article` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`         VARCHAR(255) NOT NULL COMMENT '文章标题',
    `cover`         VARCHAR(255) DEFAULT NULL COMMENT '封面图地址',
    `summary`       VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    `content`       LONGTEXT     NOT NULL COMMENT '正文(Markdown 原文)',
    `category_id`   BIGINT       DEFAULT NULL COMMENT '分类 ID',
    `type`          TINYINT      NOT NULL DEFAULT 1 COMMENT '内容类型: 1 技术文章, 2 人文杂谈',
    `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0 草稿, 1 已发布, 2 私密, 3 回收站',
    `is_top`        TINYINT      NOT NULL DEFAULT 0 COMMENT '是否置顶: 0 否, 1 是',
    `view_count`    BIGINT       NOT NULL DEFAULT 0 COMMENT '阅读量',
    `like_count`    BIGINT       NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` BIGINT       NOT NULL DEFAULT 0 COMMENT '评论数',
    `published_at`  DATETIME     DEFAULT NULL COMMENT '发布时间',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`     BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`     BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_type_status` (`type`, `status`),
    KEY `idx_published` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- ----------------------------------------------------------------------------
-- 5. 文章-标签关联表 (t_article_tag)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_article_tag`;
CREATE TABLE `t_article_tag` (
    `id`         BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id` BIGINT NOT NULL COMMENT '文章 ID',
    `tag_id`     BIGINT NOT NULL COMMENT '标签 ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ----------------------------------------------------------------------------
-- 6. 说说表 (t_moment)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_moment`;
CREATE TABLE `t_moment` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `content`       TEXT         NOT NULL COMMENT '说说内容',
    `images`        JSON         DEFAULT NULL COMMENT '图片数组(最多 9 张), JSON 字符串数组',
    `location`      VARCHAR(128) DEFAULT NULL COMMENT '发布定位',
    `mood`          VARCHAR(32)  DEFAULT NULL COMMENT '心情 Tag',
    `visible`       TINYINT      NOT NULL DEFAULT 0 COMMENT '可见性: 0 公开, 1 隐藏, 2 私密',
    `like_count`    BIGINT       NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` BIGINT       NOT NULL DEFAULT 0 COMMENT '评论数',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`     BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`     BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='说说表';

-- ----------------------------------------------------------------------------
-- 7. 相册表 (t_gallery)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_gallery`;
CREATE TABLE `t_gallery` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(128) NOT NULL COMMENT '相册名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '相册描述',
    `cover`       VARCHAR(255) DEFAULT NULL COMMENT '相册封面图地址',
    `shoot_date`  DATE         DEFAULT NULL COMMENT '拍摄日期',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序值',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='相册表';

-- ----------------------------------------------------------------------------
-- 8. 照片表 (t_photo)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_photo`;
CREATE TABLE `t_photo` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gallery_id`  BIGINT       NOT NULL COMMENT '所属相册 ID',
    `original`    VARCHAR(255) NOT NULL COMMENT '原图地址',
    `thumbnail`   VARCHAR(255) NOT NULL COMMENT '缩略图地址',
    `shoot_time`  DATETIME     DEFAULT NULL COMMENT '拍摄时间',
    `exif`        JSON         DEFAULT NULL COMMENT 'EXIF 参数(JSON 对象: 相机型号/ISO/快门等)',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序值',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    KEY `idx_gallery` (`gallery_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片表';

-- ----------------------------------------------------------------------------
-- 9. 友情链接表 (t_friend)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_friend`;
CREATE TABLE `t_friend` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(128) NOT NULL COMMENT '站点名称',
    `url`         VARCHAR(255) NOT NULL COMMENT '站点地址',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '站点描述',
    `group_name`  VARCHAR(64)  DEFAULT NULL COMMENT '分组名(如: 我的伙伴们)',
    `email`       VARCHAR(128) DEFAULT NULL COMMENT '申请人邮箱(用于通知)',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '审核状态: 0 待审核, 1 已通过, 2 已拒绝',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序值',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_group` (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='友情链接表';

-- ----------------------------------------------------------------------------
-- 10. 评论表 (t_comment)  -- 文章与说说共用, target_type 区分
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `target_type` TINYINT      NOT NULL COMMENT '目标类型: 1 文章, 2 说说',
    `target_id`   BIGINT       NOT NULL COMMENT '目标 ID',
    `content`     TEXT         NOT NULL COMMENT '评论内容',
    `nickname`    VARCHAR(64)  NOT NULL COMMENT '评论者昵称',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '评论者头像',
    `email`       VARCHAR(128) DEFAULT NULL COMMENT '评论者邮箱',
    `website`     VARCHAR(255) DEFAULT NULL COMMENT '评论者站点',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0 待审核, 1 已发布, 2 已隐藏',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    KEY `idx_target` (`target_type`, `target_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ----------------------------------------------------------------------------
-- 11. 点赞表 (t_like)  -- 防刷点赞记录
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_like`;
CREATE TABLE `t_like` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `target_type` TINYINT     NOT NULL COMMENT '目标类型: 1 文章, 2 说说',
    `target_id`   BIGINT      NOT NULL COMMENT '目标 ID',
    `ip`          VARCHAR(64) DEFAULT NULL COMMENT '点赞者 IP(防刷标识)',
    `user_id`     BIGINT      DEFAULT NULL COMMENT '点赞用户 ID(未登录为 NULL)',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_target_ip` (`target_type`, `target_id`, `ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';

-- ----------------------------------------------------------------------------
-- 12. 关于我表 (t_about)  -- 单行配置
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_about`;
CREATE TABLE `t_about` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `signature`   VARCHAR(255) DEFAULT NULL COMMENT '个性签名',
    `intro`       TEXT         DEFAULT NULL COMMENT '自我介绍',
    `skills`      JSON         DEFAULT NULL COMMENT '技能树 JSON 数组 [{name, level, category}]',
    `milestones`  JSON         DEFAULT NULL COMMENT '履历里程碑 JSON 数组 [{year, title, description}]',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关于我表';

-- ----------------------------------------------------------------------------
-- 13. 站点配置表 (t_site_config)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_site_config`;
CREATE TABLE `t_site_config` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_key`  VARCHAR(128) NOT NULL COMMENT '配置键(如 siteName/icp/notice)',
    `config_value` TEXT        DEFAULT NULL COMMENT '配置值',
    `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点配置表';

-- ----------------------------------------------------------------------------
-- 14. 公告表 (t_notice)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `content`     TEXT     NOT NULL COMMENT '公告内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT   DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT   DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站长公告表';

-- ----------------------------------------------------------------------------
-- 15. 一言表 (t_word)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_word`;
CREATE TABLE `t_word` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `content`     VARCHAR(500) NOT NULL COMMENT '语句内容',
    `source`      VARCHAR(128) DEFAULT NULL COMMENT '出处',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='一言表';

-- ----------------------------------------------------------------------------
-- 16. 日程/倒计时表 (t_schedule)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_schedule`;
CREATE TABLE `t_schedule` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`       VARCHAR(255) NOT NULL COMMENT '事件标题',
    `target_time` DATETIME     NOT NULL COMMENT '目标时间(倒计时基准)',
    `type`        VARCHAR(32)  DEFAULT 'countdown' COMMENT '类型: countdown 倒计时, schedule 日程',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建人',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程倒计时表';

-- ----------------------------------------------------------------------------
-- 17. 文章访问日志表 (t_article_view_log)  -- 阅读量统计与防刷 / 访问趋势
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `t_article_view_log`;
CREATE TABLE `t_article_view_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id`  BIGINT       NOT NULL COMMENT '文章 ID',
    `ip`          VARCHAR(64)  DEFAULT NULL COMMENT '访问者 IP',
    `user_agent`  VARCHAR(255) DEFAULT NULL COMMENT 'UA',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    PRIMARY KEY (`id`),
    KEY `idx_article` (`article_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章访问日志表';

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- 18. 前台用户表 (t_app_user)
-- ----------------------------------------------------------------------------
-- 与后台管理员(t_user)独立分离, 仅用于前台展示端的注册/登录/评论/点赞等互动场景
-- 密码策略: MD5(32 位十六进制小写), 与 t_user 保持一致
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `t_app_user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`      VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    `password`      VARCHAR(64)  NOT NULL COMMENT '密码(MD5 加密, 32 位十六进制)',
    `nickname`      VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`        VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `email`         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0 禁用, 1 正常',
    `last_login_at` DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`     BIGINT       DEFAULT NULL COMMENT '创建人(注册时为用户自身 ID, 后台创建时为管理员 ID)',
    `update_by`     BIGINT       DEFAULT NULL COMMENT '更新人',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0 否, 1 是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='前台用户表';
