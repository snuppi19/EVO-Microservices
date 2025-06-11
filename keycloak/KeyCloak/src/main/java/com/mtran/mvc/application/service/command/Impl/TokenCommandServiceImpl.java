package com.mtran.mvc.application.service.command.Impl;

import com.mtran.mvc.application.service.command.TokenCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCommandServiceImpl implements TokenCommandService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 3600_000; // 1 giờ
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 3600_000; // 7 ngày


    @Override
    public void saveRefreshToken(String userEmail, String refreshToken) {
        if (refreshToken == null) {
            redisTemplate.delete("refresh_token:" + userEmail);
        } else {
            redisTemplate.opsForValue().set("refresh_token:" + userEmail, refreshToken,
                    REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
        }

    }

    @Override
    public void saveAccessToken(String userEmail, String accessToken) {
        if (accessToken == null) {
            redisTemplate.delete("access_token:" + userEmail);
        } else {
            redisTemplate.opsForValue().set("access_token:" + userEmail, accessToken,
                    ACCESS_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
        }
    }
}
