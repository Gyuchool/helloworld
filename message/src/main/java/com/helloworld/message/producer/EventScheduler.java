package com.helloworld.message.producer;

import com.helloworld.message.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
class EventScheduler{
    private final MessagePublisher messagePublisher;
    private static int ID = 0;

    @Scheduled(fixedRate = 10000L)
    public void startAll() {
        var value = ID += 1;
        MessageDto messageDto = new MessageDto(value+"번 메시지");
        messagePublisher.sendMessage(messageDto);
    }
}
