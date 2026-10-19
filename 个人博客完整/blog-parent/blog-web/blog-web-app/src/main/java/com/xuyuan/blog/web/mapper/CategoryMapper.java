package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.Category;
import com.xuyuan.blog.web.vo.misc.CategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    @Select("SELECT c.id,c.name,c.description,COUNT(a.id) AS articleCount FROM t_category c LEFT JOIN t_article a ON a.category_id=c.id AND a.deleted=0 AND a.status=1 WHERE c.deleted=0 GROUP BY c.id,c.name,c.description,c.sort ORDER BY c.sort ASC,c.id ASC")
    List<CategoryVO> listPublished();
}
