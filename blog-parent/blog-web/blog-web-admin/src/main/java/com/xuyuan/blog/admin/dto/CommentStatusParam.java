package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 评论状态变更请求参数
 */
@Data
public class CommentStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态: 0 待审核, 1 已发布, 2 已隐藏 */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
