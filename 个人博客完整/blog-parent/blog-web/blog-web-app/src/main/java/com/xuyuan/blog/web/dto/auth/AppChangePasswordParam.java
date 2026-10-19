package com.xuyuan.blog.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "前台用户修改密码参数")
public class AppChangePasswordParam {

    @NotBlank
    @Schema(description = "原密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 32)
    @Schema(description = "新密码（6-32 位）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
