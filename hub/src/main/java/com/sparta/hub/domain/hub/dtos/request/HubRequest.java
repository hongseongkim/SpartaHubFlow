package com.sparta.hub.domain.hub.dtos.request;

import com.sparta.hub.domain.hub.dtos.HubDto;
import lombok.Getter;

@Getter
public class HubRequest {

    private String name;
    private String address;

    public HubDto toDTO() {
        return HubDto.createWithoutManager(name, address);
    }
}


