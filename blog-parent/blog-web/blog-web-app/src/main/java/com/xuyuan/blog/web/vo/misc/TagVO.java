package com.xuyuan.blog.web.vo.misc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签视图对象
 */
@Data
@Schema(description = "标签(前台展示用, 含文章数与权重)")
public class TagVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 标签 ID */
    @Schema(description = "标签 ID", example = "1")
    private Long id;

    /** 标签名称 */
    @Schema(description = "标签名称", example = "Spring")
    private String name;

    /** 标签颜色 */
    @Schema(description = "标签颜色(十六进制)", example = "#409EFF")
    private String color;

    /** 权重(按文章密集度) */
    @Schema(description = "权重(按文章密集度, 用于标签云字号)", example = "5")
    private Integer weight;

    /** 文章数 */
    @Schema(description = "该标签下文章数", example = "8")
    private Long articleCount;
}
