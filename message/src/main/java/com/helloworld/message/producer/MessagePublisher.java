package com.helloworld.message.producer;

import com.helloworld.message.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MessagePublisher {

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchange;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(MessageDto message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
