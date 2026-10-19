package com.xuyuan.blog.admin.service.impl;

import com.xuyuan.blog.admin.service.FileService;
import com.xuyuan.blog.admin.vo.file.FileUploadVO;
import com.xuyuan.blog.common.config.MinioConfig;
import com.xuyuan.blog.common.result.ResultCode;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private static final Set<String> SCENES = Set.of("article", "avatar", "moment", "gallery", "comment");
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public FileUploadVO upload(MultipartFile file, String scene, boolean imageOnly) {
        if (file == null || file.isEmpty()) throw ResultCode.ADMIN_FILE_UPLOAD_ERROR.toException();
        if (file.getSize() > MAX_FILE_SIZE) throw ResultCode.ADMIN_FILE_SIZE_ERROR.toException();
        if (scene == null || !SCENES.contains(scene)) throw ResultCode.ADMIN_FILE_TYPE_ERROR.toException();
        String extension = extension(file.getOriginalFilename());
        if (imageOnly && !IMAGE_EXTENSIONS.contains(extension)) throw ResultCode.ADMIN_FILE_TYPE_ERROR.toException();
        try {
            ensureBucket();
            String object = scene + "/" + UUID.randomUUID() + "." + extension;
            put(object, file.getInputStream(), file.getSize(), file.getContentType());
            String url = url(object);
            FileUploadVO vo = new FileUploadVO();
            vo.setUrl(url); vo.setFileName(object); vo.setSize(file.getSize());
            if (imageOnly) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Thumbnails.of(file.getInputStream()).size(400, 400).outputFormat(extension).toOutputStream(output);
                String thumbObject = scene + "/thumb/" + UUID.randomUUID() + "." + extension;
                byte[] bytes = output.toByteArray();
                put(thumbObject, new ByteArrayInputStream(bytes), bytes.length, file.getContentType());
                vo.setThumbnailUrl(url(thumbObject));
            }
            return vo;
        } catch (Exception ex) {
            throw ResultCode.ADMIN_FILE_UPLOAD_ERROR.toException();
        }
    }

    private void put(String object, InputStream input, long size, String contentType) throws Exception {
        try (InputStream in = input) {
            minioClient.putObject(PutObjectArgs.builder().bucket(minioConfig.getBucket()).object(object)
                    .stream(in, size, -1).contentType(contentType).build());
        }
    }

    private void ensureBucket() throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucket()).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucket()).build());
        }
    }

    private String url(String object) { return minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + object; }
    private String extension(String name) { int i = name == null ? -1 : name.lastIndexOf('.'); return i > -1 ? name.substring(i + 1).toLowerCase() : "bin"; }
}
