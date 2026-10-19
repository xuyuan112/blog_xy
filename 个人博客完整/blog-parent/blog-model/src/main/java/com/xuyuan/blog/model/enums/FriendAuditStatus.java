package com.xuyuan.blog.model.enums;

import com.xuyuan.blog.common.result.BaseEnum;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 友链审核状态
 *
 * <p>对应 {@code t_friend.status} 字段。</p>
 */
public enum FriendAuditStatus implements BaseEnum<Integer> {

    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝"),
    ;

    @EnumValue
    private final Integer code;
    private final String label;

    FriendAuditStatus(Integer code, String label) {
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
