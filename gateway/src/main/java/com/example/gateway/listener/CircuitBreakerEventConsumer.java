package com.example.gateway.listener;

import static io.github.resilience4j.circuitbreaker.CircuitBreaker.StateTransition.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.gateway.service.SlackNotificationService;

import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.github.resilience4j.core.EventConsumer;


@Component
public class CircuitBreakerEventConsumer implements EventConsumer<CircuitBreakerOnStateTransitionEvent> {
	private static final Logger logger = LoggerFactory.getLogger("CircuitBreakerEventConsumer");
	private final SlackNotificationService slackNotificationService;

	public CircuitBreakerEventConsumer(SlackNotificationService slackNotificationService) {
		this.slackNotificationService = slackNotificationService;
	}

	@Override
	public void consumeEvent(CircuitBreakerOnStateTransitionEvent transitionEvent) {
		if (transitionEvent.getStateTransition() == CLOSED_TO_OPEN) {
			logger.warn("[서킷] 열렸다!!!!!!!!");
			slackNotificationService.send("서킷 브레이커가 열렸습니다: " + transitionEvent.getCircuitBreakerName() + ":: 시간:" + transitionEvent.getCreationTime());
		} else if (transitionEvent.getStateTransition() == OPEN_TO_CLOSED || transitionEvent.getStateTransition() == HALF_OPEN_TO_CLOSED) {
			logger.info("[서킷] 닫혔다!!!!!!!!!");
			slackNotificationService.send("서킷 브레이커가 닫혔습니다: " + transitionEvent.getCircuitBreakerName() + ":: 시간:" + transitionEvent.getCreationTime());
		}
	}
}
