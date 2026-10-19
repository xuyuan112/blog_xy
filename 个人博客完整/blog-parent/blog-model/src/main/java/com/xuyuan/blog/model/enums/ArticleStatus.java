package com.xuyuan.blog.model.enums;

import com.xuyuan.blog.common.result.BaseEnum;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 文章状态
 *
 * <p>对应 {@code t_article.status} 字段。</p>
 */
public enum ArticleStatus implements BaseEnum<Integer> {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    PRIVATE(2, "私密"),
    RECYCLE(3, "回收站"),
    ;

    @EnumValue
    private final Integer code;
    private final String label;

    ArticleStatus(Integer code, String label) {
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
