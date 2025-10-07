package hexagonal_architecture.application;

import hexagonal_architecture.App;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import java.util.logging.Level;

public class HttpCommunication extends VerticleBase {

    private final int port;
    private final Router router;
    private final Vertx vertx;

    public HttpCommunication(final int port, final Router router, final Vertx vertx) {
        this.port = port;
        this.router = router;
        this.vertx = vertx;
    }

    @Override
    public Future<?> start() {
        final Future<HttpServer> future = this.vertx.createHttpServer().requestHandler(this.router).listen(this.port);
        future.onSuccess(res -> App.getLogger().log(Level.INFO, "TTT Game Server ready - port: " + this.port));
        return future;
    }
}
