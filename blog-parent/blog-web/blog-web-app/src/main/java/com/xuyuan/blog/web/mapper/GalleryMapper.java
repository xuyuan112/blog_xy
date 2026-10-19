package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.model.entity.Gallery;
import com.xuyuan.blog.web.dto.gallery.GalleryPageQuery;
import com.xuyuan.blog.web.vo.gallery.GalleryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GalleryMapper extends BaseMapper<Gallery> {

    IPage<GalleryVO> pagePublished(
            Page<GalleryVO> page,
            @Param("query") GalleryPageQuery query
    );
}
