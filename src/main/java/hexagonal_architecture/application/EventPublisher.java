package hexagonal_architecture.application;

import annotations.OutBoundPort;

@OutBoundPort
public interface EventPublisher {
    void publish(String gameId, Object event);
}
