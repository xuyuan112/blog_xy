package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    @Select("SELECT tag_id FROM t_article_tag WHERE article_id = #{articleId}")
    List<Long> listTagIds(@Param("articleId") Long articleId);
}
