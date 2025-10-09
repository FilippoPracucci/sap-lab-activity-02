package hexagonal_architecture.application;

public interface EventPublisher {
    void publish(String gameId, Object event);
}
