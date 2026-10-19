package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {

    @Select("SELECT id,gallery_id AS galleryId,original,thumbnail,shoot_time AS shootTime,exif,sort FROM t_photo WHERE gallery_id = #{galleryId} AND deleted = 0 ORDER BY sort ASC, id ASC")
    List<Photo> listByGalleryId(@Param("galleryId") Long galleryId);
}
