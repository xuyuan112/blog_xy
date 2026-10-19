package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章访问日志实体 (t_article_view_log)
 *
 * <p>用于阅读量统计、防刷计数与访问趋势分析。</p>
 */
@Data
@TableName("t_article_view_log")
@Schema(description = "文章访问日志实体(用于阅读量统计与防刷)")
public class ArticleViewLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(description = "主键 ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章 ID */
    @Schema(description = "文章 ID", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long articleId;

    /** 访问者 IP */
    @Schema(description = "访问者 IP", example = "192.168.1.1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ip;

    /** UA */
    @Schema(description = "User-Agent", example = "Mozilla/5.0...")
    private String userAgent;

    /** 访问时间 */
    @Schema(description = "访问时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}
