package com.sparta.delivery.infrastructure.client;

import com.sparta.delivery.domain.delivery.dto.slack.SlackMessageRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "slack-service", configuration = FeignConfig.class)
public interface SlackFeignClient {
    @PostMapping("/api/v1/slack")
    void sendSlackMessage(@RequestBody SlackMessageRequestDto requestDto);
}
