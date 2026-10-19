package com.xuyuan.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.ArticleCreateParam;
import com.xuyuan.blog.admin.dto.ArticleQueryParam;
import com.xuyuan.blog.admin.dto.ArticleStatusParam;
import com.xuyuan.blog.admin.dto.IdsParam;
import com.xuyuan.blog.admin.mapper.ArticleMapper;
import com.xuyuan.blog.admin.mapper.ArticleTagMapper;
import com.xuyuan.blog.admin.mapper.CategoryMapper;
import com.xuyuan.blog.admin.mapper.TagMapper;
import com.xuyuan.blog.admin.service.AdminArticleService;
import com.xuyuan.blog.admin.vo.article.AdminArticleVO;
import com.xuyuan.blog.admin.vo.article.ArticleDetailVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.model.entity.Article;
import com.xuyuan.blog.model.entity.ArticleTag;
import com.xuyuan.blog.model.entity.Category;
import com.xuyuan.blog.model.entity.Tag;
import com.xuyuan.blog.model.enums.ArticleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 后台管理端-文章业务实现
 *
 * <h3>任务书 M3 关键点落实</h3>
 * <ol>
 *   <li>文章-标签多对多走 {@code t_article_tag}: 新增/编辑时 「删除全部旧关联 → 批量写入新关联」</li>
 *   <li>状态机严格走 ArticleStatus: 0 草稿 / 1 已发布 / 2 私密 / 3 回收站, 批量删除=逻辑删除设 status=3</li>
 *   <li>草稿保存: id 存在则更新, 否则新建 (满足自动保存)</li>
 *   <li>分页排序: 置顶优先 → 发布时间倒序 (XML 里实现)</li>
 *   <li>发布状态变更时写入 published_at 字段 (避免空排序)</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminArticleServiceImpl implements AdminArticleService {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    // ======================================================================
    // 1. 分页列表 (3 段流程)
    // ======================================================================

    @Override
    public PageResult<AdminArticleVO> pageList(ArticleQueryParam param) {
        // 1-1: 分页查询主表 + category_name 左关联
        Page<AdminArticleVO> pageReq = new Page<>(param.getPage(), param.getPageSize());
        IPage<AdminArticleVO> pageRes = articleMapper.pageAdminList(pageReq, param);
        if (CollectionUtils.isEmpty(pageRes.getRecords())) {
            return PageResult.of(Collections.emptyList(), 0L, param.getPage(), param.getPageSize());
        }
        List<AdminArticleVO> records = pageRes.getRecords();
        List<Long> articleIds = records.stream().map(AdminArticleVO::getId).distinct().collect(Collectors.toList());

        // 1-2: 批量查文章关联的标签 (减少 N+1)
        Map<Long, List<Long>> tagIdsMap = loadTagIdsByArticleIds(articleIds);
        Map<Long, Tag> tagMap = loadTagMap(tagIdsMap);

        // 1-3: 聚合 VO (tags / tagIds / isTop boolean / isPrivate boolean)
        for (AdminArticleVO vo : records) {
            List<Long> tagIds = tagIdsMap.getOrDefault(vo.getId(), Collections.emptyList());
            vo.setTagIds(tagIds);
            vo.setTags(tagIds.stream()
                    .filter(tagMap::containsKey)
                    .map(tagId -> {
                        Tag t = tagMap.get(tagId);
                        AdminArticleVO.TagItemVO item = new AdminArticleVO.TagItemVO();
                        item.setId(t.getId());
                        item.setName(t.getName());
                        return item;
                    })
                    .collect(Collectors.toList()));
            // Integer(0/1) → Boolean: 数据库 is_top=1 时 isTop=true
            vo.setIsTop(Integer.valueOf(1).equals(vo.getIsTopRaw()));
            // status=2 → 私密文章
            vo.setIsPrivate(ArticleStatus.PRIVATE.getCode().equals(vo.getStatus()));
        }

        return PageResult.of(records, pageRes.getTotal(), pageRes.getCurrent(), pageRes.getSize());
    }

    /** 多文章批量查 tagIds, Map<articleId, tagIds> */
    private Map<Long, List<Long>> loadTagIdsByArticleIds(List<Long> articleIds) {
        if (CollectionUtils.isEmpty(articleIds)) return Collections.emptyMap();
        LambdaQueryWrapper<ArticleTag> w = new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getArticleId, articleIds);
        List<ArticleTag> rows = articleTagMapper.selectList(w);
        return rows.stream()
                .collect(Collectors.groupingBy(ArticleTag::getArticleId,
                        Collectors.mapping(ArticleTag::getTagId, Collectors.toList())));
    }

    /** 根据 tagId 集合加载 Tag 实体 Map<id, Tag> */
    private Map<Long, Tag> loadTagMap(Map<Long, List<Long>> tagIdsMap) {
        Set<Long> tagIds = tagIdsMap.values().stream()
                .flatMap(Collection::stream).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(tagIds)) return Collections.emptyMap();
        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
        return tags.stream().collect(Collectors.toMap(Tag::getId, Function.identity()));
    }

    // ======================================================================
    // 2. 详情 (编辑页 tagIds 回显)
    // ======================================================================

    @Override
    public ArticleDetailVO detail(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw ResultCode.ADMIN_ARTICLE_NOT_FOUND.toException();
        }
        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtils.copyProperties(article, vo);
        vo.setTagIds(articleTagMapper.listTagIdsByArticleId(id));
        vo.setIsTop(Integer.valueOf(1).equals(article.getIsTop()));
        if (article.getCreateTime() != null) vo.setCreateTime(article.getCreateTime().format(DTF));
        if (article.getUpdateTime() != null) vo.setUpdateTime(article.getUpdateTime().format(DTF));
        return vo;
    }

    // ======================================================================
    // 3. 新建
    // ======================================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ArticleCreateParam param) {
        return doSave(null, param);
    }

    // ======================================================================
    // 4. 编辑 (全量更新 + 标签关系重建)
    // ======================================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ArticleCreateParam param) {
        Article exists = articleMapper.selectById(id);
        if (exists == null) {
            throw ResultCode.ADMIN_ARTICLE_NOT_FOUND.toException();
        }
        doSave(id, param);
    }

    // ======================================================================
    // 5. 批量删除 (逻辑删除, status = RECYCLE(3))
    //       —— 用 MyBatis-Plus LambdaUpdateWrapper 单条 SQL 批量更新, 代替 N 次循环 updateById
    // ======================================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(IdsParam param) {
        if (CollectionUtils.isEmpty(param.getIds())) return;
        // 先用 QueryWrapper/selectBatchIds 过滤出真实存在(未被逻辑删)的文章 ID
        List<Article> exists = articleMapper.selectBatchIds(param.getIds());
        if (CollectionUtils.isEmpty(exists)) {
            throw ResultCode.ADMIN_ARTICLE_NOT_FOUND.toException();
        }
        List<Long> validIds = exists.stream()
                .map(Article::getId)
                .distinct()
                .collect(Collectors.toList());

        // 一条 SQL 批量更新: status=3 WHERE id IN (validIds)
        // MyBatis-Plus MetaObjectHandler 自动填充 updateTime / updateBy
        int affected = articleMapper.update(null,
                new LambdaUpdateWrapper<Article>()
                        .set(Article::getStatus, ArticleStatus.RECYCLE.getCode())
                        .in(Article::getId, validIds));
        log.info("[文章批量删除(逻辑回收站)] ids={}, affectedRows={}", validIds, affected);
    }

    // ======================================================================
    // 6. 状态变更 (含置顶字段, 发布状态切换时写 published_at)
    // ======================================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, ArticleStatusParam param) {
        Article exists = articleMapper.selectById(id);
        if (exists == null) {
            throw ResultCode.ADMIN_ARTICLE_NOT_FOUND.toException();
        }
        // 状态合法性: 不允许从「回收站(3)」直接回拉成别的状态, 只能在回收站列表手动二次操作恢复 (本期未实现, 直接拦)
        if (ArticleStatus.RECYCLE.getCode().equals(exists.getStatus())
                && !ArticleStatus.RECYCLE.getCode().equals(param.getStatus())) {
            throw ResultCode.ADMIN_ARTICLE_STATUS_ERROR.toException();
        }
        exists.setStatus(param.getStatus());
        if (param.getIsTop() != null) {
            exists.setIsTop(param.getIsTop() ? 1 : 0);
        }
        // 首次/重新发布时写入 published_at
        if (ArticleStatus.PUBLISHED.getCode().equals(param.getStatus()) && exists.getPublishedAt() == null) {
            exists.setPublishedAt(LocalDateTime.now());
        }
        articleMapper.updateById(exists);
    }

    // ======================================================================
    // 7. 保存草稿 (id 存在则更新, 否则新建)
    // ======================================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(Long id, ArticleCreateParam param) {
        // 草稿强制 status=DRAFT, 覆盖前端可能传的其它状态
        param.setStatus(ArticleStatus.DRAFT.getCode());
        return doSave(id, param);
    }

    // ======================================================================
    //                        私有: 统一保存 + 关联写入
    // ======================================================================

    private Long doSave(Long id, ArticleCreateParam param) {
        // 1) 若分类上送 → 必须存在 (MyBatis-Plus @TableLogic 自动过滤 deleted=1)
        if (param.getCategoryId() != null) {
            Category c = categoryMapper.selectById(param.getCategoryId());
            if (c == null) {
                throw ResultCode.ADMIN_CATEGORY_NOT_FOUND.toException();
            }
        }
        Article entity = (id == null) ? new Article() : articleMapper.selectById(id);
        if (entity == null) throw ResultCode.ADMIN_ARTICLE_NOT_FOUND.toException();

        BeanUtils.copyProperties(param, entity, "status");
        entity.setStatus(param.getStatus());
        if (param.getIsTop() != null) {
            entity.setIsTop(param.getIsTop() ? 1 : 0);
        } else if (entity.getIsTop() == null) {
            entity.setIsTop(0);
        }
        if (entity.getViewCount() == null) entity.setViewCount(0L);
        if (entity.getLikeCount() == null) entity.setLikeCount(0L);
        if (entity.getCommentCount() == null) entity.setCommentCount(0L);
        // 首次发布状态 → 写入 published_at; 非发布清空避免前台排序混乱
        if (ArticleStatus.PUBLISHED.getCode().equals(param.getStatus()) && entity.getPublishedAt() == null) {
            entity.setPublishedAt(LocalDateTime.now());
        } else if (!ArticleStatus.PUBLISHED.getCode().equals(param.getStatus())) {
            entity.setPublishedAt(null);
        }

        if (id == null) {
            articleMapper.insert(entity);
        } else {
            articleMapper.updateById(entity);
        }
        Long savedId = entity.getId();

        // 2) 标签关联关系重建
        //    - 删除旧关联
        articleTagMapper.deleteByArticleId(savedId);
        //    - 批量写新关联 (空 tagIds 跳过)
        List<Long> tagIds = param.getTagIds() == null ? Collections.emptyList() : param.getTagIds();
        if (!CollectionUtils.isEmpty(tagIds)) {
            // 过滤掉不存在的 tagId, 避免脏数据
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            Set<Long> existIds = tags.stream().map(Tag::getId).collect(Collectors.toSet());
            List<ArticleTag> binds = tagIds.stream()
                    .filter(existIds::contains)
                    .map(tid -> {
                        ArticleTag b = new ArticleTag();
                        b.setArticleId(savedId);
                        b.setTagId(tid);
                        return b;
                    })
                    .collect(Collectors.toList());
            for (ArticleTag b : binds) {
                articleTagMapper.insert(b);
            }
        }
        return savedId;
    }
}
