package com.xuyuan.blog.web.dto.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "前台相册分页查询参数")
public class GalleryPageQuery {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页条数", example = "12")
    private Integer pageSize = 12;

    @Schema(description = "相册名称或描述关键词")
    private String keyword;
}
