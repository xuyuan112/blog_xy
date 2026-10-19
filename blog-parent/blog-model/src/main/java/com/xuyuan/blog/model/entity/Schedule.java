package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 日程/倒计时实体 (t_schedule)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_schedule")
@Schema(description = "日程/倒计时实体")
public class Schedule extends BaseEntity {

    /** 事件标题 */
    @Schema(description = "事件标题", example = "项目截止日", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    /** 目标时间(倒计时基准) */
    @Schema(description = "目标时间(倒计时基准)", example = "2026-12-31T23:59:59", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime targetTime;

    /** 类型: countdown 倒计时, schedule 日程 */
    @Schema(description = "类型(countdown 倒计时, schedule 日程)", example = "countdown", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
}
