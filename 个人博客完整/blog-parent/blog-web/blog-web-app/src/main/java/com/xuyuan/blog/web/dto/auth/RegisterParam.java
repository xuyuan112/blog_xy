package com.xuyuan.blog.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 前台用户注册请求参数
 */
@Data
@Schema(description = "前台用户注册请求参数")
public class RegisterParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名 */
    @Schema(description = "用户名", example = "demo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 32, message = "用户名长度必须在 3-32 个字符之间")
    private String username;

    /** 密码 */
    @Schema(description = "密码(明文, 后端 MD5 加密存储)", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在 6-32 个字符之间")
    private String password;

    /** 昵称 */
    @Schema(description = "昵称", example = "示例用户", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "昵称不能为空")
    @Size(max = 64, message = "昵称长度不能超过 64 个字符")
    private String nickname;

    /** 邮箱(可选) */
    @Schema(description = "邮箱(可选)", example = "demo@example.com")
    @Size(max = 128, message = "邮箱长度不能超过 128 个字符")
    private String email;
}
