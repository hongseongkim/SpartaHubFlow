package com.sparta.hub.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class HubDto {
    private String name;
    private String address;

    // Interface 계층과 Application 계층간 Object 분리 방법
    public static HubDto create(String name, String address) {
        return HubDto.builder()
                .name(name)
                .address(address)
                .build();
    }
}
