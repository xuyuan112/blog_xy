package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文章实体 (t_article)
 *
 * <p>技术文章与人文杂谈共用一张表, 通过 {@link #type} 字段区分。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_article")
@Schema(description = "文章实体(技术文章与人文杂谈共用)")
public class Article extends BaseEntity {

    /** 文章标题 */
    @Schema(description = "文章标题", example = "Spring Boot 3 入门", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    /** 封面图地址 */
    @Schema(description = "封面图地址(URL)", example = "https://example.com/cover.jpg")
    private String cover;

    /** 摘要 */
    @Schema(description = "摘要", example = "本文介绍 Spring Boot 3 的核心特性...")
    private String summary;

    /** 正文(Markdown 原文) */
    @Schema(description = "正文(Markdown 原文)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    /** 分类 ID */
    @Schema(description = "分类 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    /** 内容类型: 1 技术文章, 2 人文杂谈 */
    @Schema(description = "内容类型(1 技术文章, 2 人文杂谈)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

    /** 状态: 0 草稿, 1 已发布, 2 私密, 3 回收站 */
    @Schema(description = "状态(0 草稿, 1 已发布, 2 私密, 3 回收站)", example = "1")
    private Integer status;

    /** 是否置顶: 0 否, 1 是 */
    @Schema(description = "是否置顶(0 否, 1 是)", example = "0")
    private Integer isTop;

    /** 阅读量 */
    @Schema(description = "阅读量", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private Long viewCount;

    /** 点赞数 */
    @Schema(description = "点赞数", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private Long likeCount;

    /** 评论数 */
    @Schema(description = "评论数", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private Long commentCount;

    /** 发布时间 */
    @Schema(description = "发布时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime publishedAt;
}
