package hexagonal_architecture.application;

import hexagonal_architecture.infrastructure.EventPublisher;
import io.vertx.core.eventbus.EventBus;

public class VertxEventPublisher implements EventPublisher {

    private static final String TTT_CHANNEL = "ttt-events";

    private final EventBus eventBus;

    public VertxEventPublisher(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void publish(final String gameId, final Object event) {
        final String channel = TTT_CHANNEL + "-" + gameId;
        this.eventBus.publish(channel, event);
    }
}
