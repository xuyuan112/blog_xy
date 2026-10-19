package com.xuyuan.blog.web.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 点赞切换结果视图对象
 */
@Data
@Schema(description = "点赞切换结果(点赞/取消点赞后返回最新状态)")
public class LikeToggleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目标 ID */
    @Schema(description = "目标 ID(文章 ID 或说说 ID)", example = "100")
    private Long targetId;

    /** 是否已点赞 */
    @Schema(description = "当前用户是否已点赞(true 已点赞, false 已取消)", example = "true")
    private Boolean liked;

    /** 最新点赞数 */
    @Schema(description = "最新点赞数", example = "51")
    private Long likeCount;
}
