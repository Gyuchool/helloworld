package com.example.lock;

import com.example.open_feature.application.RedisBloomFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bloomfilter")
public class BloomFilterController {

    private final RedisBloomFilterService bloomFilterService;

    @Autowired
    public BloomFilterController(RedisBloomFilterService bloomFilterService) {
        this.bloomFilterService = bloomFilterService;
    }

    @PostMapping("/check")
    public ResponseEntity<String> checkDuplicate(@RequestParam String data) {
        var bloomResponse = bloomFilterService.isDuplicate(data);

        if (bloomResponse.getIsDuplicate()) {
            return ResponseEntity.ok("Duplicate data: " + data);
        } else {
            return ResponseEntity.ok("New data added: " + data);
        }
    }
}
