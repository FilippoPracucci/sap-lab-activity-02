package hexagonal_architecture.infrastructure;

import hexagonal_architecture.domain.GameLogger;
import hexagonal_architecture.domain.Game;
import hexagonal_architecture.domain.User;
import hexagonal_architecture.application.*;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.logging.Level;

public class TTTBackend {

    private final UserRegistry userRegistry;
    private final GameRegistry gameRegistry;
    private final EventPublisher publisher;
    private final App app;

    public TTTBackend(
            final App app,
            final UserRegistry userRegistry,
            final GameRegistry gameRegistry,
            final int port,
            final Vertx vertx)
    {
        this.app = app;
        this.userRegistry = userRegistry;
        this.gameRegistry = gameRegistry;
        HttpCommunication server = new HttpCommunication(port, this.createRouter(vertx), vertx);
        this.publisher = new VertxEventPublisher(vertx, server.getHttpServer(), this.gameRegistry);
        vertx.deployVerticle(server);
    }

    private Router createRouter(final Vertx vertx) {
        final Router router = Router.router(vertx);
        router.route(HttpMethod.POST, "/api/registerUser").handler(this::registerUser);
        router.route(HttpMethod.POST, "/api/createGame").handler(this::createNewGame);
        router.route(HttpMethod.POST, "/api/joinGame").handler(this::joinGame);
        router.route(HttpMethod.POST, "/api/makeAMove").handler(this::makeAMove);
        router.route("/public/*").handler(StaticHandler.create());
        return router;
    }

    private void registerUser(RoutingContext context) {
        context.request().handler(buffer -> {
            final JsonObject userInfo = buffer.toJsonObject();
            final String userName = userInfo.getString("userName");
            final String userId = this.app.registerUser(userName);
            context.json(new JsonObject()
                    .put("userId", userId)
                    .put("userName", userName));
        });
    }

    private void createNewGame(RoutingContext context) {
        final String gameId = this.app.createNewGame();
        context.json(new JsonObject().put("gameId", gameId));
    }

    private void joinGame(RoutingContext context) {
        context.request().handler(buffer -> {
            final JsonObject joinInfo = buffer.toJsonObject();
            GameLogger.getLogger().log(Level.INFO, joinInfo.toString());
            final User user = this.userRegistry.findUserById(joinInfo.getString("userId")).orElseThrow();
            final Game game = this.gameRegistry.findGameById(joinInfo.getString("gameId")).orElseThrow();
            final String symbol = joinInfo.getString("symbol");
            final JsonObject reply = new JsonObject();
            if (this.app.joinGame(user.id(), game.getId(), symbol)) {
                reply.put("result", "accepted");
            } else {
                reply.put("result", "denied");
            }
            context.json(reply);
        });
    }

    private void makeAMove(RoutingContext context) {
        context.request().handler(buffer -> {
            final JsonObject reply = new JsonObject();
            final JsonObject moveInfo = buffer.toJsonObject();
            final User user = this.userRegistry.findUserById(moveInfo.getString("userId")).orElseThrow();
            final Game game = this.gameRegistry.findGameById(moveInfo.getString("gameId")).orElseThrow();
            final String symbol = moveInfo.getString("symbol");
            final int x = Integer.parseInt(moveInfo.getString("x"));
            final int y = Integer.parseInt(moveInfo.getString("y"));
            if (this.app.makeAMove(user.id(), game.getId(), symbol, x, y)) {
                reply.put("result", "accepted");
            } else {
                reply.put("result", "invalid-move");
            }

            final JsonObject event = new JsonObject()
                    .put("event", "new-move")
                    .put("x", x)
                    .put("y", y)
                    .put("symbol", symbol);
            this.publisher.publish(game.getId(), event);

            if (game.isGameEnd()) {
                final JsonObject eventEnd = new JsonObject();
                eventEnd.put("event", "game-ended");
                if (game.isTie()) {
                    eventEnd.put("result", "tie");
                } else {
                    eventEnd.put("winner", game.getWinner().get().toString().toLowerCase());

                }
                this.publisher.publish(game.getId(), eventEnd);
            }
            context.json(reply);
        });
    }
}
