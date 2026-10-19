package com.xuyuan.blog.admin.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 后台管理端-文章分页列表项 VO
 *
 * <p>字段 100% 对齐前端 blog-admin-ui/api/admin.ts 的 {@code AdminArticle} 接口:</p>
 * <pre>
 * isTop / isPrivate: 前端字段是 boolean, VO 层从数据库 Integer(0/1) / status(2) 转换
 * categoryName    : XML 关联 t_category.name
 * tags            : 二次查 t_tag 聚合 (id+name)
 * tagIds          : 同 tags 的 id 子集 (用于前端编辑页标签回显)
 * </pre>
 */
@Data
@Schema(description = "后台文章分页列表项 VO")
public class AdminArticleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文章 ID", example = "1")
    private Long id;

    @Schema(description = "文章标题", example = "Spring Boot 3 入门")
    private String title;

    @Schema(description = "封面图 URL")
    private String cover;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "分类 ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "后端")
    private String categoryName;

    @Schema(description = "标签列表 (id + name, 用于列表页角标显示)", example = "[{\"id\":1,\"name\":\"Spring\"}]")
    private List<TagItemVO> tags;

    @Schema(description = "标签 ID 数组 (用于编辑页 TagSelect 回显)", example = "[1,2,3]")
    private List<Long> tagIds;

    @Schema(description = "阅读量", example = "1234")
    private Long viewCount;

    @Schema(description = "评论数", example = "45")
    private Long commentCount;

    @Schema(description = "点赞数", example = "67")
    private Long likeCount;

    @Schema(description = "内容类型(1 技术文章/2 人文杂谈)", example = "1")
    private Integer type;

    /** 原始数据库 0/1 值 (由 XML 写入), Service 层再转换为下面的 Boolean 字段; 对前端隐藏 */
    @io.swagger.v3.oas.annotations.media.Schema(hidden = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Integer isTopRaw;

    @Schema(description = "是否置顶(由 isTopRaw 转换而来)", example = "true")
    private Boolean isTop;

    @Schema(description = "发布时间(ISO 字符串)", example = "2026-08-25 10:00:00")
    private String publishedAt;

    @Schema(description = "文章状态(0 草稿/1 已发布/2 私密/3 回收站)", example = "1")
    private Integer status;

    @Schema(description = "是否私密 (status == 2 时为 true)", example = "false")
    private Boolean isPrivate;

    @Schema(description = "创建时间(ISO 字符串)")
    private String createTime;

    @Schema(description = "更新时间(ISO 字符串)")
    private String updateTime;

    /** tags 数组的内嵌对象 */
    @Data
    @Schema(description = "文章标签极简结构 (id+name)")
    public static class TagItemVO implements Serializable {
        private static final long serialVersionUID = 1L;
        @Schema(description = "标签 ID", example = "1")
        private Long id;
        @Schema(description = "标签名称", example = "Spring")
        private String name;
    }
}
