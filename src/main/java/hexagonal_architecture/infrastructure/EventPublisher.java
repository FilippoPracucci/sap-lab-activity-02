package hexagonal_architecture.infrastructure;

public interface EventPublisher {
    void publish(String gameId, Object event);
}
