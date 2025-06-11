package com.mtran.mvc.application.service.query.Impl;

import com.mtran.mvc.application.service.query.TokenQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenQueryServiceImpl implements TokenQueryService {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String getRefreshToken(String userEmail) {
        return redisTemplate.opsForValue().get("refresh_token:" + userEmail);
    }

    @Override
    public String getAccessToken(String userEmail) {
        return redisTemplate.opsForValue().get("access_token:" + userEmail);
    }
}
