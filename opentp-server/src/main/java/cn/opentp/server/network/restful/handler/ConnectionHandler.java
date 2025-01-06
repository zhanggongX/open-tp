package cn.opentp.server.network.restful.handler;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.connection.ConnectionImpl;
import cn.opentp.server.domain.connection.ConnectionRepository;
import com.google.inject.Injector;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class ConnectionHandler {

    public static final String BASE_URL = "/api/connections/*";
    private final Router router;
    private final OpentpApp opentpApp = OpentpApp.instance();
    private final Injector injector = opentpApp.injector();

    public ConnectionHandler(Vertx vertx) {
        this.router = Router.router(vertx);

        router.get("/").handler(this::connections);

        router.errorHandler(500, ErrorHandler::handleError);
    }

    private void connections(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String appKey = body.getString("appKey");
        ConnectionRepository connectionRepository = injector.getInstance(ConnectionRepository.class);
        List<ConnectionImpl> connections = connectionRepository.findByAppKey(appKey);
        ctx.json(connections);
    }

    public Router getRouter() {
        return router;
    }
}
