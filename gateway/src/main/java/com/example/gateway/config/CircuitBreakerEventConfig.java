package com.example.gateway.config;

import org.springframework.context.annotation.Configuration;

import com.example.gateway.listener.CircuitBreakerEventConsumer;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Configuration
public class CircuitBreakerEventConfig {

	public CircuitBreakerEventConfig(CircuitBreakerRegistry circuitBreakerRegistry, CircuitBreakerEventConsumer circuitBreakerEventConsumer) {

		circuitBreakerRegistry.getAllCircuitBreakers()
			.forEach(it -> it.getEventPublisher().onStateTransition(circuitBreakerEventConsumer));

	}

}
