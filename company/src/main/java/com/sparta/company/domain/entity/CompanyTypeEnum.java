package com.sparta.company.domain.entity;

import lombok.ToString;

@ToString
public enum CompanyTypeEnum {
    PRODUCER("producer"),
    RECEIPT("receipt");

    private final String type;

    CompanyTypeEnum(String type) { this.type = type; }

    public String getType() { return this.type; }

}
