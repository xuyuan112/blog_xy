package com.xuyuan.blog.web.vo.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 相册详情视图对象(含照片列表)
 */
@Data
@Schema(description = "相册详情(含照片列表)")
public class GalleryDetailVO implements Serializable {

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

    /** 拍摄日期 */
    @Schema(description = "拍摄日期", example = "2024-07-15")
    private LocalDate shootDate;

    /** 照片列表 */
    @Schema(description = "照片列表(按 sort 排序)")
    private List<PhotoVO> photos;
}
