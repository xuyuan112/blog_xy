package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章状态变更请求参数
 */
@Data
public class ArticleStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态: 0 草稿, 1 已发布, 2 私密, 3 回收站 */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /** 是否置顶 */
    private Boolean isTop;
}
