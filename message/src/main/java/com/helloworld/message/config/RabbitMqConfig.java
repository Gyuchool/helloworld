package com.helloworld.message.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helloworld.message.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class RabbitMqConfig {

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    @Value("${spring.rabbitmq.virtual-host}")
    private String rabbitmqVhost;

    @Value("${spring.rabbitmq.queue.name}")
    private String queueName;

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    private final ObjectMapper objectMapper;

    @Bean
    public Queue queue() {
        return QueueBuilder
                .durable(queueName)
                .deadLetterExchange(exchangeName+".dlx")
                .ttl(300_000)
                .build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable("giron.deadletter")
                .build();
    }

    @Bean
    FanoutExchange deadLetterExchange() {
        return new FanoutExchange(exchangeName+".dlx");
    }


    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback(((correlationData, ack, cause) -> {
            if (!ack) {
                Message message = correlationData.getReturned().getMessage();
                byte[] body = message.getBody();
                log.error("Fail to producerId:{}, body:{}", correlationData.getId(), body);
            }
        }));
        return rabbitTemplate;
    }

    @Component
    public class DeadLetterMessageListener implements MessageListener {

        @Override
        public void onMessage(Message message) {
            try {
                MessageDto messageDto = objectMapper.readValue(message.getBody(), MessageDto.class);
                log.info("Received message from DLQ: {}", messageDto);

            }catch(Exception e){
                log.error("Error while processing message from DLQ", e);
                //reject the message
            }
        }
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             DeadLetterMessageListener listener) {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(3)
                        .backOffOptions(1000, 2, 2000)
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .build()
        );
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(deadLetterQueue());
        container.setMessageListener(listener);
        return container;
    }
}