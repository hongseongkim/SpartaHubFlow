package com.sparta.route.infrastructure.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Decoder feignDecoder() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Java 8 날짜/시간 타입 지원을 위한 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules(); // 추가 모듈을 자동으로 등록
        return new JacksonDecoder(objectMapper);
    }
}
