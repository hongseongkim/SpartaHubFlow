package com.sparta.hotsix.slack;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlackController {

    private final SlackService slackService;

    public SlackController(SlackService slackService) {
        this.slackService = slackService;
    }

    @PostMapping("/slack/send")
    public void sendSlackMessage(@RequestBody SlackMessageRequestDto requestDto) {
        slackService.sendMessage(requestDto);
    }
}