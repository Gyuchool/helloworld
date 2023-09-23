package com.helloworld.message.consumer;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public void receiveMessage(Message message, Channel channel) {
        long tag = message.getMessageProperties().getDeliveryTag();
        log.info("Received message:{}, Delivery tag:{}", new String(message.getBody()), tag);

        try {
            // 처리가 성공적으로 완료되면 메시지에 대한 ACK 전송
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 처리 중 예외가 발생한 경우 메시지에 대한 NACK 전송
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ex) {
                log.error("nack error:{}", ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        }
    }
}
