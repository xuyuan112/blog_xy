package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章标签实体 (t_tag)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_tag")
@Schema(description = "文章标签实体")
public class Tag extends BaseEntity {

    /** 标签名称 */
    @Schema(description = "标签名称", example = "Spring", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 标签颜色, 如 #409EFF */
    @Schema(description = "标签颜色(十六进制)", example = "#409EFF")
    private String color;
}
