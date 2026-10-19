package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 排序调整请求参数
 */
@Data
public class SortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排序值 */
    @NotNull(message = "排序值不能为空")
    private Integer sort;
}
