package cn.opentp.server.network.restful;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.network.restful.handler.ApplicationHandler;
import cn.opentp.server.network.restful.handler.*;
import cn.opentp.server.network.restful.handler.JwtAuthHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.User;
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
        ApplicationHandler applicationHandler = new ApplicationHandler(vertx);
        ConnectionHandler connectionHandler = new ConnectionHandler(vertx);
        ThreadPoolHandler threadPoolHandler = new ThreadPoolHandler(vertx);
        ManagerHandler managerHandler = new ManagerHandler(vertx);
        JwtAuthHandler jwtAuthHandler = new JwtAuthHandler(vertx);

        // register all router
        // register static resource router
        mainRouter.route("/*").handler(CorsHandler.create().
                allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.OPTIONS));
        mainRouter.route("/*").handler(StaticHandler.create("static"));
        mainRouter.route(JwtAuthHandler.AUTH_URL).subRouter(jwtAuthHandler.getRouter());
        mainRouter.route(JwtAuthHandler.PERMISSION_BASE_URL).handler(JWTAuthHandler.create(jwtAuthHandler.getJwtAuth()));
        // 设置用户信息
        mainRouter.route(JwtAuthHandler.PERMISSION_BASE_URL).handler(ctx -> {
            if (OpentpApp.instance().getManagerUsername() == null || OpentpApp.instance().getManagerUsername().isEmpty()) {
                User user = ctx.user();
                if (user != null) {
                    String username = user.principal().getString("sub");
                    OpentpApp.instance().setManagerUsername(username);
                }else{
                    throw new UnsupportedOperationException("请先登录");
                }
            }
            ctx.next();
        });
        mainRouter.route(ApplicationHandler.BASE_URL).subRouter(applicationHandler.getRouter());
        mainRouter.route(ConnectionHandler.BASE_URL).subRouter(connectionHandler.getRouter());
        mainRouter.route(ThreadPoolHandler.BASE_URL).subRouter(threadPoolHandler.getRouter());
        mainRouter.route(ManagerHandler.BASE_URL).subRouter(managerHandler.getRouter());

        // start vert.x server
        vertx.createHttpServer().requestHandler(mainRouter).listen(port)
                .onSuccess(e -> {
                    log.info("Restful server start success on port: {}", port);
                }).onFailure(e -> {
                    log.error("Restful server start failure");
                });
    }
}
