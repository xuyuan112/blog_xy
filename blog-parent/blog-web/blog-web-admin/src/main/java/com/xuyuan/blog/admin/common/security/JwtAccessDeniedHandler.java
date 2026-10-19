package com.xuyuan.blog.admin.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 无权限访问 统一响应入口
 *
 * <p>当已登录但 authorities 不足以访问该资源时触发。
 * 返回三位数业务码 309(ADMIN_ACCESS_FORBIDDEN=无访问权限)。
 * 当前项目权限粒度较粗, 此处理器一般不会触发, 但作为安全链路的闭环保留。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("[AccessDenied] 无权限访问: uri={}, cause={}",
                request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        R<Void> body = R.fail(ResultCode.ADMIN_ACCESS_FORBIDDEN);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
