package cn.opentp.server.network.restful.handler;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.application.ApplicationCreateCommand;
import cn.opentp.server.domain.application.ApplicationCreateCommandHandler;
import cn.opentp.server.infrastructure.secret.MD5Util;
import cn.opentp.server.network.restful.Result;
import cn.opentp.server.network.restful.util.ErrorHandler;
import cn.opentp.server.service.domain.DomainCommandInvoker;
import com.google.inject.Injector;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * 应用接口处理器 handler
 *
 * @author zg
 */
public class ApplicationHandler {

    private final Router router;
    public static final String BASE_URL = "/api/application/*";

    private final Injector injector = OpentpApp.instance().injector();
    private final DomainCommandInvoker domainCommandInvoker = injector.getInstance(DomainCommandInvoker.class);

    public ApplicationHandler(Vertx vertx) {
        this.router = Router.router(vertx);

        router.get("/create").handler(this::create);

        router.errorHandler(500, ErrorHandler::handleError);
    }

    /**
     * 创建应用
     *
     * @param ctx routing context
     */
    private void create(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String appName = body.getString("appName");
        String showName = body.getString("showName");

        ApplicationCreateCommandHandler applicationCreateCommandHandler = injector.getInstance(ApplicationCreateCommandHandler.class);
        ApplicationCreateCommand applicationCreateCommand = new ApplicationCreateCommand(appName, showName);
        boolean created = domainCommandInvoker.invoke(applicationCreateCommand, (q, c) -> applicationCreateCommandHandler.handle(q, applicationCreateCommand));
        if (created) {
            ctx.json(Result.success());
        } else {
            ctx.json(Result.fail());
        }
    }

    public Router getRouter() {
        return router;
    }
}
