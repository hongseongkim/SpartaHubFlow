package com.sparta.hub.infrastructure.configuration;

import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 기본 캐시 설정 구성
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                // null 값 캐싱 방지
                .disableCachingNullValues()
                // 캐시 TTL(Time To Live) 설정
                .entryTtl(Duration.ofDays(1))
                // 캐시 키에 대한 접두사 설정
                .computePrefixWith(CacheKeyPrefix.simple())
                // 캐시 값 직렬화 설정
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                // 캐시 키 직렬화를 String으로 설정 (가독성 향상)
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()));

        // RedisCacheManager 생성 및 설정 적용
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .build();
    }
}
