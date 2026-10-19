package com.xuyuan.blog.admin.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台文章管理视图对象
 *
 * <p>在列表项基础上补充状态、私密标记等管理字段。</p>
 */
@Data
@Schema(description = "后台文章管理视图(含状态、私密标记等管理字段)")
public class ArticleAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文章 ID */
    @Schema(description = "文章 ID", example = "1")
    private Long id;

    /** 标题 */
    @Schema(description = "标题", example = "Spring Boot 3 入门")
    private String title;

    /** 封面图 */
    @Schema(description = "封面图(URL)", example = "https://example.com/cover.jpg")
    private String cover;

    /** 摘要 */
    @Schema(description = "摘要")
    private String summary;

    /** 分类 ID */
    @Schema(description = "分类 ID", example = "1")
    private Long categoryId;

    /** 分类名称 */
    @Schema(description = "分类名称", example = "后端")
    private String categoryName;

    /** 阅读量 */
    @Schema(description = "阅读量", example = "1024")
    private Long viewCount;

    /** 评论数 */
    @Schema(description = "评论数", example = "20")
    private Long commentCount;

    /** 点赞数 */
    @Schema(description = "点赞数", example = "50")
    private Long likeCount;

    /** 内容类型: 1 技术文章, 2 人文杂谈 */
    @Schema(description = "内容类型(1 技术文章, 2 人文杂谈)", example = "1")
    private Integer type;

    /** 是否置顶 */
    @Schema(description = "是否置顶", example = "false")
    private Boolean isTop;

    /** 发布时间 */
    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    /** 状态: 0 草稿, 1 已发布, 2 私密, 3 回收站 */
    @Schema(description = "状态(0 草稿, 1 已发布, 2 私密, 3 回收站)", example = "1")
    private Integer status;

    /** 是否私密 */
    @Schema(description = "是否私密", example = "false")
    private Boolean isPrivate;

    /** 标签 ID 列表(回显用) */
    @Schema(description = "标签 ID 列表(回显用)", example = "[1, 2, 3]")
    private List<Long> tagIds;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
