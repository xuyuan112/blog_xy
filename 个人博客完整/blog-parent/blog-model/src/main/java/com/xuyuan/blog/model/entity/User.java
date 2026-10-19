package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体 (t_user)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
@Schema(description = "用户实体")
public class User extends BaseEntity {

    /** 登录用户名 */
    @Schema(description = "登录用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /** 密码(BCrypt 加密) */
    @Schema(description = "密码(BCrypt 加密)", example = "$2a$10$xxx...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /** 昵称 */
    @Schema(description = "昵称", example = "许袁")
    private String nickname;

    /** 头像地址 */
    @Schema(description = "头像地址(URL)", example = "https://example.com/avatar.png")
    private String avatar;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    /** 状态: 0 禁用, 1 正常 */
    @Schema(description = "状态(0 禁用, 1 正常)", example = "1")
    private Integer status;

    /** 最后登录时间 */
    @Schema(description = "最后登录时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime lastLoginAt;
}
