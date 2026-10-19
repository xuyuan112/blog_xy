package com.xuyuan.blog.web.service.article.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.model.entity.Article;
import com.xuyuan.blog.model.entity.Tag;
import com.xuyuan.blog.web.dto.article.ArticlePageQuery;
import com.xuyuan.blog.web.mapper.ArticleMapper;
import com.xuyuan.blog.web.mapper.ArticleTagMapper;
import com.xuyuan.blog.web.mapper.TagMapper;
import com.xuyuan.blog.web.service.article.ArticleService;
import com.xuyuan.blog.web.vo.article.ArticleDetailVO;
import com.xuyuan.blog.web.vo.article.ArticleListItemVO;
import com.xuyuan.blog.web.vo.article.ArticleNavVO;
import com.xuyuan.blog.web.vo.misc.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private static final Pattern HEADING = Pattern.compile("^(#{2,3})\\s+(.+?)\\s*$", Pattern.MULTILINE);
    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagMapper tagMapper;

    @Override
    public PageResult<ArticleListItemVO> page(ArticlePageQuery query) {
        normalize(query);
        IPage<ArticleListItemVO> result = articleMapper.pagePublished(new Page<>(query.getPage(), query.getPageSize()), query);
        enrichTags(result.getRecords());
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<ArticleListItemVO> pageByType(ArticlePageQuery query, Integer type) {
        ArticlePageQuery effectiveQuery = query == null ? new ArticlePageQuery() : query;
        effectiveQuery.setType(type);
        return page(effectiveQuery);
    }

    @Override
    public ArticleDetailVO detail(Long id) {
        return detailByType(id, null);
    }

    @Override
    public ArticleDetailVO detailByType(Long id, Integer type) {
        Article article = articleMapper.selectById(id);
        if (article == null || !Objects.equals(article.getStatus(), 1)
                || article.getDeleted() == 1
                || (type != null && !Objects.equals(article.getType(), type))) {
            return null;
        }
        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtils.copyProperties(article, vo);
        vo.setIsTop(Objects.equals(article.getIsTop(), 1));
        vo.setTags(loadTags(id));
        vo.setToc(parseToc(article.getContent()));
        vo.setPrevArticle(articleMapper.previous(article.getType(), article.getPublishedAt()));
        vo.setNextArticle(articleMapper.next(article.getType(), article.getPublishedAt()));
        return vo;
    }

    @Override
    public List<ArticleListItemVO> hot(Integer limit) {
        int size = limit == null ? 5 : Math.max(1, Math.min(limit, 20));
        return articleMapper.hot(size);
    }

    private void normalize(ArticlePageQuery q) {
        if (q.getPage() == null || q.getPage() < 1) q.setPage(1);
        if (q.getPageSize() == null || q.getPageSize() < 1) q.setPageSize(10);
        q.setPageSize(Math.min(q.getPageSize(), 50));
    }

    private void enrichTags(List<ArticleListItemVO> records) {
        for (ArticleListItemVO item : records) {
            item.setTags(loadTags(item.getId()));
        }
    }

    private List<TagVO> loadTags(Long articleId) {
        List<Long> ids = articleTagMapper.listTagIds(articleId);
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        Map<Long, Tag> tags = tagMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Tag::getId, Function.identity()));
        return ids.stream().map(tags::get).filter(Objects::nonNull).map(tag -> {
            TagVO vo = new TagVO();
            BeanUtils.copyProperties(tag, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private List<ArticleDetailVO.TocItem> parseToc(String content) {
        if (!StringUtils.hasText(content)) return Collections.emptyList();
        List<ArticleDetailVO.TocItem> result = new ArrayList<>();
        Matcher matcher = HEADING.matcher(content);
        while (matcher.find()) {
            ArticleDetailVO.TocItem item = new ArticleDetailVO.TocItem();
            item.setLevel(matcher.group(1).length());
            item.setText(matcher.group(2));
            item.setAnchor(matcher.group(2).trim().replaceAll("\\s+", "-"));
            result.add(item);
        }
        return result;
    }
}
