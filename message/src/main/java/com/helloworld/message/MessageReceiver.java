package com.helloworld.message;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    @RabbitListener(queues = "${spring.rabbitmq.queue}", concurrency = "3")
    public void receiveMessage(String message) {

        System.out.println("Received message: " + message);
    }
}
