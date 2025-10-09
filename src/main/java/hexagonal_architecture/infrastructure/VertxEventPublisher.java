package hexagonal_architecture.infrastructure;

import hexagonal_architecture.domain.Game;
import hexagonal_architecture.domain.GameLogger;
import hexagonal_architecture.application.EventPublisher;
import hexagonal_architecture.application.GameRegistry;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;

import java.util.logging.Level;

public class VertxEventPublisher implements EventPublisher {

    private final EventBus eventBus;

    public VertxEventPublisher(final Vertx vertx, final HttpServer server, final GameRegistry gameRegistry) {
        this.eventBus = vertx.eventBus();
        server.webSocketHandler(webSocket -> {
            GameLogger.getLogger().log(Level.INFO, "New TTT subscription accepted.");

            /*
             *
             * Receiving a first message including the id of the game
             * to observe
             *
             */
            webSocket.textMessageHandler(openMsg -> {
                GameLogger.getLogger().log(Level.INFO, "For game: " + openMsg);
                final JsonObject obj = new JsonObject(openMsg);
                final String gameId = obj.getString("gameId");

                /*
                 * Subscribing events on the event bus to receive
                 * events concerning the game, to be notified
                 * to the frontend using the websocket
                 *
                 */
                this.consume(gameId, webSocket);

                /*
                 *
                 * When both players joined the game and both
                 * have the websocket connection ready,
                 * the game can start
                 *
                 */
                final Game game = gameRegistry.findGameById(gameId).orElseThrow();
                this.gameStart(game);
            });
        });
    }

    @Override
    public void publish(final String gameId, final Object event) {
        this.eventBus.publish(this.gameAddress(gameId), event);
    }

    private void consume(final String gameId, final WebSocket webSocket) {
        this.eventBus.consumer(this.gameAddress(gameId), msg -> {
            final JsonObject event = (JsonObject) msg.body();
            GameLogger.getLogger().log(Level.INFO, "Notifying event to the frontend: " + event.encodePrettily());
            webSocket.writeTextMessage(event.encodePrettily());
        });
    }

    private String gameAddress(final String gameId) {
        return "ttt-events-" + gameId;
    }

    private void gameStart(final Game game) {
        if (game.bothPlayersJoined()) {
            try {
                game.start();
                final JsonObject eventGameStarted = new JsonObject()
                        .put("event", "game-started");
                this.publish(game.getId(), eventGameStarted);
            } catch (Exception ex) {
                GameLogger.getLogger().log(Level.INFO, "Unable to start the game");
            }
        }

    }
}
