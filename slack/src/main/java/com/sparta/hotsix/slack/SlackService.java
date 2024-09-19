package com.sparta.hotsix.slack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class SlackService {

    private final SlackMessageRepository slackMessageRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final List<SlackMessage> delayedMessages = new ArrayList<>(); // 대기 메시지 목록

    @Value("${slack.token}")
    private String slackToken;

    public SlackService(SlackMessageRepository slackMessageRepository, ObjectMapper objectMapper) {
        this.slackMessageRepository = slackMessageRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    public void sendMessage(SlackMessageRequestDto requestDto) {
        // 메시지를 데이터베이스에 저장
        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setUserEmail(requestDto.getUserEmail());
        slackMessage.setMessage(requestDto.getMessage());
        slackMessageRepository.save(slackMessage);

        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.of(6, 0);
        LocalTime endTime = LocalTime.of(18, 0);

        // 06:00 - 18:00 사이이면 즉시 메시지 전송
        if (now.isAfter(startTime) && now.isBefore(endTime)) {
            sendDirectMessage(requestDto);
        } else {
            // 그 외 시간대에는 대기 목록에 메시지 추가
            log.info("Adding message to delayed list to be sent at 06:00 next day.");
            synchronized (delayedMessages) {
                delayedMessages.add(slackMessage);
            }
        }
    }

    private void sendDirectMessage(SlackMessageRequestDto requestDto) {
        log.info("Finding Slack user ID for email: {}", requestDto.getUserEmail());

        // 사용자 이메일로 Slack 사용자 ID 조회
        String userId = findSlackUserIdByEmail(requestDto.getUserEmail());

        if (userId == null) {
            log.error("Slack user not found for email: {}", requestDto.getUserEmail());
            return;
        }

        // Slack 사용자 ID로 메시지 전송
        log.info("Sending Slack message to user ID {}: {}", userId, requestDto.getMessage());
        sendSlackMessage(userId, requestDto.getMessage());
    }

    private String findSlackUserIdByEmail(String email) {
        try {
            String url = "https://slack.com/api/users.lookupByEmail?email=" + email;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(slackToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody().contains("\"ok\":true")) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.path("user").path("id").asText(null);
            } else {
                log.error("Failed to find Slack user by email: {}", response.getBody());
                return null;
            }
        } catch (Exception e) {
            log.error("Error while finding Slack user by email: {}", e.getMessage());
            return null;
        }
    }

    private void sendSlackMessage(String userId, String message) {
        try {
            String url = "https://slack.com/api/chat.postMessage";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(slackToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String payload = String.format("{\"channel\":\"%s\",\"text\":\"%s\"}", userId, message);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody().contains("\"ok\":true")) {
                log.info("Slack message sent successfully to user ID {}: {}", userId, message);
            } else {
                log.error("Failed to send Slack message: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Error while sending Slack message: {}", e.getMessage());
        }
    }

    // 매일 오전 06:00에 대기 메시지 전송
    @Scheduled(cron = "0 0 6 * * ?")
    public void sendDelayedMessages() {
        log.info("Sending delayed messages.");
        synchronized (delayedMessages) {
            for (SlackMessage slackMessage : delayedMessages) {
                SlackMessageRequestDto requestDto = new SlackMessageRequestDto();
                requestDto.setUserEmail(slackMessage.getUserEmail());
                requestDto.setMessage(slackMessage.getMessage());
                sendDirectMessage(requestDto);
            }
            delayedMessages.clear(); // 대기 메시지 목록 초기화
        }
    }

    public SlackMessage getSlackMessage(Long id) {
        return slackMessageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));

    }

    public List<SlackMessage> getAllSlackMessages() {
        
        return slackMessageRepository.findAll();
    }

}