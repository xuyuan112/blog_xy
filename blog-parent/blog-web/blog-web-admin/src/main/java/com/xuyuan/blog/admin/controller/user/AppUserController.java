package com.xuyuan.blog.admin.controller.user;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.dto.*;
import com.xuyuan.blog.admin.service.AppUserService;
import com.xuyuan.blog.admin.vo.user.AppUserAdminVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台前台用户管理")
@RestController
@RequestMapping("/xuyuan/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AppUserController {
    private final AppUserService service;

    @GetMapping
    @Operation(summary = "前台用户分页列表")
    public R<PageResult<AppUserAdminVO>> page(@ParameterObject AppUserPageParam param) { return R.ok(service.pageList(param)); }

    @GetMapping("/{id}")
    @Operation(summary = "前台用户详情")
    public R<AppUserAdminVO> detail(@PathVariable Long id) { return R.ok(service.detail(id)); }

    @PutMapping("/{id}/status")
    @Operation(summary = "启用或禁用前台用户")
    public R<Void> status(@PathVariable Long id, @Valid @RequestBody AppUserStatusParam param) { service.changeStatus(id, param); return R.ok(); }

    @PutMapping("/{id}/reset-password")
    @Operation(summary = "重置前台用户密码")
    public R<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody AppUserResetPasswordParam param) { service.resetPassword(id, param); return R.ok(); }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除前台用户")
    public R<Void> delete(@PathVariable Long id) { service.delete(id); return R.ok(); }
}
