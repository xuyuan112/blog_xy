package com.xuyuan.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 技能树条目视图对象
 *
 * <p>以 JSON 数组形式存储于 t_about.skills 字段。</p>
 */
@Data
@Schema(description = "技能树条目")
public class SkillVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 技能名称 */
    @Schema(description = "技能名称", example = "Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 熟练度 0-100 */
    @Schema(description = "熟练度(0-100)", example = "85", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer level;

    /** 分类, 如 后端/前端 */
    @Schema(description = "分类", example = "后端")
    private String category;
}
