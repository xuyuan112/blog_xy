package com.xuyuan.blog.web.vo.gallery;

import com.xuyuan.blog.model.vo.ExifVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 照片视图对象
 */
@Data
@Schema(description = "照片(前台展示用)")
public class PhotoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 照片 ID */
    @Schema(description = "照片 ID", example = "1")
    private Long id;

    /** 所属相册 ID */
    @Schema(description = "所属相册 ID", example = "1")
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
    private ExifVO exif;
}
