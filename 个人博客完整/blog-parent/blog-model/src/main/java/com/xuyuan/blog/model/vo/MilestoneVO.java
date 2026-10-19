package com.xuyuan.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 履历里程碑条目视图对象
 *
 * <p>以 JSON 数组形式存储于 t_about.milestones 字段。</p>
 */
@Data
@Schema(description = "履历里程碑条目")
public class MilestoneVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 年份 */
    @Schema(description = "年份", example = "2024", requiredMode = Schema.RequiredMode.REQUIRED)
    private String year;

    /** 标题 */
    @Schema(description = "标题", example = "开源博客上线", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    /** 描述 */
    @Schema(description = "描述", example = "完成个人博客系统并开源")
    private String description;
}
