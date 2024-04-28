package com.example.gateway.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Service
public class SlackNotificationService {

	private static final Logger logger = LoggerFactory.getLogger("SlackNotificationService");

	private final WebClient webClient = WebClient.create();

	private final ObjectMapper objectMapper;
	private final String slackWebhookUrl;

	public SlackNotificationService(@Value("${slack.webhook.url}") String slackWebhookUrl, ObjectMapper objectMapper) {
		this.slackWebhookUrl = slackWebhookUrl;
		this.objectMapper = objectMapper;
	}


	public void send(String message){
		Mono<String> resultMono  = sendSlackMessage(message);
		resultMono.doOnSuccess(result -> logger.info("Message sent successfully: " + result))
			.doOnError(error -> logger.error("Error sending message: " + error.getMessage()))
			.subscribe();
	}

	public Mono<String> sendSlackMessage(String message) {
		String jsonMessage = getMessage(message);

		return webClient.post()
			.uri(slackWebhookUrl)
			.bodyValue(jsonMessage)
			.retrieve()
			.bodyToMono(String.class);
	}

	private String getMessage(String message) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put("text", message);

		try {
			return objectMapper.writeValueAsString(messageMap);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON 변환 중 오류 발생", e);
		}
	}
}
