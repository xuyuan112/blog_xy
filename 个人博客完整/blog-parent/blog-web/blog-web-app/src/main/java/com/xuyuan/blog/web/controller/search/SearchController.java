package com.xuyuan.blog.web.controller.search;

import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.web.dto.article.ArticlePageQuery;
import com.xuyuan.blog.web.service.article.ArticleService;
import com.xuyuan.blog.web.vo.article.ArticleListItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "全站搜索")
@RequestMapping("/xuyuan/search")
public class SearchController {
    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "搜索已发布文章")
    public R<PageResult<ArticleListItemVO>> search(@ModelAttribute ArticlePageQuery query) {
        // Global search covers both technical articles and thoughts.
        query.setType(null);
        return R.ok(articleService.page(query));
    }
}
