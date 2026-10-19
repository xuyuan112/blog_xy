package com.xuyuan.blog.admin.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/** 后台-标签列表项 VO */
@Data
@Schema(description = "标签列表项 VO")
public class TagVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "标签 ID", example = "1")
    private Long id;

    @Schema(description = "标签名称", example = "Spring")
    private String name;

    @Schema(description = "标签颜色(十六进制)", example = "#409EFF")
    private String color;

    @Schema(description = "绑定该标签的文章总数", example = "18")
    private Long articleCount;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
