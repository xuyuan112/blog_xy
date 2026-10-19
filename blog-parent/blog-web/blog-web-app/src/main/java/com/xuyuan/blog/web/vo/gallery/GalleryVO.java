package com.xuyuan.blog.web.vo.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 相册视图对象
 */
@Data
@Schema(description = "相册(列表项)")
public class GalleryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 相册 ID */
    @Schema(description = "相册 ID", example = "1")
    private Long id;

    /** 相册名称 */
    @Schema(description = "相册名称", example = "2024 夏日旅行")
    private String name;

    /** 相册描述 */
    @Schema(description = "相册描述", example = "记录夏日旅行的美好瞬间")
    private String description;

    /** 封面图 */
    @Schema(description = "封面图(URL)", example = "https://example.com/gallery-cover.jpg")
    private String cover;

    /** 照片数量 */
    @Schema(description = "照片数量", example = "32")
    private Long photoCount;

    /** 拍摄日期 */
    @Schema(description = "拍摄日期", example = "2024-07-15")
    private LocalDate shootDate;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
