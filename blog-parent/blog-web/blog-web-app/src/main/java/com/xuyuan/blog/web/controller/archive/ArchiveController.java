package com.xuyuan.blog.web.controller.archive;

import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.web.service.archive.ArchiveService;
import com.xuyuan.blog.web.vo.misc.CategoryVO;
import com.xuyuan.blog.web.vo.misc.TagVO;
import com.xuyuan.blog.web.vo.timeline.TimelineYearVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "文章归档", description = "分类、标签与时间线归档")
@RequestMapping("/xuyuan")
public class ArchiveController {

    private final ArchiveService archiveService;

    @GetMapping("/categories")
    @Operation(summary = "分类列表")
    public R<List<CategoryVO>> categories() {
        return R.ok(archiveService.categories());
    }

    @GetMapping("/tags")
    @Operation(summary = "标签列表")
    public R<List<TagVO>> tags() {
        return R.ok(archiveService.tags());
    }

    @GetMapping("/archives/timeline")
    @Operation(summary = "时间线归档")
    public R<List<TimelineYearVO>> timeline() {
        return R.ok(archiveService.timeline());
    }
}
