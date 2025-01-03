package cn.opentp.server.network.restful.handler;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.manager.ManagerImpl;
import cn.opentp.server.network.restful.Result;
import cn.opentp.server.service.ManagerService;
import com.google.inject.Injector;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ManagerHandler {

    private final Router router;
    public static final String BASE_URL = "/api/manager/*";
    private final OpentpApp opentpApp = OpentpApp.instance();
    private final Injector injector = opentpApp.injector();

    public ManagerHandler(Vertx vertx) {
        Router router = Router.router(vertx);
        router.post("/info").handler(this::userInfo);

        router.errorHandler(500, ErrorHandler::handleError);
        this.router = router;
    }

    /**
     * 用户信息
     *
     * @param ctx routing context
     */
    private void userInfo(RoutingContext ctx) {
        String username = opentpApp.getManagerUsername();
        if (username.isEmpty()) {
            ctx.response().setStatusCode(401).end("Unauthorized");
        } else {
            ManagerService managerService = injector.getInstance(ManagerService.class);
            ManagerImpl manager = managerService.queryManagerInfo(username);
            // todo
            manager.setRole("admin");
            ctx.json(Result.success(manager));
        }
    }

    public Router getRouter() {
        return router;
    }
}
