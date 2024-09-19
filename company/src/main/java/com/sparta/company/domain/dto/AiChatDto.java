package com.sparta.company.domain.dto;

import com.sparta.company.domain.entity.CompanyAiChat;
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

        public static Response of(CompanyAiChat companyAiChat){
            return Response.builder()
                    .chatId(companyAiChat.getChatId())
                    .userId(companyAiChat.getUserId())
                    .isUserChat(companyAiChat.isUserChat())
                    .content(companyAiChat.getContent())
                    .chatTime(companyAiChat.getChatTime())
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

        public GetAllChatsResponse(CompanyAiChat companyAiChat){
            this.chatId = companyAiChat.getChatId();
            this.userId = companyAiChat.getUserId();
            this.isUserChat = companyAiChat.isUserChat();
            this.content = companyAiChat.getContent();
            this.chatTime = companyAiChat.getChatTime();
        }
    }


}
