package com.xuyuan.blog.web.service.article;

import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.web.dto.article.ArticlePageQuery;
import com.xuyuan.blog.web.vo.article.ArticleDetailVO;
import com.xuyuan.blog.web.vo.article.ArticleListItemVO;
import java.util.List;

public interface ArticleService {
    PageResult<ArticleListItemVO> page(ArticlePageQuery query);

    PageResult<ArticleListItemVO> pageByType(ArticlePageQuery query, Integer type);

    ArticleDetailVO detail(Long id);

    ArticleDetailVO detailByType(Long id, Integer type);
    List<ArticleListItemVO> hot(Integer limit);
}
