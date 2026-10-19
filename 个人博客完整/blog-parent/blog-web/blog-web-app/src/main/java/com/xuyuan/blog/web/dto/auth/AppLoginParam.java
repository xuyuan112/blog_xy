package com.xuyuan.blog.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 前台用户登录请求参数
 *
 * <p>与后台登录不同, 前台登录不使用图形验证码, 以提升用户体验。</p>
 */
@Data
@Schema(description = "前台用户登录请求参数")
public class AppLoginParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名 */
    @Schema(description = "用户名", example = "demo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码 */
    @Schema(description = "密码(明文, 后端 MD5 比对)", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;
}
