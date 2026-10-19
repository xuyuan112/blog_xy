package com.xuyuan.blog.admin.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 未登录/认证失败 统一响应入口
 *
 * <p>当 JwtAuthenticationFilter 未解析出有效 token 却访问需要认证的接口时,
 * Spring Security 会回调此 EntryPoint。返回三位数业务码 305(ADMIN_LOGIN_AUTH=未登录)
 * 的 JSON, HTTP 状态行仍为 200(与全局规范一致)。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("[AuthEntryPoint] 未登录访问受保护接口: uri={}, cause={}",
                request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        R<Void> body = R.fail(ResultCode.ADMIN_LOGIN_AUTH);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
