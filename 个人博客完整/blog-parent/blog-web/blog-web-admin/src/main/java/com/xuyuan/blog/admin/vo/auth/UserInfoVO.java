package com.xuyuan.blog.admin.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户信息视图对象
 */
@Data
@Schema(description = "当前登录用户信息")
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    @Schema(description = "用户 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /** 用户名 */
    @Schema(description = "登录用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /** 昵称 */
    @Schema(description = "昵称", example = "许袁")
    private String nickname;

    /** 头像地址 */
    @Schema(description = "头像地址(URL)", example = "https://example.com/avatar.png")
    private String avatar;

    /** 角色列表 */
    @Schema(description = "角色列表(用于前端按钮级权限控制)", example = "[\"ROLE_ADMIN\"]")
    private List<String> roles;
}
