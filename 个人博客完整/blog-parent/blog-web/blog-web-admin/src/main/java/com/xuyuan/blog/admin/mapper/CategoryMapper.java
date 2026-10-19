package com.xuyuan.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/** 文章分类 Mapper */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
