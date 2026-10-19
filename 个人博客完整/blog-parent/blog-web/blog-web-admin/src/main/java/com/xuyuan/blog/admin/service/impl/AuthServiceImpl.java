package com.xuyuan.blog.admin.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.xuyuan.blog.admin.dto.LoginParam;
import com.xuyuan.blog.admin.dto.RefreshParam;
import com.xuyuan.blog.admin.mapper.UserMapper;
import com.xuyuan.blog.admin.service.AuthService;
import com.xuyuan.blog.admin.vo.auth.CaptchaVO;
import com.xuyuan.blog.admin.vo.auth.LoginVO;
import com.xuyuan.blog.admin.vo.auth.UserInfoVO;
import com.xuyuan.blog.common.constant.RedisConstant;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.common.util.CaptchaUtil;
import com.xuyuan.blog.common.util.JwtUtil;
import com.xuyuan.blog.model.entity.User;
import com.xuyuan.blog.model.enums.UserStatus;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 后台认证服务实现
 *
 * <p>严格按 "admin-开发任务书.md → M2 Auth 模块" 约定的登录 5 步骤流程编写,
 * 每一个业务异常分支都抛出对应三位数 ResultCode。
 *
 * <p>Redis Key 全部收敛到 {@link RedisConstant}, 命名规范 blog:{模块}:{业务}:{其他}。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // ====================== 所有 Redis Key/TTL 来自 RedisConstant, 避免魔法值 ======================

    private static final String TOKEN_TYPE = "Bearer";

    private final JwtUtil jwtUtil;
    private final CaptchaUtil captchaUtil;
    private final UserMapper userMapper;
    /** 统一使用 StringRedisTemplate (UTF-8 字符串序列化, Spring Boot 默认就有),
     *  与 CaptchaUtil/JwtAuthenticationFilter 相同, 避免 Jackson JSON 序列化格式错位。 */
    private final StringRedisTemplate stringRedisTemplate;

    // ==============================
    // 1. 图形验证码
    // ==============================
    @Override
    public CaptchaVO getCaptcha() {
        CaptchaUtil.CaptchaResult result = captchaUtil.generate();
        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaKey(result.captchaKey());
        vo.setCaptchaImage(result.captchaImage());
        return vo;
    }

    // ==============================
    // 2. 登录 (M2 核心流程)
    // ==============================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginParam param) {
        // -------- 步骤 1: 验证码三步校验 (304 / 303 / 302) --------
        validateCaptcha(param.getCaptchaKey(), param.getCaptchaCode());

        // -------- 步骤 2: 账号校验 (用户不存在 + 用户禁用) --------
        User user = userMapper.selectByUsername(param.getUsername());
        if (user == null) {
            log.warn("[登录失败] 账号不存在: username={}", param.getUsername());
            throw ResultCode.ADMIN_ACCOUNT_ERROR.toException();
        }
        if (UserStatus.DISABLED.getCode().equals(user.getStatus())) {
            log.warn("[登录失败] 账号已禁用: userId={}", user.getId());
            throw ResultCode.ADMIN_ACCOUNT_DISABLED_ERROR.toException();
        }

        // -------- 步骤 3: 密码 MD5 比对 (任务书 M2 明确规定 MD5, 不走 BCrypt) --------
        String md5Password = SecureUtil.md5(param.getPassword());
        if (!md5Password.equalsIgnoreCase(user.getPassword())) {
            log.warn("[登录失败] 密码错误: userId={}", user.getId());
            throw ResultCode.ADMIN_ACCOUNT_ERROR.toException();
        }

        // -------- 步骤 4: 签发双 Token + 更新 last_login_at --------
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUsername());

        User update = new User();
        update.setId(user.getId());
        update.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(update);

        // -------- 步骤 5: 旧 refreshToken 加入黑名单 (同账号多端互踢) --------
        revokeOldUserRefreshTokens(user.getId());
        trackUserRefreshToken(user.getId(), refreshToken);

        return buildLoginVO(accessToken, refreshToken, user);
    }

    // ==============================
    // 3. 登出
    // ==============================
    @Override
    public void logout(String accessToken, String refreshToken) {
        if (StringUtils.hasText(accessToken)) {
            addToBlacklist(accessToken, jwtUtil.getAccessExpire());
        }
        if (StringUtils.hasText(refreshToken)) {
            addToBlacklist(refreshToken, jwtUtil.getRefreshExpire());
        }
    }

    // ==============================
    // 4. 刷新 Token
    // ==============================
    @Override
    public LoginVO refresh(RefreshParam param) {
        String refreshToken = param.getRefreshToken();
        if (!StringUtils.hasText(refreshToken)) {
            throw ResultCode.TOKEN_INVALID.toException();
        }

        // 4.1 refreshToken 是否已被拉黑 (登出/同账号互踢)
        Boolean blacklisted = stringRedisTemplate.opsForSet()
                .isMember(RedisConstant.ADMIN_LOGIN_TOKEN_BLACKLIST, refreshToken);
        if (Boolean.TRUE.equals(blacklisted)) {
            throw ResultCode.TOKEN_INVALID.toException("refreshToken 已失效, 请重新登录");
        }

        // 4.2 解析 refreshToken: parseToken 失败返回 null (视为非法/过期)
        Claims claims = jwtUtil.parseToken(refreshToken);
        if (claims == null) {
            throw ResultCode.TOKEN_EXPIRED.toException();
        }
        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();
        if (userId == null || !StringUtils.hasText(username)) {
            throw ResultCode.TOKEN_INVALID.toException();
        }

        // 4.3 用户是否还存在/未禁用
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw ResultCode.TOKEN_INVALID.toException("用户不存在, 请重新登录");
        }
        if (UserStatus.DISABLED.getCode().equals(user.getStatus())) {
            throw ResultCode.ADMIN_ACCOUNT_DISABLED_ERROR.toException();
        }

        // 4.4 签发新 token + 旧 refreshToken 加入黑名单 (一次性消耗)
        String newAccessToken = jwtUtil.createAccessToken(userId, username);
        String newRefreshToken = jwtUtil.createRefreshToken(userId, username);
        addToBlacklist(refreshToken, jwtUtil.getRefreshExpire());
        trackUserRefreshToken(userId, newRefreshToken);

        return buildLoginVO(newAccessToken, newRefreshToken, user);
    }

    // ==============================
    // 5. 当前登录用户信息
    // ==============================
    @Override
    public UserInfoVO getUserInfo(Long userId) {
        if (userId == null) {
            throw ResultCode.TOKEN_MISSING.toException();
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw ResultCode.TOKEN_INVALID.toException("用户不存在, 请重新登录");
        }
        if (UserStatus.DISABLED.getCode().equals(user.getStatus())) {
            throw ResultCode.ADMIN_ACCOUNT_DISABLED_ERROR.toException();
        }
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        vo.setUserId(user.getId());
        vo.setRoles(Collections.singletonList("ROLE_ADMIN"));
        return vo;
    }

    // ============================================================
    //                        私有辅助方法
    // ============================================================

    /**
     * 任务书 M2 规定的 3 步验证码校验: 每一步都独立抛对应三位数业务码。
     * 不使用 CaptchaUtil.verify() 的合并 boolean 返回, 是为了区分"空值 / 过期 / 错误"三种情况。
     */
    private void validateCaptcha(String captchaKey, String captchaCode) {
        // Step 1.1 captchaCode 空 → 304 未输入验证码
        if (!StringUtils.hasText(captchaCode)) {
            throw ResultCode.ADMIN_CAPTCHA_CODE_NOT_FOUND.toException();
        }
        // Step 1.2 captchaKey 对应的缓存取不到 → 303 验证码已过期 (或从未生成/已一次性消耗)
        String fullKey = RedisConstant.ADMIN_LOGIN_CAPTCHA_PREFIX + captchaKey;
        String cachedCode = stringRedisTemplate.opsForValue().get(fullKey);
        if (!StringUtils.hasText(cachedCode)) {
            throw ResultCode.ADMIN_CAPTCHA_CODE_EXPIRED.toException();
        }
        // Step 1.3 忽略大小写不一致 → 302 验证码错误
        if (!cachedCode.equalsIgnoreCase(captchaCode.trim())) {
            throw ResultCode.ADMIN_CAPTCHA_CODE_ERROR.toException();
        }
        // 校验通过, 立即删除验证码 (一次性消耗, 防暴力破解/重放攻击)
        stringRedisTemplate.delete(fullKey);
    }

    /**
     * 把 token 加入 Redis 黑名单集合, 并刷新集合整体 TTL。
     * TTL 近似使用配置值代替 token 真实剩余时间 (7 天兜底), 过期后自动淘汰避免无限膨胀。
     */
    private void addToBlacklist(String token, long ttlSeconds) {
        try {
            stringRedisTemplate.opsForSet().add(RedisConstant.ADMIN_LOGIN_TOKEN_BLACKLIST, token);
            // 集合整体过期时间 = max(当前过期时间, 新 token 的 ttl) — 这里简化成每次刷新为兜底值
            stringRedisTemplate.expire(RedisConstant.ADMIN_LOGIN_TOKEN_BLACKLIST,
                    Math.max(ttlSeconds, RedisConstant.ADMIN_LOGIN_TOKEN_BLACKLIST_TTL_SEC),
                    TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("[登出黑名单] 写入 Redis 失败, token={}, cause={}", token, e.getMessage());
        }
    }

    /**
     * 同账号所有历史 refreshToken 全部加入黑名单 (多端互踢)。
     */
    private void revokeOldUserRefreshTokens(Long userId) {
        String userKey = RedisConstant.ADMIN_LOGIN_USER_REFRESH_PREFIX + userId;
        try {
            var members = stringRedisTemplate.opsForSet().members(userKey);
            if (members != null && !members.isEmpty()) {
                stringRedisTemplate.opsForSet().add(RedisConstant.ADMIN_LOGIN_TOKEN_BLACKLIST,
                        members.toArray(new String[0]));
                stringRedisTemplate.delete(userKey);
            }
        } catch (Exception e) {
            log.warn("[互踢] 旧 refreshToken 清理失败 userId={}, cause={}", userId, e.getMessage());
        }
    }

    /** 把新 refreshToken 登记到该用户集合, TTL = refresh-token 自身有效期 */
    private void trackUserRefreshToken(Long userId, String refreshToken) {
        String userKey = RedisConstant.ADMIN_LOGIN_USER_REFRESH_PREFIX + userId;
        try {
            stringRedisTemplate.opsForSet().add(userKey, refreshToken);
            stringRedisTemplate.expire(userKey, jwtUtil.getRefreshExpire(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("[Token 登记] 写入失败 userId={}, cause={}", userId, e.getMessage());
        }
    }

    /** 统一构造 LoginVO (login 和 refresh 两处复用) */
    private LoginVO buildLoginVO(String accessToken, String refreshToken, User user) {
        LoginVO vo = new LoginVO();
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setExpiresIn(jwtUtil.getAccessExpire());
        vo.setTokenType(TOKEN_TYPE);

        UserInfoVO info = new UserInfoVO();
        BeanUtils.copyProperties(user, info);
        info.setUserId(user.getId());
        info.setRoles(Collections.singletonList("ROLE_ADMIN"));
        vo.setUserInfo(info);
        return vo;
    }
}
