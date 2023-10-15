package com.practice.springbatch.domain;

import lombok.Getter;

@Getter
public enum GradeStatus {
    BASIC("기본 등급"),
    PREMIUM("프리미엄 등급"),
    VIP("VIP 등급"),
    ;

    private final String value;

    GradeStatus(String value) {
        this.value = value;
    }
}
