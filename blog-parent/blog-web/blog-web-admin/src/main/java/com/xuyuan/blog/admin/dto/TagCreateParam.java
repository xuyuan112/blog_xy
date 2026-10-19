package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签新增/编辑请求参数
 */
@Data
public class TagCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 标签名称 */
    @NotBlank(message = "标签名称不能为空")
    private String name;

    /** 标签颜色 */
    private String color;
}
