package com.sparta.hotsix.product.domain.dto;

import com.sparta.hotsix.product.domain.entity.ProductAiChat;
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

        public static Response of(ProductAiChat productAiChat){
            return Response.builder()
                    .chatId(productAiChat.getChatId())
                    .userId(productAiChat.getUserId())
                    .isUserChat(productAiChat.isUserChat())
                    .content(productAiChat.getContent())
                    .chatTime(productAiChat.getChatTime())
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

        public GetAllChatsResponse(ProductAiChat productAiChat){
            this.chatId = productAiChat.getChatId();
            this.userId = productAiChat.getUserId();
            this.isUserChat = productAiChat.isUserChat();
            this.content = productAiChat.getContent();
            this.chatTime = productAiChat.getChatTime();
        }
    }


}
