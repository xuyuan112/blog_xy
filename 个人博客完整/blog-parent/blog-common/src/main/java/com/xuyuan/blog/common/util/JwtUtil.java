package com.xuyuan.blog.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 *
 * <p>基于 JJWT 0.11.x, 提供 Access/Refresh Token 的生成与解析。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtUtil {

    /** 签名密钥(至少 32 字节) */
    private String secret;

    /** AccessToken 有效期(秒) */
    private long accessExpire;

    /** RefreshToken 有效期(秒) */
    private long refreshExpire;

    /** 签发者 */
    private String issuer;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 AccessToken
     */
    public String createAccessToken(Long userId, String username) {
        return createToken(userId, username, accessExpire);
    }

    /**
     * 生成 RefreshToken
     */
    public String createRefreshToken(Long userId, String username) {
        return createToken(userId, username, refreshExpire);
    }

    private String createToken(Long userId, String username, long expireSeconds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireSeconds * 1000))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token, 校验失败返回 null
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
