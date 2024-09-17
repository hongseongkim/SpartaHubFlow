package com.sparta.hotsix.slack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

@Service
public class SlackService {

    @Value("${slack.token}")
    private String slackToken;

    private final WebClient webClient;

    public SlackService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://slack.com/api").build();
    }

    @KafkaListener(topics = "user-topic", groupId = "slack-service-group")
    public void consume(String message, ServerWebExchange exchange) {
        String userEmail = exchange.getRequest().getHeaders().getFirst("User-Email");

        if (userEmail != null) {
            sendMessageToSlack(userEmail, message);
        } else {
            System.err.println("User-Email header is missing");
        }
    }

    public void sendMessageToSlack(String userEmail, String message) {
        String payload = String.format("{\"channel\": \"#general\", \"text\": \"Message for %s: %s\"}", userEmail, message);

        webClient.post()
                .uri("/chat.postMessage")
                .header("Authorization", "Bearer " + slackToken)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.err.println("Error sending message to Slack: " + error.getMessage()))
                .subscribe(response -> System.out.println("Message sent to Slack: " + response));
    }
}
