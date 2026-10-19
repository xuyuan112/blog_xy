package com.xuyuan.blog.admin.controller.comment;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.dto.CommentQueryParam;
import com.xuyuan.blog.admin.dto.CommentStatusParam;
import com.xuyuan.blog.admin.service.AdminCommentService;
import com.xuyuan.blog.admin.vo.comment.AdminCommentVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台评论管理")
@RestController
@RequestMapping("/xuyuan/admin/comments")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AdminCommentController {
    private final AdminCommentService service;

    @GetMapping
    @Operation(summary = "评论分页列表", description = "按目标类型和评论状态筛选，按创建时间倒序返回评论")
    public R<PageResult<AdminCommentVO>> pageList(@ParameterObject CommentQueryParam param) {
        return R.ok(service.pageList(param));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "审核或隐藏评论", description = "将评论状态更新为待审核、已发布或已隐藏")
    public R<Void> changeStatus(@PathVariable Long id, @Valid @RequestBody CommentStatusParam param) {
        service.changeStatus(id, param);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "对指定评论执行逻辑删除")
    public R<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return R.ok();
    }
}
