package com.xuyuan.blog.admin.controller.gallery;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.dto.GalleryCreateParam;
import com.xuyuan.blog.admin.dto.GalleryQueryParam;
import com.xuyuan.blog.admin.service.AdminGalleryService;
import com.xuyuan.blog.admin.vo.gallery.AdminGalleryVO;
import com.xuyuan.blog.admin.vo.gallery.AdminPhotoVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Tag(name = "后台相册管理", description = "相册分页、新建、编辑、删除及照片上传")
@RestController
@RequestMapping("/xuyuan/admin/galleries")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AdminGalleryController {

    private final AdminGalleryService service;

    @GetMapping
    @Operation(summary = "相册分页列表", description = "按相册名称关键词分页查询相册")
    public R<PageResult<AdminGalleryVO>> pageList(@ParameterObject GalleryQueryParam param) {
        return R.ok(service.pageList(param));
    }

    @PostMapping
    @Operation(summary = "新建相册", description = "创建相册基本信息")
    public R<Map<String, Long>> create(@Valid @RequestBody GalleryCreateParam param) {
        Map<String, Long> result = new HashMap<>(1);
        result.put("id", service.create(param));
        return R.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑相册", description = "更新相册名称、描述、封面和拍摄日期")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody GalleryCreateParam param) {
        service.update(id, param);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除相册", description = "删除相册及其关联照片")
    public R<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return R.ok();
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "批量上传照片", description = "向指定相册批量上传图片文件")
    public R<Map<String, Object>> uploadPhotos(
            @PathVariable Long id,
            @Parameter(description = "待上传的图片文件，可选择多个", required = true)
            @RequestParam("files") MultipartFile[] files) {
        List<AdminPhotoVO> photos = service.upload(id, List.of(files));
        Map<String, Object> result = new HashMap<>(2);
        result.put("galleryId", id);
        result.put("photos", photos);
        return R.ok(result);
    }

    @GetMapping("/{id}/photos")
    @Operation(summary = "相册照片列表", description = "查询指定相册下的全部照片")
    public R<List<AdminPhotoVO>> listPhotos(@PathVariable Long id) {
        return R.ok(service.listPhotos(id));
    }
}
