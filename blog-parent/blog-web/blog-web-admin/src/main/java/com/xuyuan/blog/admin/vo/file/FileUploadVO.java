package com.xuyuan.blog.admin.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传结果视图对象
 */
@Data
@Schema(description = "文件上传结果")
public class FileUploadVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文件访问地址 */
    @Schema(description = "文件访问地址(URL)", example = "https://example.com/files/abc.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    /** 文件名 */
    @Schema(description = "存储文件名(唯一)", example = "abc123-def456.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileName;

    /** 文件大小(字节) */
    @Schema(description = "文件大小(字节)", example = "204800", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long size;

    /** 图片缩略图地址，图片上传时返回 */
    private String thumbnailUrl;
}
