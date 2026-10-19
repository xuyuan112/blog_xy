package com.xuyuan.blog.web.vo.moment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 说说视图对象
 */
@Data
@Schema(description = "说说(前台展示用)")
public class MomentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 说说 ID */
    @Schema(description = "说说 ID", example = "1")
    private Long id;

    /** 内容 */
    @Schema(description = "内容", example = "今天天气真好")
    private String content;

    /** 图片数组 */
    @Schema(description = "图片数组(URL 列表, 最多 9 张)")
    private List<String> images;

    /** 发布定位 */
    @Schema(description = "发布定位", example = "杭州西湖")
    private String location;

    /** 心情 Tag */
    @Schema(description = "心情标签", example = "happy")
    private String mood;

    /** 点赞数 */
    @Schema(description = "点赞数", example = "10")
    private Long likeCount;

    /** 评论数 */
    @Schema(description = "评论数", example = "3")
    private Long commentCount;

    /** 创建时间 */
    @Schema(description = "发布时间")
    private LocalDateTime createdAt;
}
