package com.xuyuan.blog.model.enums;

import com.xuyuan.blog.common.result.BaseEnum;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 文章内容类型
 *
 * <p>对应 {@code t_article.type} 字段。</p>
 */
public enum ArticleType implements BaseEnum<Integer> {

    TECH(1, "技术文章"),
    ESSAY(2, "人文杂谈"),
    ;

    @EnumValue
    private final Integer code;
    private final String label;

    ArticleType(Integer code, String label) {
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
