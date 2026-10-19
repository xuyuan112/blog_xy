package com.xuyuan.blog.web.controller.auth;

import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.common.util.CaptchaUtil;
import com.xuyuan.blog.web.dto.auth.AppChangePasswordParam;
import com.xuyuan.blog.web.dto.auth.AppLoginParam;
import com.xuyuan.blog.web.dto.auth.AppRefreshParam;
import com.xuyuan.blog.web.dto.auth.RegisterParam;
import com.xuyuan.blog.web.service.auth.AppAuthService;
import com.xuyuan.blog.web.vo.auth.AppLoginVO;
import com.xuyuan.blog.web.vo.auth.AppUserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "前台用户认证", description = "用户注册、登录、Token 管理和密码修改")
@RequestMapping({"/xuyuan/app/auth", "/xuyuan/auth"})
public class AppAuthController {

    private final AppAuthService appAuthService;
    private final CaptchaUtil captchaUtil;

    @GetMapping("/captcha")
    @Operation(summary = "获取图形验证码")
    public R<CaptchaUtil.CaptchaResult> captcha() {
        return R.ok(captchaUtil.generate());
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册成功后直接返回 AccessToken 和 RefreshToken")
    public R<AppLoginVO> register(@Valid @RequestBody RegisterParam param) {
        return execute(() -> appAuthService.register(param), 507);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名和密码登录，不使用图形验证码")
    public R<AppLoginVO> login(@Valid @RequestBody AppLoginParam param) {
        return execute(() -> appAuthService.login(param), 508);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token")
    public R<AppLoginVO> refresh(@Valid @RequestBody AppRefreshParam param) {
        return execute(() -> appAuthService.refresh(param), 601);
    }

    @GetMapping("/userinfo")
    @Operation(summary = "获取当前用户信息")
    public R<AppUserInfoVO> userInfo(@RequestHeader("Authorization") String authorization) {
        return execute(() -> appAuthService.userInfo(token(authorization)), 602);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public R<Void> logout(@RequestHeader("Authorization") String authorization) {
        try {
            appAuthService.logout(token(authorization));
            return R.ok();
        } catch (IllegalArgumentException exception) {
            return R.fail(602, exception.getMessage());
        }
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public R<Void> changePassword(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody AppChangePasswordParam param
    ) {
        try {
            appAuthService.changePassword(token(authorization), param);
            return R.ok();
        } catch (IllegalArgumentException exception) {
            return R.fail(510, exception.getMessage());
        }
    }

    private String token(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 头格式无效");
        }
        return authorization.substring(7).trim();
    }

    private <T> R<T> execute(Action<T> action, int errorCode) {
        try {
            return R.ok(action.run());
        } catch (IllegalArgumentException exception) {
            return R.fail(errorCode, exception.getMessage());
        }
    }

    @FunctionalInterface
    private interface Action<T> {
        T run();
    }
}
