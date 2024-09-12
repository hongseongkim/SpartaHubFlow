package com.sparta.order.domain.dto;

import com.sparta.order.domain.entity.OrderAiChat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AiChatDto {

    @Data
    @Builder
    class Create {
        @NotNull(message = "유저 ID를 입력해주세요.")
        private final Long userId;

        @NotNull(message = "채팅 내용을 입력해주세요.")
        private final String content;
    }

    @Data
    @Builder
    class Response {
        private final UUID chatId;
        private final Long userId;
        private final boolean isUserChat;
        private final String content;
        private final LocalDateTime chatTime;

        public static Response of(OrderAiChat orderAiChat){
            return Response.builder()
                    .chatId(orderAiChat.getChatId())
                    .userId(orderAiChat.getUserId())
                    .isUserChat(orderAiChat.isUserChat())
                    .content(orderAiChat.getContent())
                    .chatTime(orderAiChat.getChatTime())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor(force = true)
    final class GetAllChatsResponse{
        private final UUID chatId;
        private final Long userId;
        private final boolean isUserChat;
        private final String content;
        private final LocalDateTime chatTime;

        public GetAllChatsResponse(OrderAiChat orderAiChat){
            this.chatId = orderAiChat.getChatId();
            this.userId = orderAiChat.getUserId();
            this.isUserChat = orderAiChat.isUserChat();
            this.content = orderAiChat.getContent();
            this.chatTime = orderAiChat.getChatTime();
        }
    }


}
