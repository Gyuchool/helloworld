package com.helloworld.message.producer;

import com.helloworld.message.MessageDto;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessagePublisherTest {

    @Autowired
    private MessagePublisher publisher;

    @Autowired
    private Queue queue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void sendMessage() {
        // 메시지 전송
        String message = "Hello, RabbitMQ!";
        publisher.sendMessage(new MessageDto(message));
        // 메시지 수신
        String receivedMessage = String.valueOf(rabbitTemplate.receiveAndConvert(queue.getName()));

        // 수신한 메시지와 전송한 메시지가 동일한지 확인
        assertThat(message).isEqualTo(receivedMessage);
    }
}