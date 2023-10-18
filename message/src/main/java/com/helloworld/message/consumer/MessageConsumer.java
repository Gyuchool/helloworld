package com.helloworld.message.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helloworld.message.MessageDto;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    @Value("${spring.rabbitmq.queue.name}")
    private String queueName;

    private final ObjectMapper objectMapper;

    @RabbitListener(
            queues = "${spring.rabbitmq.queue.name}",
            containerFactory = "listenerContainer"
    )
    public void receiveMessage(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        log.info("Received message:{}, Delivery tag:{}", new String(message.getBody()), tag);

        MessageDto dto;
        try {
            dto = objectMapper.readValue(message.getBody(), MessageDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (dto.getId() % 2 == 0) {
            log.info("Even number!! go to dead letter queue:" + dto);
            throw new AmqpRejectAndDontRequeueException("Even number!! go to dead letter queue:" + dto);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(
            queues = "giron.deadletter",
            containerFactory = "deadListenerContainer"
    )
    public void rePublish(Message failedMessage) {
        log.info("deadletter queue:{}", failedMessage);
    }
}
