package com.sparta.delivery.infrastructure.client;

import com.sparta.delivery.domain.delivery.dto.slack.SlackMessageRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "slack-service", url = "http://localhost:8091") // Slack 서비스의 URL과 포트 설정
public interface SlackFeignClient {

    //todo SlackMessageRequestDto 구현, user role이 HUB_DELIVERY_PERSON 를 에 등록된 user를 딜리버리 객체가 가지고있어야 할것같습니다
    @PostMapping("/slack/send") // Slack 서비스의 REST 컨트롤러 매핑
    void sendSlackMessage(@RequestBody SlackMessageRequestDto requestDto);
}