package com.xuyuan.blog.admin.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 访问趋势数据点视图对象
 */
@Data
@Schema(description = "访问趋势数据点(用于折线图)")
public class TrendPointVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日期(yyyy-MM-dd) */
    @Schema(description = "日期(yyyy-MM-dd)", example = "2026-08-25", requiredMode = Schema.RequiredMode.REQUIRED)
    private String date;

    /** 访问量 */
    @Schema(description = "当日访问量", example = "320", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long viewCount;

    /** 评论数 */
    @Schema(description = "当日新增评论数", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long commentCount;
}
