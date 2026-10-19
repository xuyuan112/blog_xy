package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 友链审核请求参数
 */
@Data
public class FriendAuditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 审核状态: 1 已通过, 2 已拒绝 */
    @NotNull(message = "审核状态不能为空")
    private Integer status;

    /** 拒绝原因 */
    private String reason;
}
