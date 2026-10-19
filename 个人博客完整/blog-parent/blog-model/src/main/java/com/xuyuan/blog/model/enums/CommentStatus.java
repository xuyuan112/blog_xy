package com.xuyuan.blog.model.enums;

import com.xuyuan.blog.common.result.BaseEnum;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 评论状态
 *
 * <p>对应 {@code t_comment.status} 字段。</p>
 */
public enum CommentStatus implements BaseEnum<Integer> {

    PENDING(0, "待审核"),
    PUBLISHED(1, "已发布"),
    HIDDEN(2, "已隐藏"),
    ;

    @EnumValue
    private final Integer code;
    private final String label;

    CommentStatus(Integer code, String label) {
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
