package cn.opentp.server.network.restful.handler;

import cn.opentp.server.network.restful.util.ErrorHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * business handler
 *
 * @author zg
 */
public class BusinessHandler {

    private final Router router;

    public BusinessHandler(Vertx vertx) {
        this.router = Router.router(vertx);

        router.get("/demo").handler(ctx -> {
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
