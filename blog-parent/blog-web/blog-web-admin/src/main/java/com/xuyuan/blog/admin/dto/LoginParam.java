package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求参数
 */
@Data
public class LoginParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 验证码 Key */
    @NotBlank(message = "验证码 Key 不能为空")
    private String captchaKey;

    /** 验证码内容 */
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;
}
