package hexagonal_architecture;

import hexagonal_architecture.infrastructure.App;
import io.vertx.core.Vertx;

public class Launcher {
    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        final App app = new App(vertx);
    }
}
