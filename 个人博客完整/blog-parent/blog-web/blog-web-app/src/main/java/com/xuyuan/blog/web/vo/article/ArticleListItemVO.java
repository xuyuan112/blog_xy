package com.xuyuan.blog.web.vo.article;

import com.xuyuan.blog.web.vo.misc.TagVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表项视图对象
 */
@Data
@Schema(description = "文章列表项(用于文章列表/搜索结果)")
public class ArticleListItemVO implements Serializable {

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
    @Schema(description = "摘要", example = "本文介绍 Spring Boot 3 的核心特性...")
    private String summary;

    /** 分类 ID */
    @Schema(description = "分类 ID", example = "1")
    private Long categoryId;

    /** 分类名称 */
    @Schema(description = "分类名称", example = "后端")
    private String categoryName;

    /** 标签列表 */
    @Schema(description = "标签列表")
    private List<TagVO> tags;

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
}
