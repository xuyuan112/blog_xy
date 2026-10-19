package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.AppUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {

    @Select("SELECT * FROM t_app_user WHERE username = #{username} AND deleted = 0 LIMIT 1")
    AppUser selectByUsername(String username);
}
