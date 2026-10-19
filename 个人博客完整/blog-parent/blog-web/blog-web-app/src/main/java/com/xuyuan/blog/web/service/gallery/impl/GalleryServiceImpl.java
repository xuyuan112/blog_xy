package com.xuyuan.blog.web.service.gallery.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.model.entity.Gallery;
import com.xuyuan.blog.model.entity.Photo;
import com.xuyuan.blog.web.dto.gallery.GalleryPageQuery;
import com.xuyuan.blog.web.mapper.GalleryMapper;
import com.xuyuan.blog.web.mapper.PhotoMapper;
import com.xuyuan.blog.web.service.gallery.GalleryService;
import com.xuyuan.blog.web.vo.gallery.GalleryDetailVO;
import com.xuyuan.blog.web.vo.gallery.GalleryVO;
import com.xuyuan.blog.web.vo.gallery.PhotoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

    private final GalleryMapper galleryMapper;
    private final PhotoMapper photoMapper;

    @Override
    public PageResult<GalleryVO> page(GalleryPageQuery query) {
        normalize(query);
        IPage<GalleryVO> result = galleryMapper.pagePublished(
                new Page<>(query.getPage(), query.getPageSize()), query);
        return PageResult.of(result.getRecords(), result.getTotal(),
                result.getCurrent(), result.getSize());
    }

    @Override
    public GalleryDetailVO detail(Long id) {
        Gallery gallery = galleryMapper.selectById(id);
        if (gallery == null || Integer.valueOf(1).equals(gallery.getDeleted())) {
            return null;
        }
        GalleryDetailVO result = new GalleryDetailVO();
        BeanUtils.copyProperties(gallery, result);
        result.setPhotos(toPhotoVOs(photoMapper.listByGalleryId(id)));
        return result;
    }

    @Override
    public PhotoVO photo(Long id) {
        Photo photo = photoMapper.selectById(id);
        if (photo == null || Integer.valueOf(1).equals(photo.getDeleted())) {
            return null;
        }
        return toPhotoVO(photo);
    }

    private void normalize(GalleryPageQuery query) {
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(12);
        }
        query.setPageSize(Math.min(query.getPageSize(), 50));
    }

    private List<PhotoVO> toPhotoVOs(List<Photo> photos) {
        if (photos == null || photos.isEmpty()) {
            return Collections.emptyList();
        }
        return photos.stream().map(this::toPhotoVO).collect(Collectors.toList());
    }

    private PhotoVO toPhotoVO(Photo photo) {
        PhotoVO result = new PhotoVO();
        BeanUtils.copyProperties(photo, result);
        return result;
    }
}
