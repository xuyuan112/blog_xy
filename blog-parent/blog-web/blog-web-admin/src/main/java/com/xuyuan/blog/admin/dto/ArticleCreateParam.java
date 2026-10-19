package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文章新增/编辑请求参数
 */
@Data
public class ArticleCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文章标题 */
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 封面图地址 */
    private String cover;

    /** 摘要 */
    private String summary;

    /** 正文(Markdown 原文) */
    @NotBlank(message = "正文不能为空")
    private String content;

    /** 分类 ID */
    private Long categoryId;

    /** 标签 ID 列表 */
    private List<Long> tagIds;

    /** 内容类型: 1 技术文章, 2 人文杂谈 */
    @NotNull(message = "内容类型不能为空")
    private Integer type;

    /** 状态: 0 草稿, 1 已发布, 2 私密 */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /** 是否置顶 */
    private Boolean isTop;
}
