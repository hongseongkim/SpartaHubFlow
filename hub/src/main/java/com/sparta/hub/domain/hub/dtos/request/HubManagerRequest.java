package com.sparta.hub.domain.hub.dtos.request;

import com.sparta.hub.domain.hub.dtos.HubDto;
import lombok.Getter;

@Getter
public class HubManagerRequest {

    private Long hubManagerId;
    private String hubManagerSlackId;

    public HubDto toDTO() {
        return HubDto.assignManager(hubManagerId, hubManagerSlackId);
    }
}
