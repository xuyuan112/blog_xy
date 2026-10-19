package com.xuyuan.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuyuan.blog.admin.dto.TagCreateParam;
import com.xuyuan.blog.admin.mapper.ArticleTagMapper;
import com.xuyuan.blog.admin.mapper.TagMapper;
import com.xuyuan.blog.admin.service.AdminTagService;
import com.xuyuan.blog.admin.vo.article.TagVO;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.model.entity.ArticleTag;
import com.xuyuan.blog.model.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台-标签业务实现
 *
 * <p>删除流程: 先 DELETE t_article_tag 中该标签的所有关联 (硬删, 无逻辑删除), 再 DELETE t_tag。
 * 因为任务书未要求"存在文章引用不可删标签", 允许标签被删后文章 tag 列表自然少这一项即可。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTagServiceImpl implements AdminTagService {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    @Override
    public List<TagVO> listAll() {
        List<Tag> list = tagMapper.selectList(new LambdaQueryWrapper<Tag>().orderByAsc(Tag::getId));
        if (list.isEmpty()) return Collections.emptyList();
        List<Long> tids = list.stream().map(Tag::getId).collect(Collectors.toList());
        List<ArticleTag> refs = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getTagId, tids)
        );
        Map<Long, Long> cntMap = refs.stream()
                .collect(Collectors.groupingBy(ArticleTag::getTagId, Collectors.counting()));
        return list.stream().map(t -> {
            TagVO vo = new TagVO();
            BeanUtils.copyProperties(t, vo);
            vo.setArticleCount(cntMap.getOrDefault(t.getId(), 0L));
            if (t.getCreateTime() != null) vo.setCreateTime(t.getCreateTime().format(DTF));
            if (t.getUpdateTime() != null) vo.setUpdateTime(t.getUpdateTime().format(DTF));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TagCreateParam param) {
        Tag t = new Tag();
        BeanUtils.copyProperties(param, t);
        tagMapper.insert(t);
        log.info("[标签新增] id={}, name={}", t.getId(), t.getName());
        return t.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, TagCreateParam param) {
        Tag exists = tagMapper.selectById(id);
        if (exists == null) throw ResultCode.ADMIN_TAG_NOT_FOUND.toException();
        exists.setName(param.getName());
        exists.setColor(param.getColor());
        tagMapper.updateById(exists);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Tag exists = tagMapper.selectById(id);
        if (exists == null) throw ResultCode.ADMIN_TAG_NOT_FOUND.toException();
        // 1. 先解除所有文章的关联 (硬删)
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, id));
        // 2. 再删标签本身 (逻辑删除, MyBatis-Plus deleted 自动处理)
        tagMapper.deleteById(id);
        log.info("[标签删除] id={}, name={}", id, exists.getName());
    }
}
