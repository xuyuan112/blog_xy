package com.xuyuan.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuyuan.blog.admin.dto.CategoryCreateParam;
import com.xuyuan.blog.admin.mapper.ArticleMapper;
import com.xuyuan.blog.admin.mapper.CategoryMapper;
import com.xuyuan.blog.admin.service.AdminCategoryService;
import com.xuyuan.blog.admin.vo.article.CategoryVO;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.model.entity.Article;
import com.xuyuan.blog.model.entity.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 后台-分类业务实现
 *
 * <p>核心校验: 删除时检查 {@code t_article WHERE category_id = id AND deleted=0 AND status <> 3} 是否存在,
 * 存在则抛 {@link ResultCode#ADMIN_CATEGORY_HAS_ARTICLES (313)}。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    @Override
    public List<CategoryVO> listAll() {
        List<Category> list = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSort)
                        .orderByAsc(Category::getId)
        );
        if (list.isEmpty()) return Collections.emptyList();

        // 聚合每个分类的文章数 (仅统计状态不是回收站的)
        List<Long> cids = list.stream().map(Category::getId).collect(Collectors.toList());
        List<Article> counts = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .in(Article::getCategoryId, cids)
                        .ne(Article::getStatus, 3)
                        .select(Article::getCategoryId)
        );
        Map<Long, Long> cntMap = counts.stream()
                .collect(Collectors.groupingBy(Article::getCategoryId, Collectors.counting()));

        return list.stream().map(c -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(c, vo);
            vo.setArticleCount(cntMap.getOrDefault(c.getId(), 0L));
            if (c.getCreateTime() != null) vo.setCreateTime(c.getCreateTime().format(DTF));
            if (c.getUpdateTime() != null) vo.setUpdateTime(c.getUpdateTime().format(DTF));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CategoryCreateParam param) {
        Category c = new Category();
        BeanUtils.copyProperties(param, c);
        if (c.getSort() == null) c.setSort(0);
        categoryMapper.insert(c);
        log.info("[分类新增] id={}, name={}", c.getId(), c.getName());
        return c.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, CategoryCreateParam param) {
        Category exists = categoryMapper.selectById(id);
        if (exists == null) throw ResultCode.ADMIN_CATEGORY_NOT_FOUND.toException();
        exists.setName(param.getName());
        exists.setDescription(param.getDescription());
        if (param.getSort() != null) exists.setSort(param.getSort());
        categoryMapper.updateById(exists);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Category exists = categoryMapper.selectById(id);
        if (exists == null) throw ResultCode.ADMIN_CATEGORY_NOT_FOUND.toException();
        // 关键校验: 分类下还有文章(回收站除外)则拒绝删除
        Long cnt = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getCategoryId, id)
                        .ne(Article::getStatus, 3)
        );
        if (cnt != null && cnt > 0) {
            throw ResultCode.ADMIN_CATEGORY_HAS_ARTICLES.toException();
        }
        categoryMapper.deleteById(id);
        log.info("[分类删除] id={}, name={}", id, exists.getName());
    }
}
