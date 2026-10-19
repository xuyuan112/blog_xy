package com.xuyuan.blog.web.service.auth.impl;

import cn.hutool.crypto.SecureUtil;
import com.xuyuan.blog.common.constant.RedisConstant;
import com.xuyuan.blog.common.util.JwtUtil;
import com.xuyuan.blog.model.entity.AppUser;
import com.xuyuan.blog.web.dto.auth.AppChangePasswordParam;
import com.xuyuan.blog.web.dto.auth.AppLoginParam;
import com.xuyuan.blog.web.dto.auth.AppRefreshParam;
import com.xuyuan.blog.web.dto.auth.RegisterParam;
import com.xuyuan.blog.web.mapper.AppUserMapper;
import com.xuyuan.blog.web.service.auth.AppAuthService;
import com.xuyuan.blog.web.vo.auth.AppLoginVO;
import com.xuyuan.blog.web.vo.auth.AppUserInfoVO;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AppAuthServiceImpl implements AppAuthService {

    private static final String TOKEN_BLACKLIST = RedisConstant.APP_LOGIN_TOKEN_BLACKLIST;

    private final AppUserMapper appUserMapper;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppLoginVO register(RegisterParam param) {
        String username = param.getUsername().trim();
        if (appUserMapper.selectByUsername(username) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(SecureUtil.md5(param.getPassword()));
        user.setNickname(param.getNickname().trim());
        user.setEmail(StringUtils.hasText(param.getEmail()) ? param.getEmail().trim() : null);
        user.setStatus(1);
        appUserMapper.insert(user);
        return issueTokens(user);
    }

    @Override
    public AppLoginVO login(AppLoginParam param) {
        AppUser user = appUserMapper.selectByUsername(param.getUsername().trim());
        if (user == null || !SecureUtil.md5(param.getPassword()).equalsIgnoreCase(user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw new IllegalArgumentException("该用户已被禁用");
        }
        return issueTokens(user);
    }

    @Override
    public AppLoginVO refresh(AppRefreshParam param) {
        if (Boolean.TRUE.equals(redisTemplate.opsForSet()
                .isMember(TOKEN_BLACKLIST, param.getRefreshToken()))) {
            throw new IllegalArgumentException("refreshToken 已失效，请重新登录");
        }
        Claims claims = jwtUtil.parseToken(param.getRefreshToken());
        if (claims == null) {
            throw new IllegalArgumentException("refreshToken 已过期或无效");
        }
        AppUser user = appUserMapper.selectById(claims.get("userId", Long.class));
        if (user == null || !Integer.valueOf(1).equals(user.getStatus())) {
            throw new IllegalArgumentException("用户不存在或已禁用");
        }
        blacklist(param.getRefreshToken(), jwtUtil.getRefreshExpire());
        return issueTokens(user);
    }

    @Override
    public AppUserInfoVO userInfo(String accessToken) {
        AppUser user = userByToken(accessToken);
        return toUserInfo(user);
    }

    @Override
    public void logout(String accessToken) {
        Claims claims = jwtUtil.parseToken(accessToken);
        if (claims != null) {
            long ttl = Math.max(1, (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000);
            blacklist(accessToken, ttl);
        }
    }

    private void blacklist(String token, long ttlSeconds) {
        redisTemplate.opsForSet().add(TOKEN_BLACKLIST, token);
        redisTemplate.expire(TOKEN_BLACKLIST, Math.max(1, ttlSeconds), TimeUnit.SECONDS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String accessToken, AppChangePasswordParam param) {
        AppUser user = userByToken(accessToken);
        if (!SecureUtil.md5(param.getOldPassword()).equalsIgnoreCase(user.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }
        user.setPassword(SecureUtil.md5(param.getNewPassword()));
        appUserMapper.updateById(user);
    }

    private AppLoginVO issueTokens(AppUser user) {
        return new AppLoginVO(
                jwtUtil.createAccessToken(user.getId(), user.getUsername()),
                jwtUtil.createRefreshToken(user.getId(), user.getUsername()),
                jwtUtil.getAccessExpire(),
                toUserInfo(user));
    }

    private AppUser userByToken(String token) {
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(TOKEN_BLACKLIST, token))) {
            throw new IllegalArgumentException("Token 已失效，请重新登录");
        }
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null || claims.get("userId", Long.class) == null) {
            throw new IllegalArgumentException("Token 无效或已过期");
        }
        AppUser user = appUserMapper.selectById(claims.get("userId", Long.class));
        if (user == null || !Integer.valueOf(1).equals(user.getStatus())) {
            throw new IllegalArgumentException("用户不存在或已禁用");
        }
        return user;
    }

    private AppUserInfoVO toUserInfo(AppUser user) {
        AppUserInfoVO result = new AppUserInfoVO();
        BeanUtils.copyProperties(user, result);
        result.setUserId(user.getId());
        return result;
    }
}
