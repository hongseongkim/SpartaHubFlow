package com.sparta.hotsix.slack;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/slack")
public class SlackController {

    private final SlackService slackService;

    public SlackController(SlackService slackService) {
        this.slackService = slackService;
    }

    @PostMapping
    public void sendSlackMessage(@RequestBody SlackMessageRequestDto requestDto) {
        slackService.sendMessage(requestDto);
    }

    @GetMapping("{id}")
    public SlackMessage getSlackMessage(@PathVariable Long id) {
       return slackService.getSlackMessage(id);
    }


    @GetMapping("/messages")
    public List<SlackMessage> getAllSlackMessages() {
        return slackService.getAllSlackMessages();
    }



}