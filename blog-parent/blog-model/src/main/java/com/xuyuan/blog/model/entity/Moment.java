package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import com.xuyuan.blog.model.handler.StringListTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 说说实体 (t_moment)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_moment", autoResultMap = true)
@Schema(description = "说说实体")
public class Moment extends BaseEntity {

    /** 说说内容 */
    @Schema(description = "说说内容", example = "今天天气真好", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    /** 图片数组(最多 9 张) */
    @Schema(description = "图片数组(最多 9 张, URL 列表)")
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> images;

    /** 发布定位 */
    @Schema(description = "发布定位", example = "杭州西湖")
    private String location;

    /** 心情 Tag */
    @Schema(description = "心情标签", example = "happy")
    private String mood;

    /** 可见性: 0 公开, 1 隐藏, 2 私密 */
    @Schema(description = "可见性(0 公开, 1 隐藏, 2 私密)", example = "0")
    private Integer visible;

    /** 点赞数 */
    @Schema(description = "点赞数", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private Long likeCount;

    /** 评论数 */
    @Schema(description = "评论数", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private Long commentCount;
}
