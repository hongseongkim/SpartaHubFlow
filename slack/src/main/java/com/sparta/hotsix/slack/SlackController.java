package com.sparta.hotsix.slack;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlackController {

    private final SlackService slackService;

    @Autowired
    public SlackController(SlackService slackService) {
        this.slackService = slackService;
    }

    @PostMapping("/sendMessage")
    public void sendMessage(@RequestParam String email, @RequestParam String message) {
        slackService.sendMessageToSlack(email, message);
    }
}
