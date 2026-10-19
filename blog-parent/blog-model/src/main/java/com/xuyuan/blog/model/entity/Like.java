package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点赞记录实体 (t_like)
 *
 * <p>防刷点赞, 通过 (target_type, target_id, ip) 唯一约束去重。</p>
 */
@Data
@TableName("t_like")
@Schema(description = "点赞记录实体(防刷去重)")
public class Like implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(description = "主键 ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 目标类型: 1 文章, 2 说说 */
    @Schema(description = "目标类型(1 文章, 2 说说)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer targetType;

    /** 目标 ID */
    @Schema(description = "目标 ID", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long targetId;

    /** 点赞者 IP(防刷标识) */
    @Schema(description = "点赞者 IP(防刷标识)", example = "192.168.1.1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ip;

    /** 点赞用户 ID(未登录为 NULL) */
    @Schema(description = "点赞用户 ID(未登录为 NULL)", example = "1")
    private Long userId;

    /** 创建时间 */
    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}
