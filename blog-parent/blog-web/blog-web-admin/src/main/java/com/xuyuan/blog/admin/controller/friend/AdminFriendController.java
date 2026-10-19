package com.xuyuan.blog.admin.controller.friend;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.dto.*;
import com.xuyuan.blog.admin.service.AdminFriendService;
import com.xuyuan.blog.admin.vo.friend.AdminFriendVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台友链管理", description = "友情链接的分页、编辑、审核、排序和删除")
@RestController
@RequestMapping("/xuyuan/admin/friends")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AdminFriendController {
    private final AdminFriendService service;

    @GetMapping
    @Operation(summary = "友链分页列表", description = "查询全部友链，支持按审核状态筛选")
    public R<PageResult<AdminFriendVO>> pageList(@ParameterObject FriendQueryParam param) { return R.ok(service.pageList(param)); }

    @PostMapping
    @Operation(summary = "新增友链", description = "创建一条友情链接记录")
    public R<Long> create(@Valid @RequestBody FriendCreateParam param) { return R.ok(service.create(param)); }

    @PutMapping("/{id}")
    @Operation(summary = "编辑友链", description = "更新友情链接名称、地址、头像和分组等信息")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody FriendCreateParam param) { service.update(id, param); return R.ok(); }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除友链", description = "对指定友情链接执行逻辑删除")
    public R<Void> delete(@PathVariable Long id) { service.delete(id); return R.ok(); }

    @PutMapping("/{id}/audit")
    @Operation(summary = "审核友链", description = "将友链审核状态设置为通过或拒绝")
    public R<Void> audit(@PathVariable Long id, @Valid @RequestBody FriendAuditParam param) { service.audit(id, param); return R.ok(); }

    @PutMapping("/{id}/sort")
    @Operation(summary = "调整友链排序", description = "更新友链排序值，数值越小越靠前")
    public R<Void> updateSort(@PathVariable Long id, @Valid @RequestBody SortParam param) { service.updateSort(id, param); return R.ok(); }
}
