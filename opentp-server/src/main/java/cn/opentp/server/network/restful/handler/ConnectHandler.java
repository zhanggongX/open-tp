package cn.opentp.server.network.restful.handler;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class ConnectHandler {

    public static final String BASE_URL = "/api/connect/*";
    private final Router router;

    public ConnectHandler(Vertx vertx) {
        this.router = Router.router(vertx);

        router.get("/api/businesses").handler(ctx -> {
            ctx.json("hello world");
        });

        router.get("/api/businesses/:id").handler(ctx -> {
            ctx.json("hello world");
        });

        router.post("/api/businesses").handler(ctx -> {
            ctx.json("hello world");
        });

        router.put("/api/businesses/:id").handler(ctx -> {
            ctx.json("hello world");
        });

        router.delete("/api/businesses/:id").handler(ctx -> {
            ctx.json("hello world");
        });

        router.errorHandler(500, ErrorHandler::handleError);
    }

    public Router getRouter() {
        return router;
    }
}
