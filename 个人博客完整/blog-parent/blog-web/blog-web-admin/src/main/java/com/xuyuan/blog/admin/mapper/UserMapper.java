package com.xuyuan.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口
 *
 * <p>除 MyBatis-Plus 自带的 BaseMapper CRUD 外, 额外提供 {@link #selectByUsername}
 * 用于根据用户名精确查询用户(会带逻辑删除过滤)。
 *
 * <p>注意: 这里不加 {@code @Mapper} 注解, 而是由启动类的
 * {@code @MapperScan({"com.xuyuan.blog.admin.mapper", "com.xuyuan.blog.web.mapper"})} 统一扫描,
 * 避免出现 "Bean already defined" 警告。
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户(登录时使用)
     *
     * @param username 登录用户名
     * @return 用户实体, 若不存在返回 null
     */
    User selectByUsername(@Param("username") String username);
}
