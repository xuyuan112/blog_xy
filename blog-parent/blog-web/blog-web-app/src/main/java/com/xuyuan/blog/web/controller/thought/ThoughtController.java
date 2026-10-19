package com.xuyuan.blog.web.controller.thought;

import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.web.dto.article.ArticlePageQuery;
import com.xuyuan.blog.web.service.article.ArticleService;
import com.xuyuan.blog.web.vo.article.ArticleDetailVO;
import com.xuyuan.blog.web.vo.article.ArticleListItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "杂谈展示", description = "人文随笔内容的分页列表和详情")
@RequestMapping("/xuyuan/thoughts")
public class ThoughtController {

    private static final int THOUGHT_TYPE = 2;

    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "杂谈分页列表", description = "固定查询内容类型为 2 的已发布文章")
    public R<PageResult<ArticleListItemVO>> page(@ParameterObject ArticlePageQuery query) {
        return R.ok(articleService.pageByType(query, THOUGHT_TYPE));
    }

    @GetMapping("/{id}")
    @Operation(summary = "杂谈详情", description = "仅允许访问 type=2 且已发布的杂谈")
    public R<ArticleDetailVO> detail(
            @Parameter(description = "杂谈文章 ID") @PathVariable Long id
    ) {
        ArticleDetailVO result = articleService.detailByType(id, THOUGHT_TYPE);
        return result == null ? R.fail(404, "杂谈不存在或未发布") : R.ok(result);
    }
}
