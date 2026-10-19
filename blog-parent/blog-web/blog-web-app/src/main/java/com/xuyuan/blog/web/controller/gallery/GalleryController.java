package com.xuyuan.blog.web.controller.gallery;

import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.web.dto.gallery.GalleryPageQuery;
import com.xuyuan.blog.web.service.gallery.GalleryService;
import com.xuyuan.blog.web.vo.gallery.GalleryDetailVO;
import com.xuyuan.blog.web.vo.gallery.GalleryVO;
import com.xuyuan.blog.web.vo.gallery.PhotoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "照片墙", description = "相册列表、相册详情和照片 EXIF")
@RequestMapping("/xuyuan")
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping("/galleries")
    @Operation(summary = "相册分页列表", description = "支持相册名称和描述关键词筛选")
    public R<PageResult<GalleryVO>> page(@ParameterObject GalleryPageQuery query) {
        return R.ok(galleryService.page(query));
    }

    @GetMapping("/galleries/{id}")
    @Operation(summary = "相册详情", description = "返回相册信息和按 sort 排序的照片列表")
    public R<GalleryDetailVO> detail(
            @Parameter(description = "相册 ID") @PathVariable Long id
    ) {
        GalleryDetailVO result = galleryService.detail(id);
        return result == null ? R.fail(404, "相册不存在") : R.ok(result);
    }

    @GetMapping("/photos/{id}")
    @Operation(summary = "照片详情", description = "返回原图、缩略图、拍摄时间和 EXIF")
    public R<PhotoVO> photo(
            @Parameter(description = "照片 ID") @PathVariable Long id
    ) {
        PhotoVO result = galleryService.photo(id);
        return result == null ? R.fail(404, "照片不存在") : R.ok(result);
    }
}
