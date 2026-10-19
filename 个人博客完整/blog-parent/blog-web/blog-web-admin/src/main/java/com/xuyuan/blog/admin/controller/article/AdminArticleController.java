package com.xuyuan.blog.admin.controller.article;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.dto.ArticleCreateParam;
import com.xuyuan.blog.admin.dto.ArticleQueryParam;
import com.xuyuan.blog.admin.dto.ArticleStatusParam;
import com.xuyuan.blog.admin.dto.IdsParam;
import com.xuyuan.blog.admin.service.AdminArticleService;
import com.xuyuan.blog.admin.vo.article.AdminArticleVO;
import com.xuyuan.blog.admin.vo.article.ArticleDetailVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理端-文章管理控制器 (M3)
 *
 * <p>路径前缀: {@code /xuyuan/admin/articles}。全部 7 个端点严格对齐任务书 & API_SPEC。
 * 全部接口必须携带 Header {@code access-token: <登录返回的 JWT>} (Knife4j 调试卡顶部直接有输入框)。</p>
 */
@Tag(name = "后台文章管理 M3", description = "分页、新建、编辑、详情、批量删除(回收站)、状态/置顶切换、草稿自动保存")
@RestController
@RequestMapping("/xuyuan/admin/articles")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AdminArticleController {

    private static final String JWT_DESC = "登录返回的 accessToken 纯 JWT 字符串 (无需 Bearer 前缀)";

    private final AdminArticleService service;

    // ============================================================
    // ① 分页列表 (7 维筛选 + 置顶/发布时间倒序)
    // ============================================================
    @Operation(summary = "文章分页列表", description = "支持 status/type/keyword 三维筛选, 按「置顶优先 → 发布时间倒序」返回。列表项含分类名、标签数组(用于标签角标)、tagIds(用于编辑页回显)")
    @GetMapping
    public R<PageResult<AdminArticleVO>> pageList(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @ParameterObject ArticleQueryParam param) {
        return R.ok(service.pageList(param));
    }

    // ============================================================
    // ② 新建文章
    // ============================================================
    @Operation(summary = "新建文章", description = "创建新文章并绑定标签; 返回新文章 id。保存按钮触发, 草稿走 /draft")
    @PostMapping
    public R<Map<String, Long>> create(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @Valid @RequestBody ArticleCreateParam param) {
        Long id = service.create(param);
        Map<String, Long> m = new HashMap<>(2);
        m.put("id", id);
        return R.ok(m);
    }

    // ============================================================
    // ③ 编辑文章 (全量更新)
    // ============================================================
    @Operation(summary = "编辑文章", description = "全量更新文章字段 + 重建文章-标签关联关系")
    @PutMapping("/{id}")
    public R<Void> update(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @PathVariable Long id,
            @Valid @RequestBody ArticleCreateParam param) {
        service.update(id, param);
        return R.ok();
    }

    // ============================================================
    // ④ 详情 (含 content 正文 + tagIds 回显)
    // ============================================================
    @Operation(summary = "文章详情(编辑页)", description = "编辑页打开时调用, 返回 Markdown 正文以及 tagIds 数组(用于 Element Plus tag-selector 回显)")
    @GetMapping("/{id}")
    public R<ArticleDetailVO> detail(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @PathVariable Long id) {
        return R.ok(service.detail(id));
    }

    // ============================================================
    // ⑤ 批量删除 (逻辑删除 → status = 3 回收站)
    // ============================================================
    @Operation(summary = "批量删除文章", description = "把 ids 里的文章状态设为 3 (回收站), 不是真删物理表, 可后续在回收站二次操作")
    @DeleteMapping
    public R<Void> deleteBatch(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @Valid @RequestBody IdsParam param) {
        service.deleteBatch(param);
        return R.ok();
    }

    // ============================================================
    // ⑥ 状态/置顶 变更
    // ============================================================
    @Operation(summary = "切换文章状态/置顶", description = "列表页的「发布/私密/回收站」按钮和「置顶」开关统一调用本接口。status 必传, isTop 可选")
    @PutMapping("/{id}/status")
    public R<Void> changeStatus(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @PathVariable Long id,
            @Valid @RequestBody ArticleStatusParam param) {
        service.changeStatus(id, param);
        return R.ok();
    }

    // ============================================================
    // ⑦ 草稿自动保存 (有 id 则更新, 否则新建)
    // ============================================================
    @Operation(summary = "保存草稿(自动保存)", description = "前端编辑器 30 秒自动调用一次。Query 参数 id 可选: 有则更新已有草稿, 无则新建草稿 (强制 status=0 草稿), 返回文章 id 供后续自动保存复用")
    @PostMapping("/draft")
    public R<Map<String, Long>> saveDraft(
            @Parameter(name = "access-token", description = JWT_DESC, required = true)
            @RequestHeader(value = "access-token", required = false) String accessToken,
            @Parameter(name = "id", description = "可选: 已有草稿的文章 ID (自动保存场景), 不传则新建草稿")
            @RequestParam(value = "id", required = false) Long id,
            @RequestBody ArticleCreateParam param) {
        Long savedId = service.saveDraft(id, param);
        Map<String, Long> m = new HashMap<>(2);
        m.put("id", savedId);
        return R.ok(m);
    }
}
