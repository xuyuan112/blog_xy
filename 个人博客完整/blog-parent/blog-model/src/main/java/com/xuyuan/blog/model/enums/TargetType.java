package com.xuyuan.blog.model.enums;

import com.xuyuan.blog.common.result.BaseEnum;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 评论 / 点赞 目标类型
 *
 * <p>对应 {@code t_comment.target_type} 与 {@code t_like.target_type} 字段。
 * 文章与说说共用同一套取值, 故定义为通用枚举。</p>
 */
public enum TargetType implements BaseEnum<Integer> {

    ARTICLE(1, "文章"),
    MOMENT(2, "说说"),
    ;

    @EnumValue
    private final Integer code;
    private final String label;

    TargetType(Integer code, String label) {
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
