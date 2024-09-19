package com.sparta.company.controller;

import com.sparta.company.domain.dto.AiChatDto;
import com.sparta.company.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@Tag(name = "AI", description = "AI 채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companys/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping
    @Operation(summary = "AI 채팅 생성", description = "AI 채팅을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "AI 채팅 생성 성공")
    @ResponseStatus(HttpStatus.CREATED)
    public AiChatDto.Response createChat(@RequestBody @Valid AiChatDto.Create aiChatDto,
                                         @RequestHeader("user-id") Long userId,
                                         @RequestHeader("user-role") String userRole) {
        return aiChatService.createChat(aiChatDto, userId, userRole);
    }

    @GetMapping
    @Operation(summary = "AI 채팅 페이징 조회", description = "AI 채팅을 페이징 조회합니다.")
    @ApiResponse(responseCode = "200", description = "AI 채팅 페이징 조회 성공")
    public Collection<AiChatDto.GetAllChatsResponse> getAllChats(
            @Schema(description = "페이지 번호(1부터 N까지)", defaultValue = "1")
            @RequestParam("page") int page,
            @Schema(description = "페이지에 출력할 개수를 입력합니다.", defaultValue = "10")
            @RequestParam("size") int size,
            @Schema(description = "정렬 기준을 입력합니다.")
            @RequestParam("sort") String sort,
            @RequestHeader("user-id") Long userId) {
        return aiChatService.getAllChats(page - 1, size, sort, userId);
    }

}
