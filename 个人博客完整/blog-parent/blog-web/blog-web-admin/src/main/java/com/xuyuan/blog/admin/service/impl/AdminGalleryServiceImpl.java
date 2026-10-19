package com.xuyuan.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.common.config.MinioConfig;
import com.xuyuan.blog.admin.dto.GalleryCreateParam;
import com.xuyuan.blog.admin.dto.GalleryQueryParam;
import com.xuyuan.blog.admin.mapper.GalleryMapper;
import com.xuyuan.blog.admin.mapper.PhotoMapper;
import com.xuyuan.blog.admin.service.AdminGalleryService;
import com.xuyuan.blog.admin.vo.gallery.AdminGalleryVO;
import com.xuyuan.blog.admin.vo.gallery.AdminPhotoVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.model.entity.Gallery;
import com.xuyuan.blog.model.entity.Photo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminGalleryServiceImpl implements AdminGalleryService {

    private final GalleryMapper galleryMapper;
    private final PhotoMapper photoMapper;
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public PageResult<AdminGalleryVO> pageList(GalleryQueryParam param) {
        long page = Math.max(1, param.getPage());
        long pageSize = param.getPageSize() > 0 && param.getPageSize() <= 100 ? param.getPageSize() : 10;
        IPage<AdminGalleryVO> result = galleryMapper.pageAdminList(new Page<>(page, pageSize), param);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(GalleryCreateParam param) {
        Gallery gallery = new Gallery();
        BeanUtils.copyProperties(param, gallery);
        galleryMapper.insert(gallery);
        return gallery.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, GalleryCreateParam param) {
        Gallery gallery = get(id);
        BeanUtils.copyProperties(param, gallery);
        galleryMapper.updateById(gallery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Gallery gallery = get(id);
        photoMapper.delete(new LambdaQueryWrapper<Photo>().eq(Photo::getGalleryId, gallery.getId()));
        galleryMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AdminPhotoVO> upload(Long id, List<MultipartFile> files) {
        get(id);
        List<AdminPhotoVO> result = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/")) {
                continue;
            }
            Photo photo = new Photo();
            photo.setGalleryId(id);
            String fileName = "gallery/" + id + "/" + java.util.UUID.randomUUID() + "-" + file.getOriginalFilename();
            try (java.io.InputStream input = file.getInputStream()) {
                ensureBucket();
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioConfig.getBucket()).object(fileName)
                        .stream(input, file.getSize(), -1)
                        .contentType(file.getContentType()).build());
            } catch (Exception ex) {
                throw ResultCode.ADMIN_FILE_UPLOAD_ERROR.toException();
            }
            String url = minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + fileName;
            photo.setOriginal(url);
            photo.setThumbnail(url);
            photo.setSort(0);
            photoMapper.insert(photo);

            AdminPhotoVO vo = new AdminPhotoVO();
            BeanUtils.copyProperties(photo, vo);
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<AdminPhotoVO> listPhotos(Long id) {
        get(id);
        return photoMapper.selectList(new LambdaQueryWrapper<Photo>()
                        .eq(Photo::getGalleryId, id)
                        .orderByAsc(Photo::getSort)
                        .orderByAsc(Photo::getId))
                .stream()
                .map(photo -> {
                    AdminPhotoVO vo = new AdminPhotoVO();
                    BeanUtils.copyProperties(photo, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePhoto(Long id) {
        if (photoMapper.selectById(id) == null) {
            throw ResultCode.ADMIN_GALLERY_NOT_FOUND.toException();
        }
        photoMapper.deleteById(id);
    }

    private Gallery get(Long id) {
        Gallery gallery = galleryMapper.selectById(id);
        if (gallery == null) {
            throw ResultCode.ADMIN_GALLERY_NOT_FOUND.toException();
        }
        return gallery;
    }

    private void ensureBucket() throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucket()).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucket()).build());
        }
    }
}
