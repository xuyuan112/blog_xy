package com.xuyuan.blog.admin.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/** 后台-分类列表项 VO */
@Data
@Schema(description = "分类列表项 VO")
public class CategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类 ID", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "后端")
    private String name;

    @Schema(description = "分类描述", example = "后端开发相关文章")
    private String description;

    @Schema(description = "排序值(越小越靠前)", example = "1")
    private Integer sort;

    @Schema(description = "该分类下的文章数(删除前弹窗校验用)", example = "42")
    private Long articleCount;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
