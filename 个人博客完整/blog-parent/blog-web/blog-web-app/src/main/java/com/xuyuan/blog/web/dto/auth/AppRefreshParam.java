package com.xuyuan.blog.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 刷新 Token 请求参数
 */
@Data
@Schema(description = "刷新 Token 请求参数")
public class AppRefreshParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** RefreshToken */
    @Schema(description = "RefreshToken", example = "eyJhbGciOiJIUzI1NiJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;
}
