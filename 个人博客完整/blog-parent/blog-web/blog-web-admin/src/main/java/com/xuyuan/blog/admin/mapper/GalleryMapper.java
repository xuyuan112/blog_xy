package com.xuyuan.blog.admin.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.GalleryQueryParam;
import com.xuyuan.blog.admin.vo.gallery.AdminGalleryVO;
import com.xuyuan.blog.model.entity.Gallery;
import org.apache.ibatis.annotations.Param;
public interface GalleryMapper extends BaseMapper<Gallery> { IPage<AdminGalleryVO> pageAdminList(Page<AdminGalleryVO> page,@Param("param") GalleryQueryParam param); }
