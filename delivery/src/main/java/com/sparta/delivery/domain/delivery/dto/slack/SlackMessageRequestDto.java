package com.sparta.delivery.domain.delivery.dto.slack;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlackMessageRequestDto {
    private String userEmail;
    private String message;
}