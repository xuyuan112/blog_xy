package com.xuyuan.blog.admin.controller.moment;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.dto.MomentCreateParam;
import com.xuyuan.blog.admin.dto.MomentQueryParam;
import com.xuyuan.blog.admin.service.AdminMomentService;
import com.xuyuan.blog.admin.vo.moment.AdminMomentVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Tag(name = "后台说说管理", description = "说说发布、编辑、分页查询和删除")
@RestController
@RequestMapping("/xuyuan/admin/moments")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AdminMomentController {

    private final AdminMomentService service;

    @GetMapping
    @Operation(summary = "说说分页列表", description = "分页查询全部说说，包含隐藏和私密内容")
    public R<PageResult<AdminMomentVO>> pageList(@ParameterObject MomentQueryParam param) {
        return R.ok(service.pageList(param));
    }

    @PostMapping
    @Operation(summary = "发布说说", description = "发布文本说说，图片数量最多 9 张")
    public R<Map<String, Long>> create(@Valid @RequestBody MomentCreateParam param) {
        return R.ok(Collections.singletonMap("id", service.create(param)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑说说", description = "更新说说内容、图片、位置、心情和可见性")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody MomentCreateParam param) {
        service.update(id, param);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除说说", description = "对指定说说执行逻辑删除")
    public R<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return R.ok();
    }
}
