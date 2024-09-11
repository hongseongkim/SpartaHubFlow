package com.sparta.order.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "p_ai_chat")
@Builder
@Getter
public class OrderAiChat extends Timestamped {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "chat_id", updatable = false, nullable = false)
    private UUID chatId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_user_chat", nullable = false)
    private boolean isUserChat; // true 일 경우 유저의 채팅, false 일 경우 ai의 채팅

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "chat_time", nullable = false)
    private LocalDateTime chatTime;

    public OrderAiChat(Long userId, boolean isUserChat, String content, LocalDateTime chatTime) {
        this.userId = userId;
        this.isUserChat = isUserChat;
        this.content = content;
        this.chatTime = chatTime;
    }

}
