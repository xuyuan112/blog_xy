package com.xuyuan.blog.web.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 前台登录响应视图对象
 */
@Data
@Schema(description = "前台登录响应")
public class AppLoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** AccessToken */
    @Schema(description = "AccessToken", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    /** RefreshToken */
    @Schema(description = "RefreshToken", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    /** 过期时间(秒) */
    @Schema(description = "过期时间(秒)", example = "1800")
    private Long expiresIn;

    /** Token 类型 */
    @Schema(description = "Token 类型", example = "Bearer")
    private String tokenType;

    /** 用户信息 */
    @Schema(description = "用户信息")
    private AppUserInfoVO userInfo;

    public AppLoginVO(String accessToken, String refreshToken, Long expiresIn, AppUserInfoVO userInfo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = "Bearer";
        this.userInfo = userInfo;
    }
}
