package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.model.entity.Article;
import com.xuyuan.blog.web.dto.article.ArticlePageQuery;
import com.xuyuan.blog.web.vo.article.ArticleListItemVO;
import com.xuyuan.blog.web.vo.article.ArticleNavVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Update("UPDATE t_article SET like_count = #{count} WHERE id = #{id} AND deleted = 0")
    int setLikeCount(@Param("id") Long id, @Param("count") long count);
    IPage<ArticleListItemVO> pagePublished(Page<ArticleListItemVO> page, @Param("q") ArticlePageQuery query);
    @Select("SELECT id,title,published_at AS publishedAt FROM t_article WHERE deleted=0 AND status=1 AND type=#{type} AND published_at < #{publishedAt} ORDER BY published_at DESC,id DESC LIMIT 1")
    ArticleNavVO previous(@Param("type") Integer type, @Param("publishedAt") java.time.LocalDateTime publishedAt);
    @Select("SELECT id,title,published_at AS publishedAt FROM t_article WHERE deleted=0 AND status=1 AND type=#{type} AND published_at > #{publishedAt} ORDER BY published_at ASC,id ASC LIMIT 1")
    ArticleNavVO next(@Param("type") Integer type, @Param("publishedAt") java.time.LocalDateTime publishedAt);
    @Select("SELECT id,title,view_count AS viewCount FROM t_article WHERE deleted=0 AND status=1 ORDER BY view_count DESC,id DESC LIMIT #{limit}")
    List<ArticleListItemVO> hot(@Param("limit") Integer limit);
    @Select("SELECT id,title,published_at AS publishedAt FROM t_article WHERE deleted=0 AND status=1 ORDER BY published_at DESC,id DESC")
    List<ArticleNavVO> timeline();
}
