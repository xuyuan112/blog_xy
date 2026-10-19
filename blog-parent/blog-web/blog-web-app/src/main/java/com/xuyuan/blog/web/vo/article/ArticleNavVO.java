package com.xuyuan.blog.web.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章导航(上一篇/下一篇/时间线条目)视图对象
 */
@Data
@Schema(description = "文章导航项(上一篇/下一篇/时间线条目)")
public class ArticleNavVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文章 ID */
    @Schema(description = "文章 ID", example = "1")
    private Long id;

    /** 标题 */
    @Schema(description = "标题", example = "Spring Boot 3 入门")
    private String title;

    /** 发布时间(时间线场景使用) */
    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;
}
