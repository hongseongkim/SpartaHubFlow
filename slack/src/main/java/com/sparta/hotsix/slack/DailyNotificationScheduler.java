package com.sparta.hotsix.slack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DailyNotificationScheduler {

    @Autowired
    private WeatherAndDeliveryService weatherAndDeliveryService; // 날씨와 배송 정보를 가져오는 서비스

    @Autowired
    private SlackMessageService slackMessageService; // 슬랙 메시지 전송 서비스

    @Scheduled(cron = "0 0 6 * * *") // 매일 오전 6시에 실행
    public void sendDailySummary() {
        // 당일 날짜를 포맷팅
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 날씨 및 배송 정보 가져오기
        String weatherAndDeliveryInfo = weatherAndDeliveryService.getWeatherAndDeliverySummary(todayDate);

        // 슬랙으로 메시지 전송
        String message = String.format("오늘의 날씨 및 배송 요약:\n%s", weatherAndDeliveryInfo);
        slackMessageService.sendMessageToSlack("배송 담당자 이메일", message); // 수신자의 이메일은 필요에 따라 변경
    }
}