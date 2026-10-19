package com.xuyuan.blog.model.enums;

import com.xuyuan.blog.common.result.BaseEnum;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 通用 是否 枚举
 *
 * <p>对应所有 {@code is_xxx} 字段(如 {@code t_article.is_top})。</p>
 */
public enum YesNo implements BaseEnum<Integer> {

    NO(0, "否"),
    YES(1, "是"),
    ;

    @EnumValue
    private final Integer code;
    private final String label;

    YesNo(Integer code, String label) {
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
