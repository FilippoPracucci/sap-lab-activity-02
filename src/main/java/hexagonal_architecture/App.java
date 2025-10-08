package hexagonal_architecture;

import hexagonal_architecture.application.*;
import hexagonal_architecture.domain.User;
import hexagonal_architecture.infrastructure.*;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import hexagonal_architecture.domain.Game;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static final Logger logger = Logger.getLogger("[TicTacToe]");
    private static final int PORT = 8080;
    private static final String DB_USERS = "users.json";

    private final UserService userService = new UserServiceImpl();
    private final UserRegistry userRegistry = new VertxFileUserRegistry(DB_USERS);
    private final GameRegistry gameRegistry = new GameRegistryImpl();
    private final GameService gameService = new GameServiceImpl(this.gameRegistry, this.userRegistry);
    private final HttpCommunication server;
    private final VertxEventPublisher publisher;

    public App(final Vertx vertx) {
        this.server = new HttpCommunication(PORT, this.createRouter(vertx), vertx);
        this.publisher = new VertxEventPublisher(vertx, this.server.getHttpServer(), this.gameRegistry);
    }

    public static Logger getLogger() {
        return logger;
    }

    public Router createRouter(final Vertx vertx) {
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
            final User newUser = this.userService.registerUser(userName);
            context.json(new JsonObject()
                    .put("userId", newUser.id())
                    .put("userName", userName));
        });
    }

    private void createNewGame(RoutingContext context) {
        final Game newGame = this.gameService.createNewGame();
        context.json(new JsonObject().put("gameId", newGame.getId()));
    }

    private void joinGame(RoutingContext context) {
        context.request().handler(buffer -> {
            final JsonObject joinInfo = buffer.toJsonObject();
            logger.log(Level.INFO, joinInfo.toString());
            final User user = this.userRegistry.findUserById(joinInfo.getString("userId")).orElseThrow();
            final Game game = this.gameRegistry.findGameById(joinInfo.getString("gameId")).orElseThrow();
            final String symbol = joinInfo.getString("symbol");
            final JsonObject reply = new JsonObject();
            try {
                this.gameService.joinGame(user.id(), game.getId(), symbol);
                reply.put("result", "accepted");
            } catch (Exception ex) {
                reply.put("result", "denied");
            }
            context.json(reply);
        });
    }

    private void makeAMove(RoutingContext context) {
        context.request().handler(buffer -> {
            final JsonObject reply = new JsonObject();
            try {
                final JsonObject moveInfo = buffer.toJsonObject();
                final User user = this.userRegistry.findUserById(moveInfo.getString("userId")).orElseThrow();
                final Game game = this.gameRegistry.findGameById(moveInfo.getString("gameId")).orElseThrow();
                final String symbol = moveInfo.getString("symbol");
                final int x = Integer.parseInt(moveInfo.getString("x"));
                final int y = Integer.parseInt(moveInfo.getString("y"));
                this.gameService.makeAMove(user.id(), game.getId(), symbol, x, y);
                reply.put("result", "accepted");

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
            } catch (Exception ex) {
                reply.put("result", "invalid-move");
            }
            context.json(reply);
        });
    }

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        final App app = new App(vertx);
        vertx.deployVerticle(app.server);
    }
}
