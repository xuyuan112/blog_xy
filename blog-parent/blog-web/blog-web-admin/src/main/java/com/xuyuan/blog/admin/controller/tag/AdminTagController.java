package com.xuyuan.blog.admin.controller.tag;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.dto.TagCreateParam;
import com.xuyuan.blog.admin.service.AdminTagService;
import com.xuyuan.blog.admin.vo.article.TagVO;
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
 * 后台管理端-标签管理控制器 (M3)
 *
 * <p>路径前缀: {@code /xuyuan/admin/tags}。4 端点 + 删除时先解除文章关联。</p>
 */
@Tag(name = "后台标签管理 M3", description = "标签 CRUD, 删除时自动解除所有文章的标签关联, 不影响文章内容")
@RestController
@RequestMapping("/xuyuan/admin/tags")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AdminTagController {

    private static final String JWT_DESC = "登录返回的 accessToken 纯 JWT 字符串 (无需 Bearer 前缀)";

    private final AdminTagService service;

    @Operation(summary = "标签列表", description = "不分页, 按 id 升序, 含每个标签绑定的文章总数")
    @GetMapping
    public R<List<TagVO>> listAll(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken) {
        return R.ok(service.listAll());
    }

    @Operation(summary = "新增标签")
    @PostMapping
    public R<Map<String, Long>> create(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @Valid @RequestBody TagCreateParam param) {
        Long id = service.create(param);
        Map<String, Long> m = new HashMap<>(2);
        m.put("id", id);
        return R.ok(m);
    }

    @Operation(summary = "编辑标签")
    @PutMapping("/{id}")
    public R<Void> update(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @PathVariable Long id,
            @Valid @RequestBody TagCreateParam param) {
        service.update(id, param);
        return R.ok();
    }

    @Operation(summary = "删除标签", description = "先删除 t_article_tag 中该标签的所有关联, 再逻辑删除 t_tag 本身")
    @DeleteMapping("/{id}")
    public R<Void> delete(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @PathVariable Long id) {
        service.delete(id);
        return R.ok();
    }
}
