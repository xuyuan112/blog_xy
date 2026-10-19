package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.model.entity.Comment;
import com.xuyuan.blog.web.dto.comment.CommentPageQuery;
import com.xuyuan.blog.web.vo.comment.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    IPage<CommentVO> pageApproved(
            Page<CommentVO> page,
            @Param("query") CommentPageQuery query
    );
}
