package com.xuyuan.blog.admin.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 验证码视图对象
 */
@Data
@Schema(description = "图形验证码(用于登录前置校验)")
public class CaptchaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 验证码 Key(用于校验时关联) */
    @Schema(description = "验证码 Key(提交登录时携带)", example = "a1b2c3d4-e5f6-7890", requiredMode = Schema.RequiredMode.REQUIRED)
    private String captchaKey;

    /** 验证码图片(base64) */
    @Schema(description = "验证码图片(Base64 编码, 直接放入 <img src='data:image/png;base64,...'>)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String captchaImage;
}
