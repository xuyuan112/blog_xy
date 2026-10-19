package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 相册实体 (t_gallery)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_gallery")
@Schema(description = "相册实体")
public class Gallery extends BaseEntity {

    /** 相册名称 */
    @Schema(description = "相册名称", example = "2024 夏日旅行", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 相册描述 */
    @Schema(description = "相册描述", example = "记录夏日旅行的美好瞬间")
    private String description;

    /** 相册封面图地址 */
    @Schema(description = "相册封面图地址(URL)", example = "https://example.com/gallery-cover.jpg")
    private String cover;

    /** 拍摄日期 */
    @Schema(description = "拍摄日期", example = "2024-07-15")
    private LocalDate shootDate;

    /** 排序值 */
    @Schema(description = "排序值(越小越靠前)", example = "1")
    private Integer sort;
}
