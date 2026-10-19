package com.xuyuan.blog.model.enums;

import com.xuyuan.blog.common.result.BaseEnum;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 日程类型
 *
 * <p>对应 {@code t_schedule.type} 字段, 使用字符串值。</p>
 */
public enum ScheduleType implements BaseEnum<String> {

    COUNTDOWN("countdown", "倒计时"),
    SCHEDULE("schedule", "日程"),
    ;

    @EnumValue
    private final String code;
    private final String label;

    ScheduleType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
