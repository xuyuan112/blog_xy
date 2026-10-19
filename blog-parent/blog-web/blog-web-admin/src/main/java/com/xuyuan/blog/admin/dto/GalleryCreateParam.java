package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 相册新增/编辑请求参数
 */
@Data
public class GalleryCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 相册名称 */
    @NotBlank(message = "相册名称不能为空")
    private String name;

    /** 相册描述 */
    private String description;

    /** 封面图地址 */
    private String cover;

    /** 拍摄日期 */
    private LocalDate shootDate;
}
