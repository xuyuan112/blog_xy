package com.xuyuan.blog.web.service.comment.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.model.entity.Article;
import com.xuyuan.blog.model.entity.Comment;
import com.xuyuan.blog.model.entity.Moment;
import com.xuyuan.blog.web.dto.CommentCreateParam;
import com.xuyuan.blog.web.dto.comment.CommentPageQuery;
import com.xuyuan.blog.web.mapper.ArticleMapper;
import com.xuyuan.blog.web.mapper.CommentMapper;
import com.xuyuan.blog.web.mapper.MomentMapper;
import com.xuyuan.blog.web.service.comment.CommentService;
import com.xuyuan.blog.web.vo.comment.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final MomentMapper momentMapper;

    @Override
    public PageResult<CommentVO> page(CommentPageQuery query) {
        validateTarget(query.getTargetType(), query.getTargetId());
        normalize(query);
        IPage<CommentVO> result = commentMapper.pageApproved(
                new Page<>(query.getPage(), query.getPageSize()), query);
        return PageResult.of(result.getRecords(), result.getTotal(),
                result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CommentCreateParam param) {
        validateTarget(param.getTargetType(), param.getTargetId());
        String content = trimRequired(param.getContent(), "评论内容不能为空");
        String nickname = trimRequired(param.getNickname(), "昵称不能为空");
        if (content.length() > 1000) {
            throw new IllegalArgumentException("评论内容不能超过 1000 个字符");
        }
        if (nickname.length() > 64) {
            throw new IllegalArgumentException("昵称不能超过 64 个字符");
        }

        Comment comment = new Comment();
        BeanUtils.copyProperties(param, comment);
        comment.setContent(content);
        comment.setNickname(nickname);
        comment.setEmail(trimToNull(param.getEmail()));
        comment.setWebsite(trimToNull(param.getWebsite()));
        // Comments are visible immediately; administrators can hide or delete
        // inappropriate content from the admin console afterwards.
        comment.setStatus(1);
        commentMapper.insert(comment);
        return comment.getId();
    }

    private void validateTarget(Integer targetType, Long targetId) {
        if (targetType == null || (targetType != 1 && targetType != 2)) {
            throw new IllegalArgumentException("目标类型必须为 1（文章）或 2（说说）");
        }
        if (targetId == null || targetId <= 0) {
            throw new IllegalArgumentException("目标 ID 无效");
        }
        boolean exists;
        if (targetType == 1) {
            Article article = articleMapper.selectById(targetId);
            exists = article != null && Integer.valueOf(1).equals(article.getStatus())
                    && !Integer.valueOf(1).equals(article.getDeleted());
        } else {
            Moment moment = momentMapper.selectById(targetId);
            exists = moment != null && Integer.valueOf(0).equals(moment.getVisible())
                    && !Integer.valueOf(1).equals(moment.getDeleted());
        }
        if (!exists) {
            throw new IllegalArgumentException("评论目标不存在或不可见");
        }
    }

    private void normalize(CommentPageQuery query) {
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        query.setPageSize(Math.min(query.getPageSize(), 50));
    }

    private String trimRequired(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
