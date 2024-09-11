package com.sparta.hub.presentation.dto.request;

import com.sparta.hub.application.dto.HubDto;
import lombok.Getter;

@Getter
public class HubRequest {
    private String name;
    private String address;

    public HubDto toDTO() {
        return HubDto.create(name, address);
    }
}


