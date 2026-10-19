package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.CommentQueryParam;
import com.xuyuan.blog.admin.dto.CommentStatusParam;
import com.xuyuan.blog.admin.vo.comment.AdminCommentVO;
import com.xuyuan.blog.common.result.PageResult;

public interface AdminCommentService {
    PageResult<AdminCommentVO> pageList(CommentQueryParam param);
    void changeStatus(Long id, CommentStatusParam param);
    void delete(Long id);
}
