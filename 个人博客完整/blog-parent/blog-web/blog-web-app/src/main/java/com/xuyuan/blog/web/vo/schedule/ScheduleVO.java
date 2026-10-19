package com.xuyuan.blog.web.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 日程/倒计时视图对象
 */
@Data
@Schema(description = "日程/倒计时(前台展示用)")
public class ScheduleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日程 ID */
    @Schema(description = "日程 ID", example = "1")
    private Long id;

    /** 事件标题 */
    @Schema(description = "事件标题", example = "项目截止日")
    private String title;

    /** 目标时间 */
    @Schema(description = "目标时间", example = "2026-12-31T23:59:59")
    private LocalDateTime targetTime;

    /** 类型: countdown 倒计时, schedule 日程 */
    @Schema(description = "类型(countdown 倒计时, schedule 日程)", example = "countdown")
    private String type;
}
