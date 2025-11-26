package com.tsb.console.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConsoleControlTokenService {
    private static final String TOKEN_KEY = "console_control_token";
    private static final String TOKEN_TIMESTAMP_KEY = "console_control_token_ts";
    private static final long EXPIRE_MILLIS = 20_000; // 20 秒，可自行設定

    @Autowired
    private StringRedisTemplate redis;

    // 嘗試搶令牌，或刷新自己
    public boolean tryAcquireOrUpdateToken(String sessionId) {
        String holder = redis.opsForValue().get(TOKEN_KEY);
        String tsStr = redis.opsForValue().get(TOKEN_TIMESTAMP_KEY);
        long now = System.currentTimeMillis();
        long ts = tsStr != null ? Long.parseLong(tsStr) : 0;
        if (holder == null || ts + EXPIRE_MILLIS < now) {
            // 沒有持有人 或 令牌已過期
            redis.opsForValue().set(TOKEN_KEY, sessionId);
            redis.opsForValue().set(TOKEN_TIMESTAMP_KEY, String.valueOf(now));
            return true;
        }
        if (holder.equals(sessionId)) {
            // 是本人就刷新時間戳
            redis.opsForValue().set(TOKEN_TIMESTAMP_KEY, String.valueOf(now));
            return true;
        }
        return false;
    }

    public boolean isController(String sessionId) {
        String holder = redis.opsForValue().get(TOKEN_KEY);
        String tsStr = redis.opsForValue().get(TOKEN_TIMESTAMP_KEY);
        long now = System.currentTimeMillis();
        long ts = tsStr != null ? Long.parseLong(tsStr) : 0;
        // 有人持有且時間未過期且是本人
        return holder != null && holder.equals(sessionId) && ts + EXPIRE_MILLIS >= now;
    }

    public void releaseTokenIfMatch(String sessionId) {
        String holder = redis.opsForValue().get(TOKEN_KEY);
        if (holder != null && holder.equals(sessionId)) {
            redis.delete(TOKEN_KEY);
            redis.delete(TOKEN_TIMESTAMP_KEY);
        }
    }
}
