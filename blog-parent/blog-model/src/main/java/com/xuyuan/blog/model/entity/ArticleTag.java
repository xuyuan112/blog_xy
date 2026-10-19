package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章-标签关联实体 (t_article_tag)
 *
 * <p>多对多关联表, 无审计字段。</p>
 */
@Data
@TableName("t_article_tag")
@Schema(description = "文章-标签关联实体(多对多关联表)")
public class ArticleTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(description = "主键 ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章 ID */
    @Schema(description = "文章 ID", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long articleId;

    /** 标签 ID */
    @Schema(description = "标签 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tagId;
}
