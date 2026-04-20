package com.boot.vuevbenadminboot.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * RefreshToken 服务
 */
@Service
public class RefreshTokenService {

    private static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";

    private final StringRedisTemplate redis;
    private final long refreshExpirationMs;

    public RefreshTokenService(
            StringRedisTemplate redis,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.redis = redis;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    // 生成 RefreshToken：返回新生成的 token
    public String issueToken(String username) {
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redis.opsForValue().set(key, username, Duration.ofMillis(refreshExpirationMs));
        return refreshToken;
    }

    // 验证 RefreshToken：返回用户名或 null
    public String validateAndGetUsername(String refreshToken) {
        return redis.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);
    }

    // 撤销 RefreshToken：删除 Redis 中的记录
    public void revokeToken(String refreshToken) {
        redis.delete(REFRESH_TOKEN_PREFIX + refreshToken);
    }
}

