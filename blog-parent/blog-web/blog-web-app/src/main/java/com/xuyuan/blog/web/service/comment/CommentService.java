package com.xuyuan.blog.web.service.comment;

import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.web.dto.CommentCreateParam;
import com.xuyuan.blog.web.dto.comment.CommentPageQuery;
import com.xuyuan.blog.web.vo.comment.CommentVO;

public interface CommentService {

    PageResult<CommentVO> page(CommentPageQuery query);

    Long create(CommentCreateParam param);
}
