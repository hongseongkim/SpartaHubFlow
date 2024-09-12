package com.sparta.hotsix.product.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

public interface UserDto {

    @Data
    @NoArgsConstructor(force = true)
    class Response {

        private final Long userId;
        private final String userName;
        private final String email;
        private final String slackId;
        private final Enum<?> role;

    }
}
