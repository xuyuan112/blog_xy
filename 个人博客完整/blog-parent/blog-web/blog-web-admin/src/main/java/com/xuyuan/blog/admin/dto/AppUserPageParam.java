package com.xuyuan.blog.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 前台用户分页查询参数
 */
@Data
@Schema(description = "前台用户分页查询参数")
public class AppUserPageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页码(默认 1) */
    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    /** 每页条数(默认 10) */
    @Schema(description = "每页条数", example = "10")
    private Long size = 10L;

    /** 用户名(模糊查询) */
    @Schema(description = "用户名(模糊查询)", example = "demo")
    private String username;

    /** 昵称(模糊查询) */
    @Schema(description = "昵称(模糊查询)", example = "示例")
    private String nickname;

    /** 状态: 0 禁用, 1 正常, null 查全部 */
    @Schema(description = "状态(0 禁用, 1 正常, null 查全部)", example = "1")
    private Integer status;
}
