package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 前台用户实体 (t_app_user)
 *
 * <p>与后台管理员 {@link User} 独立分离, 仅用于前台展示端的注册/登录/评论/点赞等互动场景。
 * 密码采用 MD5 加密(32 位十六进制小写), 与 t_user 表保持一致。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_app_user")
@Schema(description = "前台用户实体")
public class AppUser extends BaseEntity {

    /** 登录用户名 */
    @Schema(description = "登录用户名", example = "demo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /** 密码(MD5 加密, 32 位十六进制) */
    @Schema(description = "密码(MD5 加密, 32 位十六进制)", example = "e10adc3949ba59abbe56e057f20f883e", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

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
}
