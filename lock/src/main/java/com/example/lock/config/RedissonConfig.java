package com.example.open_feature.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.redisson.api.RedissonClient;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        // Redis 설정
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")  // Redis 서버 주소 및 포트
                .setConnectionPoolSize(10)              // 연결 풀 크기
                .setConnectionMinimumIdleSize(5);       // 최소 연결 수

        // RedissonClient 빈 생성 및 반환
        return Redisson.create(config);
    }

}
