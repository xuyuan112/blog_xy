package com.xuyuan.blog.web.vo.misc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 站长公告视图对象
 */
@Data
@Schema(description = "站长公告(前台展示用)")
public class NoticeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 公告 ID */
    @Schema(description = "公告 ID", example = "1")
    private Long id;

    /** 公告内容 */
    @Schema(description = "公告内容", example = "本站新增相册功能")
    private String content;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
