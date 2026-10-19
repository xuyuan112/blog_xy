package com.xuyuan.blog.model.enums;

import com.xuyuan.blog.common.result.BaseEnum;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 用户状态
 *
 * <p>对应 {@code t_user.status} 字段。</p>
 */
public enum UserStatus implements BaseEnum<Integer> {

    DISABLED(0, "禁用"),
    NORMAL(1, "正常"),
    ;

    @EnumValue
    private final Integer code;
    private final String label;

    UserStatus(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
