package com.xuyuan.blog.web.service.gallery;

import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.web.dto.gallery.GalleryPageQuery;
import com.xuyuan.blog.web.vo.gallery.GalleryDetailVO;
import com.xuyuan.blog.web.vo.gallery.GalleryVO;
import com.xuyuan.blog.web.vo.gallery.PhotoVO;

public interface GalleryService {

    PageResult<GalleryVO> page(GalleryPageQuery query);

    GalleryDetailVO detail(Long id);

    PhotoVO photo(Long id);
}
