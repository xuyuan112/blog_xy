package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站长公告实体 (t_notice)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_notice")
@Schema(description = "站长公告实体")
public class Notice extends BaseEntity {

    /** 公告内容 */
    @Schema(description = "公告内容", example = "本站新增相册功能", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
