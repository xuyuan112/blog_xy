package com.xuyuan.blog.admin.controller.category;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.dto.CategoryCreateParam;
import com.xuyuan.blog.admin.service.AdminCategoryService;
import com.xuyuan.blog.admin.vo.article.CategoryVO;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理端-分类管理控制器 (M3)
 *
 * <p>路径前缀: {@code /xuyuan/admin/categories}。4 端点 + 删除校验 313(该分类下有文章不可删)。</p>
 */
@Tag(name = "后台分类管理 M3", description = "分类 CRUD, 删除前自动校验该分类下是否有文章(回收站除外)")
@RestController
@RequestMapping("/xuyuan/admin/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AdminCategoryController {

    private static final String JWT_DESC = "登录返回的 accessToken 纯 JWT 字符串 (无需 Bearer 前缀)";

    private final AdminCategoryService service;

    @Operation(summary = "分类列表", description = "不分页, 按 sort 升序 + id 升序, 含每个分类的文章数统计")
    @GetMapping
    public R<List<CategoryVO>> listAll(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken) {
        return R.ok(service.listAll());
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public R<Map<String, Long>> create(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @Valid @RequestBody CategoryCreateParam param) {
        Long id = service.create(param);
        Map<String, Long> m = new HashMap<>(2);
        m.put("id", id);
        return R.ok(m);
    }

    @Operation(summary = "编辑分类")
    @PutMapping("/{id}")
    public R<Void> update(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @PathVariable Long id,
            @Valid @RequestBody CategoryCreateParam param) {
        service.update(id, param);
        return R.ok();
    }

    @Operation(summary = "删除分类", description = "若该分类下还有非回收站的文章, 返回 code=313「请先删除该分类下的文章」")
    @DeleteMapping("/{id}")
    public R<Void> delete(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @PathVariable Long id) {
        service.delete(id);
        return R.ok();
    }
}
