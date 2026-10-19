package com.xuyuan.blog.admin.common.config;

import com.xuyuan.blog.admin.common.security.JwtAccessDeniedHandler;
import com.xuyuan.blog.admin.common.security.JwtAuthenticationEntryPoint;
import com.xuyuan.blog.admin.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置(后台管理端)
 *
 * <h3>设计要点</h3>
 * <ul>
 *   <li>前后端分离 + JWT: {@code csrf}、{@code formLogin}、{@code httpBasic} 全部禁用,
 *       SessionPolicy 设为 STATELESS(HTTP 无状态)。</li>
 *   <li>白名单(无需认证): 认证 3 接口 + Knife4j/Swagger 静态资源 + favicon。
 *       其余接口(含 /xuyuan/admin/**、/xuyuan/auth/logout、/xuyuan/auth/userinfo) 均需 Bearer token。</li>
 *   <li>JWT 过滤器放置在 {@code UsernamePasswordAuthenticationFilter} 之前,
 *       这样整个 Security 过滤器链可以在第一个环节拿到认证信息。</li>
 *   <li>认证失败/权限不足通过 {@link JwtAuthenticationEntryPoint} 和 {@link JwtAccessDeniedHandler}
 *       返回三位数业务码 JSON (HTTP 响应行保持 200, 与前端 axios 拦截器一致)。</li>
 *   <li>PasswordEncoder 用 BCrypt 作为 Spring Security 的默认注入实现;
 *       登录密码比对走纯 MD5(任务书约定), 因此不通过 Spring Security 的 DaoAuthenticationProvider,
 *       而是在 {@code AuthServiceImpl} 中手动比对。</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * 放行白名单: 认证公共接口 + Knife4j 文档 + 静态资源
     */
    private static final String[] WHITE_LIST = {
            // ===== 认证公共接口 =====
            "/xuyuan/auth/captcha",
            "/xuyuan/auth/login",
            "/xuyuan/auth/refresh",
            // ===== Knife4j 增强文档 =====
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/favicon.ico"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ---- 基础防护: 关闭默认表单登录 ----
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                // ---- 无状态 Session(JWT 替代) ----
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ---- 权限策略 ----
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated()
                )
                // ---- 认证异常/权限不足 统一 JSON ----
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                // ---- JWT 过滤器放在 UsernamePasswordAuthenticationFilter 前 ----
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // ---- CORS: 与 WebMvcConfigurer CORS 映射保持同源 ----
                .cors(cors -> {});

        return http.build();
    }

    /**
     * 用于 Spring Security 的密码编码器 (Bean 注入不可省略)。
     * 当前项目登录密码比对使用 MD5, 此 Bean 主要用于未来接入 BCrypt 时平滑迁移。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
