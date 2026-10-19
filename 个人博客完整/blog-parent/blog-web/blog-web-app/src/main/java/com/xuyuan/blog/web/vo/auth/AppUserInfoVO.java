package com.xuyuan.blog.web.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 前台用户信息视图对象
 */
@Data
@Schema(description = "前台用户信息")
public class AppUserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    /** 用户名 */
    @Schema(description = "用户名", example = "demo")
    private String username;

    /** 昵称 */
    @Schema(description = "昵称", example = "示例用户")
    private String nickname;

    /** 头像地址 */
    @Schema(description = "头像地址(URL)", example = "https://example.com/avatar.png")
    private String avatar;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "demo@example.com")
    private String email;
}
