package com.practice.feign;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class Controller {

    private final UserClient userClient;


    @GetMapping("/feign")
    public UserResponse feign() {
        return userClient.getUser("kor");
    }
}
