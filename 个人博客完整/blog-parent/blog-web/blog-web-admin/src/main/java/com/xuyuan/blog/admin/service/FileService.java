package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.vo.file.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileUploadVO upload(MultipartFile file, String scene, boolean imageOnly);
}
