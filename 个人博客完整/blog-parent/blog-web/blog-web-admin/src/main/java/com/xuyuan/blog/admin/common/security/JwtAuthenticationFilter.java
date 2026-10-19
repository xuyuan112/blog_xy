package com.xuyuan.blog.admin.common.security;

import com.xuyuan.blog.common.constant.RedisConstant;
import com.xuyuan.blog.common.context.CurrentUserContext;
import com.xuyuan.blog.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 *
 * <h3>设计要点 (参考 Experience 1182220 的鉴权失败分流 + 公开接口过滤器跳过)</h3>
 * <ol>
 *   <li><b>公开接口过滤器跳过</b>: 对 captcha/login/refresh 在 {@link #shouldNotFilter} 中直接返回 true,
 *       这样即使前端在登录流程中携带了"历史脏 token"(例如用户切换账号 / 本地缓存没清) , 也不会被本过滤器
 *       把 SecurityContext 清空, 造成与登录成功结果的时序竞态冲突。</li>
 *   <li><b>三种失败分支明确 WARN 日志</b>: (无 Authorization 头) / (黑名单命中) / (parseToken 返回 null 或 claims 无 userId),
 *       再结合 EntryPoint 里的 uri, 就能立刻定位是"Knife4j 没带 Header"还是"token 过期/错密钥"。</li>
 *   <li>黑名单集合 Key / 前缀 统一来自 {@link RedisConstant}。</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    /** 新的简化版 Header 名: access-token = 纯 JWT 字符串 (无需 Bearer 前缀) */
    private static final String ACCESS_TOKEN_HEADER = "access-token";
    /** 兼容老前端/脚本的 Header 名: Authorization = Bearer <jwt> 或 纯 <jwt> */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * 白名单: 本过滤器完全跳过解析, 交给 authorizeHttpRequests.permitAll() 即可。
     * 精确到 METHOD + PATH 避免被通配符误伤。
     */
    private static final RequestMatcher WHITE_LIST_MATCHER = new OrRequestMatcher(Arrays.asList(
            AntPathRequestMatcher.antMatcher(HttpMethod.GET,  "/xuyuan/auth/captcha"),
            AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/xuyuan/auth/login"),
            AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/xuyuan/auth/refresh"),
            // Knife4j / Swagger 静态资源路径 (Spring Security 已 permitAll, 但过滤器也跳过更省 CPU)
            AntPathRequestMatcher.antMatcher(HttpMethod.GET,  "/doc.html"),
            AntPathRequestMatcher.antMatcher(HttpMethod.GET,  "/v3/api-docs/**"),
            AntPathRequestMatcher.antMatcher(HttpMethod.GET,  "/swagger-resources/**"),
            AntPathRequestMatcher.antMatcher(HttpMethod.GET,  "/webjars/**"),
            AntPathRequestMatcher.antMatcher(HttpMethod.GET,  "/swagger-ui/**"),
            AntPathRequestMatcher.antMatcher(HttpMethod.GET,  "/swagger-ui.html"),
            AntPathRequestMatcher.antMatcher(HttpMethod.GET,  "/favicon.ico")
    ));

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 白名单请求直接跳过整个过滤器 (Experience 1182220 Failure-1 的修复策略)。
     * 相比只在 SecurityConfig 里 permitAll, 过滤器跳过能避免"携带脏 token 被过滤器清空上下文"的隐性 bug。
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return WHITE_LIST_MATCHER.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // ========== ⚠️ ThreadLocal 必须在 finally 清理: 防止 Tomcat 线程池复用导致 "身份串号" ==========
        try {
            doFilterInternalCore(request, response, filterChain);
        } finally {
            CurrentUserContext.clear();
        }
    }

    /**
     * 核心认证逻辑 (由 {@link #doFilterInternal} try/finally 包裹, 确保 ThreadLocal 清理)。
     */
    private void doFilterInternalCore(HttpServletRequest request,
                                      HttpServletResponse response,
                                      FilterChain filterChain) throws ServletException, IOException {
        // ========== 简化版: 优先读取 Header "access-token" = 纯 JWT 值 (用户要求的主方案) ==========
        String accessTokenRaw = request.getHeader(ACCESS_TOKEN_HEADER);
        String authorizationRaw = request.getHeader(AUTHORIZATION_HEADER);
        String token = extractToken(accessTokenRaw, authorizationRaw);

        if (!StringUtils.hasText(token)) {
            // [失败分支 1] 受保护接口缺失 access-token/Authorization → Knife4j 用户大概率没在调试卡片填 access-token
            log.warn("[JwtFilter/NO-TOKEN] 受保护接口缺失 access-token/Authorization Header, uri={}, method={}."
                            + " 若来自 Knife4j, 直接在接口「调试」卡片顶部的 access-token 输入框粘贴登录返回的 accessToken 纯 JWT 字符串即可。",
                    request.getRequestURI(), request.getMethod());
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // [失败分支 2] 已登出/互踢黑名单命中
            Boolean blacklisted = stringRedisTemplate.opsForSet()
                    .isMember(RedisConstant.ADMIN_LOGIN_TOKEN_BLACKLIST, token);
            if (Boolean.TRUE.equals(blacklisted)) {
                log.warn("[JwtFilter/BLACKLIST] Token 已被加入登出黑名单, uri={}, token前8位={}",
                        request.getRequestURI(), mask(token));
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            // ⭐ parseToken 只在此处执行一次 (整个请求全链路复用 ThreadLocal 里的 CurrentUserContext)
            Claims claims = jwtUtil.parseToken(token);
            if (claims == null) {
                // [失败分支 3] parseToken 返回 null: JWT 过期 / 签名错 / 密钥错 / issuer 不一致
                log.warn("[JwtFilter/INVALID] Token 解析失败(null claims), uri={}, token前8位={}. "
                                + "常见原因: accessToken已过期(30分钟)/前后端jwt.secret不一致",
                        request.getRequestURI(), mask(token));
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
            if (userId == null || !StringUtils.hasText(username)) {
                // [失败分支 4] claims 里缺核心字段
                log.warn("[JwtFilter/BAD-CLAIMS] Token 缺少 userId/sub 字段, uri={}, claims keys={}",
                        request.getRequestURI(), claims.keySet());
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            // ⭐ 拦截器一次性写入 ThreadLocal: 后续业务层/MyMetaObjectHandler/Controller 均可通过 CurrentUserContext.getCurrentUserId() 直取, 不再二次解析
            //    静态方法加 Current 前缀是刻意为之: 避免与 Lombok 实例 getter ctx.getUserId() 同名, 防止 Lombok 跳过生成 getter 造成无限递归 StackOverflowError
            CurrentUserContext userCtx = CurrentUserContext.of(userId, username);
            CurrentUserContext.set(userCtx);

            // [成功分支] 同时写入 Spring Security Context (注解鉴权需要, 但业务层优先读 ThreadLocal 避免反查 Authentication)
            if (log.isDebugEnabled()) {
                log.debug("[JwtFilter/OK] 认证成功, userId={}, username={}, uri={}",
                        userId, username, request.getRequestURI());
            }
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId.toString(),
                            token,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    );
            authentication.setDetails(userCtx);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
            // [失败分支 5] JWT 格式严重非法 (header.payload.signature 缺段 / 非 base64)
            log.warn("[JwtFilter/EXCEPTION] Token 解析抛异常, uri={}, token前8位={}, cause={}: {}",
                    request.getRequestURI(), mask(token),
                    ex.getClass().getSimpleName(), ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 解析纯 JWT 值。
     * <ol>
     *   <li>优先: Header access-token → 直接视为纯 JWT, 无需任何前缀;</li>
     *   <li>兜底: Header Authorization → 支持 "Bearer &lt;jwt&gt;" 或纯 "&lt;jwt&gt;" 两种格式。</li>
     * </ol>
     */
    private String extractToken(String accessTokenHeader, String authorizationHeader) {
        if (StringUtils.hasText(accessTokenHeader)) {
            String t = accessTokenHeader.trim();
            if (t.startsWith(BEARER_PREFIX)) {
                // 兼容用户误粘 Bearer 前缀的情况
                return t.substring(BEARER_PREFIX.length()).trim();
            }
            return t;
        }
        if (!StringUtils.hasText(authorizationHeader)) {
            return null;
        }
        String h = authorizationHeader.trim();
        if (h.startsWith(BEARER_PREFIX)) {
            return h.substring(BEARER_PREFIX.length()).trim();
        }
        return h;
    }

    /** 只打印 token 前 8 位用于对照, 避免完整 token 出现在日志泄露。 */
    private static String mask(String token) {
        if (token == null || token.length() <= 8) {
            return "***";
        }
        return token.substring(0, 8) + "***";
    }

    /** 重写用于 shouldNotFilter 的 toString 日志 (便于 DEBUG 时看白名单) */
    @SuppressWarnings("unused")
    private static String dumpWhiteList() {
        return Arrays.stream(new String[]{
                        "GET  /xuyuan/auth/captcha",
                        "POST /xuyuan/auth/login",
                        "POST /xuyuan/auth/refresh"
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
