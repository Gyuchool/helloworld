package com.helloworld.message.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DlqConfig {

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;


    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable("giron.deadletter")
                .build();
    }

    @Bean
    FanoutExchange deadLetterExchange() {
        return ExchangeBuilder
                .fanoutExchange(exchangeName + ".dlx")
                .build();
    }


    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

    @Bean
    SimpleRabbitListenerContainerFactory deadListenerContainer(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory container = new SimpleRabbitListenerContainerFactory();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
