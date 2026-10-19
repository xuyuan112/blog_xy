package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 刷新 Token 请求参数
 */
@Data
public class RefreshParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 刷新令牌 */
    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;
}
