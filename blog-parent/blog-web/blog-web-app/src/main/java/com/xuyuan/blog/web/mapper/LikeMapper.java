package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeMapper extends BaseMapper<Like> {

    @Select("SELECT COUNT(1) FROM t_like WHERE target_type = #{targetType} AND target_id = #{targetId} AND ip = #{ip}")
    int countByClient(@Param("targetType") Integer targetType,
                      @Param("targetId") Long targetId,
                      @Param("ip") String ip);

    @Delete("DELETE FROM t_like WHERE target_type = #{targetType} AND target_id = #{targetId} AND ip = #{ip}")
    int deleteByClient(@Param("targetType") Integer targetType,
                       @Param("targetId") Long targetId,
                       @Param("ip") String ip);

    @Select("SELECT COUNT(1) FROM t_like WHERE target_type = #{targetType} AND target_id = #{targetId}")
    long countByTarget(@Param("targetType") Integer targetType,
                       @Param("targetId") Long targetId);
}
