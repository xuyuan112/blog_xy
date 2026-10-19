package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量 ID 请求参数
 */
@Data
public class IdsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID 集合 */
    @NotEmpty(message = "ID 列表不能为空")
    private List<Long> ids;
}
