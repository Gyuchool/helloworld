package com.practice.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class UserResponse {

    private List<Result> results;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private String gender;
        private String email;
    }
}
