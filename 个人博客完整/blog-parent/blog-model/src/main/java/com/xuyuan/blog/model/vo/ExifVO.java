package com.xuyuan.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 照片 EXIF 参数视图对象
 *
 * <p>由 metadata-extractor 从上传原图解析得到, 以 JSON 形式存储于 t_photo.exif 字段。</p>
 */
@Data
@Schema(description = "照片 EXIF 参数")
public class ExifVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 相机型号 */
    @Schema(description = "相机型号", example = "Canon EOS 5D Mark IV")
    private String cameraModel;

    /** 感光度 ISO */
    @Schema(description = "感光度 ISO", example = "100")
    private Integer iso;

    /** 快门速度, 如 1/250 */
    @Schema(description = "快门速度", example = "1/250")
    private String shutterSpeed;

    /** 光圈, 如 f/2.8 */
    @Schema(description = "光圈", example = "f/2.8")
    private String aperture;

    /** 焦距, 如 35mm */
    @Schema(description = "焦距", example = "35mm")
    private String focalLength;

    /** 镜头型号 */
    @Schema(description = "镜头型号", example = "EF 24-70mm f/2.8L")
    private String lensModel;
}
