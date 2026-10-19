package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.model.entity.Moment;
import com.xuyuan.blog.web.dto.moment.MomentPageQuery;
import com.xuyuan.blog.web.vo.moment.MomentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MomentMapper extends BaseMapper<Moment> {

    @Update("UPDATE t_moment SET like_count = #{count} WHERE id = #{id} AND deleted = 0")
    int setLikeCount(@Param("id") Long id, @Param("count") long count);

    IPage<MomentVO> pagePublished(
            Page<MomentVO> page,
            @Param("query") MomentPageQuery query
    );
}
