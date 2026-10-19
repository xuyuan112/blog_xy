package com.xuyuan.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.CommentQueryParam;
import com.xuyuan.blog.admin.dto.CommentStatusParam;
import com.xuyuan.blog.admin.mapper.CommentMapper;
import com.xuyuan.blog.admin.service.AdminCommentService;
import com.xuyuan.blog.admin.vo.comment.AdminCommentVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.model.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {
    private final CommentMapper commentMapper;

    @Override
    public PageResult<AdminCommentVO> pageList(CommentQueryParam param) {
        if (param.getPage() < 1) param.setPage(1);
        if (param.getPageSize() < 1 || param.getPageSize() > 100) param.setPageSize(10);
        IPage<AdminCommentVO> result = commentMapper.pageAdminList(new Page<>(param.getPage(), param.getPageSize()), param);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, CommentStatusParam param) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw ResultCode.ADMIN_COMMENT_NOT_FOUND.toException();
        }
        if (param.getStatus() < 0 || param.getStatus() > 2) {
            throw ResultCode.PARAM_ERROR.toException();
        }
        comment.setStatus(param.getStatus());
        commentMapper.updateById(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (commentMapper.selectById(id) == null) {
            throw ResultCode.ADMIN_COMMENT_NOT_FOUND.toException();
        }
        commentMapper.deleteById(id);
    }
}
