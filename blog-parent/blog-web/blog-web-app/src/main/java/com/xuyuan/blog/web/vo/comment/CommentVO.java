package com.xuyuan.blog.web.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论视图对象
 */
@Data
@Schema(description = "评论(前台展示用)")
public class CommentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评论 ID */
    @Schema(description = "评论 ID", example = "1")
    private Long id;

    /** 目标类型: 1 文章, 2 说说 */
    @Schema(description = "目标类型(1 文章, 2 说说)", example = "1")
    private Integer targetType;

    /** 目标 ID */
    @Schema(description = "目标 ID(文章 ID 或说说 ID)", example = "100")
    private Long targetId;

    /** 评论内容 */
    @Schema(description = "评论内容", example = "写得很好!")
    private String content;

    /** 评论者昵称 */
    @Schema(description = "评论者昵称", example = "游客小明")
    private String nickname;

    /** 评论者头像 */
    @Schema(description = "评论者头像(URL)")
    private String avatar;

    /** 创建时间 */
    @Schema(description = "评论时间")
    private LocalDateTime createdAt;
}
