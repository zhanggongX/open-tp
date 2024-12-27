package cn.opentp.server.network.restful;

import cn.opentp.server.network.restful.handler.BusinessHandler;
import cn.opentp.server.network.restful.handler.ConnectHandler;
import cn.opentp.server.network.restful.auth.JwtAuthHandler;
import cn.opentp.server.network.restful.handler.ThreadPoolHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
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

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new RestfulServer());
    }

    @Override
    public void start() throws Exception {

        Router mainRouter = Router.router(this.getVertx());

        // resolve body;
        mainRouter.route().handler(BodyHandler.create());
        // reset timeout time;
        mainRouter.route().handler(TimeoutHandler.create(5000));

        // handlers
        BusinessHandler businessHandler = new BusinessHandler(vertx);
        ConnectHandler connectHandler = new ConnectHandler(vertx);
        ThreadPoolHandler threadPoolHandler = new ThreadPoolHandler(vertx);
        // auth handler
        JwtAuthHandler jwtAuthHandler = new JwtAuthHandler(vertx);

        // register all router
        // register static resource router
        mainRouter.route("/*").handler(StaticHandler.create("static"));
        mainRouter.route("/auth/*").subRouter(jwtAuthHandler.getRouter());
        // jwt auth control
        mainRouter.route("/api/*").handler(JWTAuthHandler.create(jwtAuthHandler.getJwtAuth()));
        // register handlers
        mainRouter.route("/api/business/*").subRouter(businessHandler.getRouter());
        mainRouter.route("/api/connect/*").subRouter(connectHandler.getRouter());
        mainRouter.route("/api/thread-pool/*").subRouter(threadPoolHandler.getRouter());

        // start vert.x server
        vertx.createHttpServer().requestHandler(mainRouter).listen(80).onSuccess(e -> {
            log.info("Restful server start success on port: {}", 80);
        }).onFailure(e -> {
            log.error("Restful server start failure, the port is: {}", 80);
        });
    }
}
