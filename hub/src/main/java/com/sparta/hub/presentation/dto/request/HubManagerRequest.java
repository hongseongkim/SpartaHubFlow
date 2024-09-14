package com.sparta.hub.presentation.dto.request;

import com.sparta.hub.application.dto.HubDto;
import lombok.Getter;

@Getter
public class HubManagerRequest {

    private Long hubManagerId;
    private String hubManagerSlackId;

    public HubDto toDTO() {
        return HubDto.assignManager(hubManagerId, hubManagerSlackId);
    }
}
