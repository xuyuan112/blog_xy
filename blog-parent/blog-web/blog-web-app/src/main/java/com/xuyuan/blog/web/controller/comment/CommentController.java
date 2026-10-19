package com.xuyuan.blog.web.controller.comment;

import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.web.dto.CommentCreateParam;
import com.xuyuan.blog.web.dto.comment.CommentPageQuery;
import com.xuyuan.blog.web.service.comment.CommentService;
import com.xuyuan.blog.web.vo.comment.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "评论互动", description = "文章和说说的评论查询与发布")
@RequestMapping("/xuyuan/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "评论分页列表", description = "仅返回已审核通过的单层评论")
    public R<PageResult<CommentVO>> page(@ParameterObject CommentPageQuery query) {
        return R.ok(commentService.page(query));
    }

    @PostMapping
    @Operation(summary = "发表评论", description = "评论提交后进入待审核状态")
    public R<Map<String, Long>> create(@Valid @RequestBody CommentCreateParam param) {
        try {
            return R.ok(Collections.singletonMap("id", commentService.create(param)));
        } catch (IllegalArgumentException exception) {
            return R.fail(400, exception.getMessage());
        }
    }
}
