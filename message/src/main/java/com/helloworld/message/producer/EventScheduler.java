package com.helloworld.message.producer;

import com.helloworld.message.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
class EventScheduler{
    private final MessagePublisher messagePublisher;

    @Scheduled(fixedRate = 10000L)
    public void startAll() {
        MessageDto messageDto = new MessageDto("Publish Message");
        messagePublisher.sendMessage(messageDto);
        log.info("start rabbitmq container: {}", messageDto);
    }
}
