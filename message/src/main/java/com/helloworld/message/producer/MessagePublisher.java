package com.helloworld.message.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helloworld.message.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MessagePublisher {

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchange;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(MessageDto message) {
        try {
            rabbitTemplate.send(
                    exchange,
                    routingKey,
                    new Message(objectMapper.writeValueAsBytes(message)),
                    new CorrelationData(UUID.randomUUID().toString()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
