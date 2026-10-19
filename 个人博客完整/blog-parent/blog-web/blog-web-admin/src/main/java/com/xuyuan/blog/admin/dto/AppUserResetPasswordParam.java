package com.xuyuan.blog.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 前台用户重置密码参数
 */
@Data
@Schema(description = "前台用户重置密码参数")
public class AppUserResetPasswordParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新密码(明文, 后端 MD5 加密存储) */
    @Schema(description = "新密码(明文, 后端 MD5 加密存储)", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在 6-32 个字符之间")
    private String newPassword;
}
