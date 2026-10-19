package com.xuyuan.blog.admin.mapper;

import com.xuyuan.blog.model.entity.*;
import org.apache.ibatis.annotations.Select;

public interface DashboardMapper {
    @Select("SELECT COUNT(*) FROM t_article WHERE deleted=0 AND status<>3") long articleCount();
    @Select("SELECT COUNT(*) FROM t_moment WHERE deleted=0") long momentCount();
    @Select("SELECT COUNT(*) FROM t_gallery WHERE deleted=0") long galleryCount();
    @Select("SELECT COUNT(*) FROM t_photo WHERE deleted=0") long photoCount();
    @Select("SELECT COUNT(*) FROM t_friend WHERE deleted=0 AND status=1") long friendCount();
    @Select("SELECT COUNT(*) FROM t_comment WHERE deleted=0 AND status=1") long commentCount();
    @Select("SELECT COALESCE(SUM(view_count),0) FROM t_article WHERE deleted=0") long totalViewCount();
    @Select("SELECT COUNT(*) FROM t_friend WHERE deleted=0 AND status=0") long pendingFriendCount();
    @Select("SELECT COUNT(*) FROM t_comment WHERE deleted=0 AND status=0") long pendingCommentCount();
    @Select("SELECT COUNT(*) FROM t_article_view_log WHERE create_time >= #{start} AND create_time < #{end}") long viewCount(java.time.LocalDateTime start, java.time.LocalDateTime end);
    @Select("SELECT COUNT(*) FROM t_comment WHERE deleted=0 AND create_time >= #{start} AND create_time < #{end}") long trendCommentCount(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
