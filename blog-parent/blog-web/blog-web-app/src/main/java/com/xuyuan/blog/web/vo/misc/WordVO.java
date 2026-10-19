package com.xuyuan.blog.web.vo.misc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 一言(每日金句)视图对象
 */
@Data
@Schema(description = "一言(每日金句)")
public class WordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 语句内容 */
    @Schema(description = "语句内容", example = "代码改变世界")
    private String content;

    /** 出处 */
    @Schema(description = "出处", example = "佚名")
    private String source;
}
