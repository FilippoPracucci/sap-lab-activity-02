package hexagonal_architecture.infrastructure;

import hexagonal_architecture.application.*;
import io.vertx.core.Vertx;

public class Launcher {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        final UserRegistry userRegistry = new VertxFileUserRegistry();
        final GameRegistry gameRegistry = new GameRegistryImpl();
        final App app = new AppImpl(new UserServiceImpl(userRegistry), new GameServiceImpl(gameRegistry, userRegistry));
        new TTTBackend(app, userRegistry, gameRegistry, PORT, vertx);
    }
}
