package com.xuyuan.blog.admin.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 后台管理端-文章详情/编辑 VO
 *
 * <p>比列表项多了 content (Markdown 正文),用于前端编辑页表单回显。
 * tagIds 数组存在即满足 Element Plus tagSelector 受控组件需要。</p>
 */
@Data
@Schema(description = "后台文章详情/编辑页 VO")
public class ArticleDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文章 ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "封面图 URL")
    private String cover;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "正文 Markdown 原文")
    private String content;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "标签 ID 列表 (编辑页标签回显)")
    private List<Long> tagIds;

    @Schema(description = "内容类型(1 技术/2 杂谈)")
    private Integer type;

    @Schema(description = "状态(0 草稿/1 已发布/2 私密/3 回收站)")
    private Integer status;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
