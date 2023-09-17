package com.practice.feign;

import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.stereotype.Component;

@Component
public class Fallback implements UserClient{

    @Override
    public UserResponse getUser(String nation) {
        throw new NoFallbackAvailableException("Boom!!", new RuntimeException());
    }

}
