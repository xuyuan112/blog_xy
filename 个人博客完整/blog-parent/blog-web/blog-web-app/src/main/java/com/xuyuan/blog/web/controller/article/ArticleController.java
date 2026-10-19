package com.xuyuan.blog.web.controller.article;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "文章浏览", description = "前台已发布文章的分页、详情和热门排行")
@RequestMapping("/xuyuan/articles")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "文章分页列表", description = "仅返回已发布文章，支持分类、标签、关键词和内容类型筛选")
    public R<PageResult<ArticleListItemVO>> page(@ParameterObject ArticlePageQuery query) {
        return R.ok(articleService.page(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "文章详情", description = "返回 Markdown 正文、目录和上下篇导航")
    public R<ArticleDetailVO> detail(@Parameter(description = "文章 ID") @PathVariable Long id) {
        ArticleDetailVO result = articleService.detail(id);
        return result == null ? R.fail(404, "文章不存在或未发布") : R.ok(result);
    }

    @GetMapping("/hot")
    @Operation(summary = "热门文章 Top N")
    public R<List<ArticleListItemVO>> hot(@RequestParam(defaultValue = "5") Integer limit) {
        return R.ok(articleService.hot(limit));
    }
}
