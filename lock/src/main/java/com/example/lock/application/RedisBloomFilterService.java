package com.example.lock;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RedisBloomFilterService {

    private final RBloomFilter<String> bloomFilter;

    public RedisBloomFilterService(RedissonClient redissonClient) {
        // Redis에서 Bloom Filter 생성 또는 가져오기
        this.bloomFilter = redissonClient.getBloomFilter("myBloomFilter");

        // Bloom Filter 초기화 (데이터 예상 개수와 오탐률 설정)
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(10_000L, 0.01);  // 10,000개의 데이터, 1% 오탐률
        }
    }

    // 중복 여부 확인 및 데이터 추가
    @Cacheable(value = "data", key = "#data")
    public BloomResponse isDuplicate(String data) {

        BloomResponse bloomResponse;
        if (bloomFilter.contains(data)) {
            bloomResponse = new BloomResponse(true, data);
        }
        else{
            bloomFilter.add(data);  // Bloom Filter에 데이터 추가
            bloomResponse = new BloomResponse(false, data);  // 새 데이터
        }
        System.out.println("cache에 데이터가 없음:" + bloomResponse.data + ":" + bloomResponse.isDuplicate);
        return bloomResponse;
    }


    public static class BloomResponse {
        private String message = "default";
        private boolean isDuplicate = false;
        private String data;

        public BloomResponse(boolean isDuplicate, String data) {
            this.isDuplicate = isDuplicate;
            this.data = data;
        }

        public BloomResponse() {
        }

        public String getMessage() {
            return message;
        }

        public boolean getIsDuplicate() {
            return isDuplicate;
        }

        public String getData() {
            return data;
        }
    }
}
