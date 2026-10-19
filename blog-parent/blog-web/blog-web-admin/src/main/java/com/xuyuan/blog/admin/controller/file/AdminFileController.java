package com.xuyuan.blog.admin.controller.file;

import com.xuyuan.blog.admin.service.FileService;
import com.xuyuan.blog.admin.vo.file.FileUploadVO;
import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "后台文件上传", description = "MinIO 文件及图片上传")
@RestController
@RequestMapping("/xuyuan/admin/files")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AdminFileController {
    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "通用文件上传", description = "上传任意类型文件，scene 可选 article/avatar/moment/gallery/comment")
    public R<FileUploadVO> upload(
            @Parameter(description = "上传文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "业务场景：article、avatar、moment、gallery、comment") @RequestParam("scene") String scene) {
        return R.ok(fileService.upload(file, scene, false));
    }

    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "图片上传", description = "仅支持 jpg/jpeg/png/gif/webp，并返回原图和缩略图 URL")
    public R<FileUploadVO> uploadImage(
            @Parameter(description = "图片文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "业务场景，默认 article") @RequestParam(value = "scene", defaultValue = "article") String scene) {
        return R.ok(fileService.upload(file, scene, true));
    }
}
