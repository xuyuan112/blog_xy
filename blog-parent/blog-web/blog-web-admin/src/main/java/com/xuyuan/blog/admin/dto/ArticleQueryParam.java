package com.xuyuan.blog.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 后台文章分页查询参数
 *
 * <p>完全对齐前端 {@code adminGet<PageResult<AdminArticle>>('/articles', params)} 的参数字段。</p>
 */
@Data
@Schema(description = "后台文章分页查询参数")
public class ArticleQueryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页码, 从 1 开始", example = "1")
    private Integer page = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "文章状态筛选(0 草稿/1 已发布/2 私密/3 回收站), 不传查全部", example = "1")
    private Integer status;

    @Schema(description = "内容类型筛选(1 技术/2 杂谈), 不传查全部", example = "1")
    private Integer type;

    @Schema(description = "关键词: 按标题/摘要模糊搜索", example = "Spring Boot")
    private String keyword;
}
