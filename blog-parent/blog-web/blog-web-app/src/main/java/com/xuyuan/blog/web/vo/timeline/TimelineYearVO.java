package com.xuyuan.blog.web.vo.timeline;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 时间线归档(年)视图对象
 */
@Data
@Schema(description = "时间线归档-年(含月份列表)")
public class TimelineYearVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 年份 */
    @Schema(description = "年份(yyyy)", example = "2026")
    private String year;

    /** 月份列表 */
    @Schema(description = "月份列表(按月份倒序)")
    private List<TimelineMonthVO> months;
}
