package com.boot.vuevbenadminboot.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 会话活跃服务
 * 职责：在 Redis 中记录用户最近活跃时间（touch），用于在线轨迹统计与后续策略扩展
 * 当前版本不基于空闲时间强制下线
 */
@Service
public class SessionActivityService {

    private static final String KEY_PREFIX = "session:activity:";

    private final StringRedisTemplate redis;
    public SessionActivityService(
            StringRedisTemplate redis,
            @Value("${app.session.idle-timeout-ms}") long idleTimeoutMs) {
        this.redis = redis;
        // 预留：当前仅做记录，不做拦截；该值可用于后续审计/策略切换
        if (idleTimeoutMs <= 0) {
            throw new IllegalArgumentException("app.session.idle-timeout-ms must be > 0");
        }
    }

    // 登录时记录活跃
    public void onLogin(String username) {
        touch(username);
    }

    // 滑动续期：更新最后活跃时间
    public void touch(String username) {
        String key = KEY_PREFIX + username;
        redis.opsForValue().set(key, String.valueOf(System.currentTimeMillis()), Duration.ofHours(8));
    }

    //兼容旧调用：当前仅记录活跃，不做空闲拦截，始终返回 true
    public boolean validateAndTouch(String username) {
        touch(username);
        return true;
    }

    public void invalidate(String username) {
        redis.delete(KEY_PREFIX + username);
    }
}
