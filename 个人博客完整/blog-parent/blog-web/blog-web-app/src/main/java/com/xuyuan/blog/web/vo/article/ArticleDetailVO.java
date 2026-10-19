package com.xuyuan.blog.web.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情视图对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文章详情(含正文、目录、上下篇导航)")
public class ArticleDetailVO extends ArticleListItemVO {

    /** 正文(Markdown 原文) */
    @Schema(description = "正文(Markdown 原文)")
    private String content;

    /** 目录树 */
    @Schema(description = "目录树(由正文 H2/H3 自动生成)")
    private List<TocItem> toc;

    /** 上一篇 */
    @Schema(description = "上一篇文章(无则 null)")
    private ArticleNavVO prevArticle;

    /** 下一篇 */
    @Schema(description = "下一篇文章(无则 null)")
    private ArticleNavVO nextArticle;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 目录条目
     */
    @Data
    @Schema(description = "目录条目")
    public static class TocItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 标题级别 */
        @Schema(description = "标题级别(2=H2, 3=H3)", example = "2")
        private Integer level;

        /** 标题文本 */
        @Schema(description = "标题文本", example = "环境准备")
        private String text;

        /** 锚点 */
        @Schema(description = "锚点 ID", example = "环境准备")
        private String anchor;
    }
}
