### api-gateway
cb + api-gw조합.
- spring cloud gateway
- webflux

CircuitBreakerOnStateTransitionEvent를 구현한 CircuitBreakerEventConsumer를 eventPublisher에 등록한후,
서킷이 변경되면 Event를 Consume한 후 서킷의 변경 사항을 슬랙으로 알림을 보낸다. 
