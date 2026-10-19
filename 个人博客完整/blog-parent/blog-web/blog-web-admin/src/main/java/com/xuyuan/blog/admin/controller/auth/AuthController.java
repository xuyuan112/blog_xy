package com.xuyuan.blog.admin.controller.auth;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.dto.LoginParam;
import com.xuyuan.blog.admin.dto.RefreshParam;
import com.xuyuan.blog.admin.service.AuthService;
import com.xuyuan.blog.admin.vo.auth.CaptchaVO;
import com.xuyuan.blog.admin.vo.auth.LoginVO;
import com.xuyuan.blog.admin.vo.auth.UserInfoVO;
import com.xuyuan.blog.common.context.CurrentUserContext;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理端 - 认证模块接口 (M2)
 *
 * <p>简化鉴权方式: <b>只要在调试卡片顶部的 Header 区填入 {@code access-token}</b>, 值就是登录接口返回的
 * {@code accessToken} 纯 JWT 字符串, 不需要写 "Bearer " 前缀, 也不需要点右上角 "Authorize" 按钮。
 *
 * <pre>
 * 5 个端点完全匹配任务书 & API_SPEC.md, 路径 /xuyuan/auth/**:
 *   captcha/login/refresh: 白名单 (空 SecurityRequirements, 不需要 access-token)
 *   logout/userinfo:       必须带 access-token (调试卡片顶部直接有输入框)
 * </pre>
 */
@Tag(name = "后台认证 M2", description = "图形验证码、登录、登出、刷新 Token、当前登录用户信息")
@RestController
@RequestMapping("/xuyuan/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String JWT_NAME = Knife4jConfiguration.SECURITY_SCHEME_NAME; // = "access-token"

    private final AuthService authService;

    // ==============================
    // 1. 图形验证码 (无需认证)
    // ==============================
    @Operation(summary = "获取图形验证码", description = "返回英数 4 位验证码 (captchaKey + Base64 图片), 5 分钟有效")
    @SecurityRequirements // 公开接口, 不显示小锁, 不需要填 access-token
    @GetMapping("/captcha")
    public R<CaptchaVO> getCaptcha() {
        return R.ok(authService.getCaptcha());
    }

    // ==============================
    // 2. 登录 (无需认证)
    // ==============================
    @Operation(summary = "管理员登录",
            description = "用户名 + 密码 + 验证码, 登录成功返回双 Token 与当前用户信息。返回的 accessToken 就是后续接口需要填入 Header access-token 的值。")
    @SecurityRequirements
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginParam param) {
        return R.ok(authService.login(param));
    }

    // ==============================
    // 3. 登出 (需要 access-token)
    // ==============================
    @Operation(summary = "管理员登出",
            description = "把当前 accessToken (必填, 直接从 Header 读取) 和可选 body.refreshToken 加入 Redis 黑名单。"
                    + "调试时直接在卡片顶部的「access-token」输入框粘贴登录返回的 accessToken 纯 JWT 即可, 无需 Bearer 前缀。")
    @SecurityRequirement(name = JWT_NAME)
    @PostMapping("/logout")
    public R<Void> logout(
            // ⭐ 让 Knife4j 在调试卡片顶部直接渲染 access-token 输入框 (APIKEY 全局安全方案 + 这里的 @Parameter 双重呈现)
            @Parameter(name = "access-token", description = "登录返回的 accessToken 纯 JWT 字符串 (无需 Bearer 前缀)",
                    required = true, schema = @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4ifQ..."))
            @RequestHeader(value = "access-token", required = false) String accessToken,

            // 兼容历史 Authorization: Bearer xxx 的请求 (JwtAuthenticationFilter 内部兜底)
            @RequestHeader(value = "Authorization", required = false) String authorization,

            @RequestBody(required = false) @Schema(description = "可选参数, 上送 refreshToken 则同步拉黑")
                RefreshParam refreshBody) {
        String token = resolveToken(accessToken, authorization);
        String refreshToken = (refreshBody != null) ? refreshBody.getRefreshToken() : null;
        authService.logout(token, refreshToken);
        return R.ok();
    }

    // ==============================
    // 4. 刷新 Token (无需认证)
    // ==============================
    @Operation(summary = "刷新 Token", description = "用 refreshToken 换取全新的双 Token, 原 refreshToken 自动失效")
    @SecurityRequirements
    @PostMapping("/refresh")
    public R<LoginVO> refresh(@Valid @RequestBody RefreshParam param) {
        return R.ok(authService.refresh(param));
    }

    // ==============================
    // 5. 当前登录用户信息 (需要 access-token)
    // ==============================
    @Operation(summary = "获取当前登录用户信息",
            description = "从 JWT 中解析 userId 查询用户详情。直接在调试卡片顶部的 access-token 输入框粘贴登录返回的 accessToken 纯 JWT 即可 (无需 Bearer 前缀)。")
    @SecurityRequirement(name = JWT_NAME)
    @GetMapping("/userinfo")
    public R<UserInfoVO> getUserInfo(
            // ⭐ 让 Knife4j 在调试卡片顶部直接渲染 access-token 输入框 (不再需要右上角 Authorize)
            @Parameter(name = "access-token", description = "登录返回的 accessToken 纯 JWT 字符串 (无需 Bearer 前缀)",
                    required = true, schema = @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4ifQ..."))
            @RequestHeader(value = "access-token", required = false) String accessToken,

            @RequestHeader(value = "Authorization", required = false) String authorization,

            Authentication authentication) {
        // 如果 Authentication 没建立 (比如 JWT 过滤器因为 token 非法跳过解析), 这里不会抛异常, 交给
        // Spring Security AuthorizationFilter 抛给 JwtAuthenticationEntryPoint 返回 305。
        Long userId = extractUserId(authentication);
        return R.ok(authService.getUserInfo(userId));
    }

    // ============================================================
    //                        私有辅助方法
    // ============================================================

    /**
     * 从 access-token / Authorization 两个 Header 中合并解析出纯 JWT。
     * 优先读取简化版 access-token (用户要求的方案)。
     */
    private String resolveToken(String accessToken, String authorization) {
        if (StringUtils.hasText(accessToken)) {
            String t = accessToken.trim();
            // 容错: 用户误写 Bearer 前缀也能接受
            if (t.startsWith(BEARER_PREFIX)) {
                return t.substring(BEARER_PREFIX.length()).trim();
            }
            return t;
        }
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        String h = authorization.trim();
        if (h.startsWith(BEARER_PREFIX)) {
            return h.substring(BEARER_PREFIX.length()).trim();
        }
        return h;
    }

    /**
     * 从 Spring Security Authentication 中取 userId:
     * 优先从 JwtAuthenticationFilter 写入的 CurrentUserContext.details 取, 失败时从 principal 字符串回退。
     */
    private Long extractUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        if (authentication.getDetails() instanceof CurrentUserContext ctx) {
            return ctx.getUserId();
        }
        if (authentication.getPrincipal() instanceof String uidStr && !uidStr.isBlank()) {
            return Long.parseLong(uidStr);
        }
        return null;
    }
}
