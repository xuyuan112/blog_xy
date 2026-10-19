package com.xuyuan.blog.web.vo.misc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分类视图对象
 */
@Data
@Schema(description = "分类(前台展示用, 含文章数)")
public class CategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类 ID */
    @Schema(description = "分类 ID", example = "1")
    private Long id;

    /** 分类名称 */
    @Schema(description = "分类名称", example = "后端")
    private String name;

    /** 分类描述 */
    @Schema(description = "分类描述", example = "后端开发相关文章")
    private String description;

    /** 文章数 */
    @Schema(description = "该分类下文章数", example = "10")
    private Long articleCount;
}
