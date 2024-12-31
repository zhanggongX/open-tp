package cn.opentp.server.network.restful;

import cn.opentp.server.network.restful.handler.BusinessHandler;
import cn.opentp.server.network.restful.handler.ConnectHandler;
import cn.opentp.server.network.restful.auth.JwtAuthHandler;
import cn.opentp.server.network.restful.handler.ThreadPoolHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1. restful server
 * 2. resolve static resources
 *
 * @author zg
 */
public class RestfulServer extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(RestfulServer.class);
    private final int port;

    public RestfulServer(int port) {
        this.port = port;
    }

    public static void start(int port) {
        Vertx.vertx().deployVerticle(new RestfulServer(port));
    }

    @Override
    public void start() throws Exception {

        Router mainRouter = Router.router(this.getVertx());

        // resolve body;
        mainRouter.route().handler(BodyHandler.create());
        // set timeout time;
        mainRouter.route().handler(TimeoutHandler.create(5000));

        // handlers
        BusinessHandler businessHandler = new BusinessHandler(vertx);
        ConnectHandler connectHandler = new ConnectHandler(vertx);
        ThreadPoolHandler threadPoolHandler = new ThreadPoolHandler(vertx);
        // auth handler
        JwtAuthHandler jwtAuthHandler = new JwtAuthHandler(vertx);

        // register all router
        // register static resource router
        mainRouter.route("/*").handler(CorsHandler.create());
        mainRouter.route("/*").handler(StaticHandler.create("static"));
        mainRouter.route(JwtAuthHandler.AUTH_URL).subRouter(jwtAuthHandler.getRouter());
        // jwt auth control
        mainRouter.route(JwtAuthHandler.PERMISSION_BASE_URL).handler(JWTAuthHandler.create(jwtAuthHandler.getJwtAuth()));
        // register handlers
        mainRouter.route(BusinessHandler.BASE_URL).subRouter(businessHandler.getRouter());
        mainRouter.route(ConnectHandler.BASE_URL).subRouter(connectHandler.getRouter());
        mainRouter.route(ThreadPoolHandler.BASE_URL).subRouter(threadPoolHandler.getRouter());

        // start vert.x server
        vertx.createHttpServer().requestHandler(mainRouter).listen(port)
                .onSuccess(e -> {
                    log.info("Restful server start success on port: {}", port);
                }).onFailure(e -> {
                    log.error("Restful server start failure");
                });
    }
}
