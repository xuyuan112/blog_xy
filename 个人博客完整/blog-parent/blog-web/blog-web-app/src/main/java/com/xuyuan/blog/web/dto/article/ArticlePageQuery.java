package com.xuyuan.blog.web.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "前台文章分页查询参数")
public class ArticlePageQuery {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "标签 ID")
    private Long tagId;

    @Schema(description = "标题或摘要关键词")
    private String keyword;

    @Schema(description = "内容类型：1 技术文章，2 杂谈")
    private Integer type = 1;
}
