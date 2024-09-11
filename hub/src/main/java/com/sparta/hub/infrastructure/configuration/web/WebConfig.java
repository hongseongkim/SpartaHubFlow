package com.sparta.hub.infrastructure.configuration.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ObjectMapper responseObjectMapper;

    public WebConfig(@Qualifier("responseObjectMapper") ObjectMapper responseObjectMapper) {
        this.responseObjectMapper = responseObjectMapper;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 응답 시 사용할 ObjectMapper를 가진 HttpMessageConverter 추가
        MappingJackson2HttpMessageConverter customConverter = new MappingJackson2HttpMessageConverter(responseObjectMapper);
        converters.add(0, customConverter);  // 커스텀 컨버터를 최우선으로 추가
    }
}
