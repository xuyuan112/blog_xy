package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 分类新增/编辑请求参数
 */
@Data
public class CategoryCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类名称 */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /** 分类描述 */
    private String description;

    /** 排序值 */
    private Integer sort;
}
