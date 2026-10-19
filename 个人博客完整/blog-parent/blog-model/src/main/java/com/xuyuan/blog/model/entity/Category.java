package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章分类实体 (t_category)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category")
@Schema(description = "文章分类实体")
public class Category extends BaseEntity {

    /** 分类名称 */
    @Schema(description = "分类名称", example = "后端", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 分类描述 */
    @Schema(description = "分类描述", example = "后端开发相关文章")
    private String description;

    /** 排序值, 越小越靠前 */
    @Schema(description = "排序值(越小越靠前)", example = "1")
    private Integer sort;
}
