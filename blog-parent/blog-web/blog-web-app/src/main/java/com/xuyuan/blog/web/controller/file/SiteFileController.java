package com.xuyuan.blog.web.controller.file;

import com.xuyuan.blog.common.config.MinioConfig;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;

/**
 * 前台文件代理
 *
 * <p>图片等静态资源存于 MinIO，后端在上传时把完整内网地址（如
 * {@code http://192.168.10.103:9000/blog/article/xxx.png}）写进了数据库。
 * 局域网/跨网段设备（手机等）访问不到该内网地址，导致图片加载失败。
 *
 * <p>前端把这类内网地址统一转成相对路径 {@code /xuyuan/file/{bucket}/{object}}，
 * 由本接口从 MinIO 读取并流式返回，浏览器只与后端同源交互，跨网段也能正常加载。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "minio", name = "enabled", havingValue = "true")
@Tag(name = "文件代理", description = "代理 MinIO 对象到浏览器（避免浏览器直连内网 MinIO）")
public class SiteFileController {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @GetMapping("/xuyuan/file/**")
    @Operation(summary = "代理 MinIO 文件", description = "path 格式: {bucket}/{object...}，如 blog/article/xxx.png")
    public void proxy(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        String prefix = "/xuyuan/file/";
        if (!uri.startsWith(prefix)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String path = uri.substring(prefix.length());
        if (path.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int idx = path.indexOf('/');
        if (idx <= 0 || idx == path.length() - 1) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String bucket = path.substring(0, idx);
        String object = path.substring(idx + 1);

        // 只允许代理当前配置的存储桶，避免通过该接口访问任意 bucket
        if (!minioConfig.getBucket().equals(bucket)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (GetObjectResponse stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(object).build())) {
            String contentType = stream.headers().get("Content-Type");
            if (contentType != null && !contentType.isEmpty()) {
                response.setContentType(contentType);
            }
            OutputStream out = response.getOutputStream();
            stream.transferTo(out);
            out.flush();
        } catch (Exception e) {
            log.debug("[File] MinIO 代理失败 bucket={} object={}: {}", bucket, object, e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
