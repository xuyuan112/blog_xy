package com.xuyuan.blog.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 前台用户状态切换参数
 */
@Data
@Schema(description = "前台用户状态切换参数")
public class AppUserStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态: 0 禁用, 1 正常 */
    @Schema(description = "状态(0 禁用, 1 正常)", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer status;
}
