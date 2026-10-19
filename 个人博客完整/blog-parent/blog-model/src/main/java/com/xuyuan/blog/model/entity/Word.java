package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一言实体 (t_word)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_word")
@Schema(description = "一言实体(每日金句)")
public class Word extends BaseEntity {

    /** 语句内容 */
    @Schema(description = "语句内容", example = "代码改变世界", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    /** 出处 */
    @Schema(description = "出处", example = "佚名")
    private String source;
}
