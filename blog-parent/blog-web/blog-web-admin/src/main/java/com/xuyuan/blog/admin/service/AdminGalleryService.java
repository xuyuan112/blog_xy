package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.GalleryCreateParam;
import com.xuyuan.blog.admin.dto.GalleryQueryParam;
import com.xuyuan.blog.admin.vo.gallery.AdminGalleryVO;
import com.xuyuan.blog.admin.vo.gallery.AdminPhotoVO;
import com.xuyuan.blog.common.result.PageResult;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AdminGalleryService {
    PageResult<AdminGalleryVO> pageList(GalleryQueryParam param);
    Long create(GalleryCreateParam param);
    void update(Long id, GalleryCreateParam param);
    void delete(Long id);
    List<AdminPhotoVO> upload(Long id, List<MultipartFile> files);

    List<AdminPhotoVO> listPhotos(Long id);
    void deletePhoto(Long id);
}
