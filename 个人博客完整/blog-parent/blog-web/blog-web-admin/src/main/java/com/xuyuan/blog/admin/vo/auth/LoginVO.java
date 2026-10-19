package com.xuyuan.blog.admin.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录结果视图对象
 */
@Data
@Schema(description = "登录成功结果(返回双 Token + 用户信息)")
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 访问令牌 */
    @Schema(description = "访问令牌(有效期 30 分钟)", example = "eyJhbGciOiJIUzI1NiJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accessToken;

    /** 刷新令牌 */
    @Schema(description = "刷新令牌(有效期 7 天, 用于换取新 accessToken)", example = "eyJhbGciOiJIUzI1NiJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;

    /** 过期时间(秒) */
    @Schema(description = "accessToken 过期时间(秒)", example = "1800", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long expiresIn;

    /** 令牌类型 */
    @Schema(description = "令牌类型", example = "Bearer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tokenType;

    /** 用户信息 */
    @Schema(description = "当前登录用户信息", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserInfoVO userInfo;
}
