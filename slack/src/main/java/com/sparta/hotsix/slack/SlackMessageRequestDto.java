package com.sparta.hotsix.slack;

import lombok.Data;

@Data
public class SlackMessageRequestDto {
    private String userEmail;
    private String message;
}