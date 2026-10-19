package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import com.xuyuan.blog.model.handler.ExifTypeHandler;
import com.xuyuan.blog.model.vo.ExifVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 照片实体 (t_photo)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_photo", autoResultMap = true)
@Schema(description = "照片实体")
public class Photo extends BaseEntity {

    /** 所属相册 ID */
    @Schema(description = "所属相册 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long galleryId;

    /** 原图地址 */
    @Schema(description = "原图地址(URL)", example = "https://example.com/photo-original.jpg")
    private String original;

    /** 缩略图地址 */
    @Schema(description = "缩略图地址(URL)", example = "https://example.com/photo-thumb.jpg")
    private String thumbnail;

    /** 拍摄时间 */
    @Schema(description = "拍摄时间")
    private LocalDateTime shootTime;

    /** EXIF 参数 */
    @Schema(description = "EXIF 参数(相机/镜头/光圈等)")
    @TableField(typeHandler = ExifTypeHandler.class)
    private ExifVO exif;

    /** 排序值 */
    @Schema(description = "排序值(越小越靠前)", example = "1")
    private Integer sort;
}
