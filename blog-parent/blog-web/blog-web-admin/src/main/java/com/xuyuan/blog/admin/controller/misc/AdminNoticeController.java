package com.xuyuan.blog.admin.controller.misc;

import com.xuyuan.blog.admin.dto.NoticeCreateParam;
import com.xuyuan.blog.admin.service.AdminNoticeService;
import com.xuyuan.blog.admin.vo.misc.AdminNoticeVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Tag(name = "后台公告管理")
@RestController
@RequestMapping("/xuyuan/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final AdminNoticeService service;

    @GetMapping
    @Operation(summary = "公告分页列表")
    public R<PageResult<AdminNoticeVO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        return R.ok(service.pageList(page, pageSize));
    }

    @PostMapping
    @Operation(summary = "新增公告")
    public R<Map<String, Long>> create(@Valid @RequestBody NoticeCreateParam param) {
        return R.ok(Collections.singletonMap("id", service.create(param)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑公告")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody NoticeCreateParam param) {
        service.update(id, param);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告")
    public R<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return R.ok();
    }
}
