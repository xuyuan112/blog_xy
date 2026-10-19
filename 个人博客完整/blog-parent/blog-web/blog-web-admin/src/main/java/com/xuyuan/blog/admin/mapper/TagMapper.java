package com.xuyuan.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/** 文章标签 Mapper */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
