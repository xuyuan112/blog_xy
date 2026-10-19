package com.xuyuan.blog.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 点赞/取消点赞请求参数
 */
@Data
public class LikeToggleParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目标类型: 1 文章, 2 说说 */
    @NotNull(message = "目标类型不能为空")
    private Integer targetType;

    /** 目标 ID */
    @NotNull(message = "目标 ID 不能为空")
    private Long targetId;
}
