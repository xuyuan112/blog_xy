package com.xuyuan.blog.admin.controller.gallery;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.service.AdminGalleryService;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台照片管理", description = "照片删除")
@RestController
@RequestMapping("/xuyuan/admin/photos")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AdminPhotoController {

    private final AdminGalleryService service;

    @DeleteMapping("/{id}")
    @Operation(summary = "删除照片", description = "对指定照片执行逻辑删除")
    public R<Void> delete(@PathVariable Long id) {
        service.deletePhoto(id);
        return R.ok();
    }
}
