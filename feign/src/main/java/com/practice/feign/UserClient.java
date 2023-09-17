package com.practice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userClient", url = "https://randomuser.me")
public interface UserClient {

    @GetMapping("/api")
    UserResponse getUser(@RequestParam("nat") String nation);
}
