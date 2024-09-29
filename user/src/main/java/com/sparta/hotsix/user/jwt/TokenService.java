package com.sparta.hotsix.user.jwt;

import com.sparta.hotsix.user.domain.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    public TokenService(RedisTemplate<String, String> redisTemplate, JwtUtil jwtUtil) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
    }

    public String getOrGenerateToken(User user) {
        String key = "token:" + user.getId();
        String token = redisTemplate.opsForValue().get(key);

        // 이미 존재하는 토큰이 있고, 유효하다면 기존 토큰 반환
        if (token != null && jwtUtil.validateToken(token)) {
            return token;
        }

        // 새로운 토큰 생성
        token = jwtUtil.generateToken(user);
        // 토큰을 Redis에 저장 (만료 시간 설정)
        redisTemplate.opsForValue().set(key, token, jwtUtil.getExpirationMs(), TimeUnit.MILLISECONDS);

        return token;
    }
}