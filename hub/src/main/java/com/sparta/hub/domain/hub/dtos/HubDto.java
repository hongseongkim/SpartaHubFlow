package com.sparta.hub.domain.hub.dtos;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class HubDto {

    private String name;
    private String address;
    private Long hubManagerId;
    private String hubManagerSlackId;

    // Interface 계층과 Application 계층간 Object 분리 방법
    public static HubDto createWithoutManager(String name, String address) {
        return HubDto.builder()
                .name(name)
                .address(address)
                .build();
    }

    public static HubDto createWithManager(String name, String address, Long hubManagerId, String hubManagerSlackId) {
        return HubDto.builder()
                .name(name)
                .address(address)
                .hubManagerId(hubManagerId)
                .hubManagerSlackId(hubManagerSlackId)
                .build();
    }

    public static HubDto assignManager(Long hubManagerId, String hubManagerSlackId) {
        return HubDto.builder()
                .hubManagerId(hubManagerId)
                .hubManagerSlackId(hubManagerSlackId)
                .build();
    }
}
