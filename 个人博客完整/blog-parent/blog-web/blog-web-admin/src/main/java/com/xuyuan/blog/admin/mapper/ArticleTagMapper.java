package com.xuyuan.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.ArticleTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章-标签 多对多关联表 Mapper (t_article_tag)
 *
 * <p>此表无逻辑删除,关联关系为硬删除。</p>
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    /** 查询某个文章已绑定的所有标签 ID (按标签表 sort 排序用不到,这里只拿 ID) */
    @Select("SELECT tag_id FROM t_article_tag WHERE article_id = #{articleId}")
    List<Long> listTagIdsByArticleId(@Param("articleId") Long articleId);

    /** 删除某个文章的全部标签关联 */
    @Delete("DELETE FROM t_article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") Long articleId);
}
