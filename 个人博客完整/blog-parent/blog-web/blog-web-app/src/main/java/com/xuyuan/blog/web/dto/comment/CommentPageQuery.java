package com.xuyuan.blog.web.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "前台评论分页查询参数")
public class CommentPageQuery {

    @Schema(description = "目标类型：1 文章，2 说说", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer targetType;

    @Schema(description = "目标 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long targetId;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
}
