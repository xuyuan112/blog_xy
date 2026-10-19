package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论实体 (t_comment)
 *
 * <p>文章与说说共用, 通过 {@link #targetType} 区分。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_comment")
@Schema(description = "评论实体(文章与说说共用)")
public class Comment extends BaseEntity {

    /** 目标类型: 1 文章, 2 说说 */
    @Schema(description = "目标类型(1 文章, 2 说说)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer targetType;

    /** 目标 ID */
    @Schema(description = "目标 ID(文章 ID 或说说 ID)", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long targetId;

    /** 评论内容 */
    @Schema(description = "评论内容", example = "写得很好!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    /** 评论者昵称 */
    @Schema(description = "评论者昵称", example = "游客小明", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;

    /** 评论者头像 */
    @Schema(description = "评论者头像(URL)")
    private String avatar;

    /** 评论者邮箱 */
    @Schema(description = "评论者邮箱(用于通知)", example = "guest@example.com")
    private String email;

    /** 评论者站点 */
    @Schema(description = "评论者站点(URL)", example = "https://example.com")
    private String website;

    /** 状态: 0 待审核, 1 已发布, 2 已隐藏 */
    @Schema(description = "状态(0 待审核, 1 已发布, 2 已隐藏)", example = "0")
    private Integer status;
}
