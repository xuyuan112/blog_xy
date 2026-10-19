package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.LoginParam;
import com.xuyuan.blog.admin.dto.RefreshParam;
import com.xuyuan.blog.admin.vo.auth.CaptchaVO;
import com.xuyuan.blog.admin.vo.auth.LoginVO;
import com.xuyuan.blog.admin.vo.auth.UserInfoVO;

/**
 * 后台认证服务
 *
 * <p>封装 M2 模块 5 个认证接口的业务逻辑: 验证码获取、登录、登出、刷新 Token、查询当前登录用户信息。
 * 所有异常路径使用 {@code ResultCode.xxx.toException()} 抛业务码, 由 GlobalExceptionHandler 统一封装为 R 响应。
 */
public interface AuthService {

    /**
     * 生成图形验证码 (算术题)
     *
     * @return 验证码 Key + Base64 图片, 服务端会以 captcha:{key} 为 key 写入 Redis, TTL 5 分钟
     */
    CaptchaVO getCaptcha();

    /**
     * 登录
     *
     * <p>严格按任务书 M2 约定顺序校验:
     * <ol>
     *   <li>三步校验验证码 → 304 / 303 / 302</li>
     *   <li>按用户名查询用户 → 用户不存在或禁用 → 307 / 308</li>
     *   <li>MD5 密码比对 → 不匹配 → 307</li>
     *   <li>签发 accessToken + refreshToken, 写入 last_login_at</li>
     *   <li>旧 refreshToken 加入黑名单 (同用户多端互踢)</li>
     * </ol>
     */
    LoginVO login(LoginParam param);

    /**
     * 登出
     *
     * @param accessToken  当前请求携带的 accessToken (从 Authorization Header 提取)
     * @param refreshToken 可选, 若前端同时上送 refreshToken 则也一并加入黑名单
     */
    void logout(String accessToken, String refreshToken);

    /**
     * 用 refreshToken 换全新的双 Token
     *
     * <p>若 refreshToken 已在黑名单中或已过期/非法 → TOKEN_INVALID(602) / TOKEN_EXPIRED(601)。
     */
    LoginVO refresh(RefreshParam param);

    /**
     * 查询当前登录用户信息
     *
     * @param userId JWT 中解析出来的 userId
     */
    UserInfoVO getUserInfo(Long userId);
}
