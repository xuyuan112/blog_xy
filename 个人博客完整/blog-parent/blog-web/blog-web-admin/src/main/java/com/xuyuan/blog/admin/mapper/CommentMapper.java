package com.xuyuan.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.CommentQueryParam;
import com.xuyuan.blog.admin.vo.comment.AdminCommentVO;
import com.xuyuan.blog.model.entity.Comment;
import org.apache.ibatis.annotations.Param;

public interface CommentMapper extends BaseMapper<Comment> {
    IPage<AdminCommentVO> pageAdminList(Page<AdminCommentVO> page, @Param("param") CommentQueryParam param);
}
