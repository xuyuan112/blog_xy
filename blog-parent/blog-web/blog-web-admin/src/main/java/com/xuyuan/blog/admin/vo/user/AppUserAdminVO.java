package com.xuyuan.blog.admin.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 后台管理 - 前台用户视图对象
 *
 * <p>用于后台分页查询/详情接口返回, 包含审计字段。</p>
 */
@Data
@Schema(description = "前台用户信息(后台管理视图)")
public class AppUserAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    @Schema(description = "用户 ID", example = "1")
    private Long id;

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

    /** 状态: 0 禁用, 1 正常 */
    @Schema(description = "状态(0 禁用, 1 正常)", example = "1")
    private Integer status;

    /** 最后登录时间 */
    @Schema(description = "最后登录时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime lastLoginAt;

    /** 创建时间 */
    @Schema(description = "注册时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}
