package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.Tag;
import com.xuyuan.blog.web.vo.misc.TagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    @Select("SELECT t.id,t.name,t.color,COUNT(a.id) AS articleCount FROM t_tag t LEFT JOIN t_article_tag at ON at.tag_id=t.id LEFT JOIN t_article a ON a.id=at.article_id AND a.deleted=0 AND a.status=1 WHERE t.deleted=0 GROUP BY t.id,t.name,t.color ORDER BY articleCount DESC,t.id ASC")
    List<TagVO> listPublished();
}
