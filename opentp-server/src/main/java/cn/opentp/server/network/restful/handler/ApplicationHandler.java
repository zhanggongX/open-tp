package cn.opentp.server.network.restful.handler;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.application.ApplicationCreateCommand;
import cn.opentp.server.domain.application.ApplicationCreateCommandHandler;
import cn.opentp.server.domain.application.ApplicationImpl;
import cn.opentp.server.network.restful.Result;
import cn.opentp.server.service.ApplicationService;
import cn.opentp.server.service.domain.DomainCommandInvoker;
import com.google.inject.Injector;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

/**
 * 应用接口处理器 handler
 *
 * @author zg
 */
public class ApplicationHandler {

    private final Router router;
    public static final String BASE_URL = "/api/applications/*";

    private final OpentpApp opentpApp = OpentpApp.instance();
    private final Injector injector = opentpApp.injector();
    private final DomainCommandInvoker domainCommandInvoker = injector.getInstance(DomainCommandInvoker.class);
    private final ApplicationCreateCommandHandler applicationCreateCommandHandler = injector.getInstance(ApplicationCreateCommandHandler.class);
    private final ApplicationService applicationService = injector.getInstance(ApplicationService.class);

    public ApplicationHandler(Vertx vertx) {
        this.router = Router.router(vertx);

        router.post("/create").handler(this::create);
        router.get("/info").handler(this::applications);

        router.errorHandler(500, ErrorHandler::handleError);
    }

    /**
     * 查询所有的应用
     *
     * @param ctx routing context
     */
    private void applications(RoutingContext ctx) {
        String username = opentpApp.getUsername();
        List<ApplicationImpl> applications = applicationService.applications(username);
        ctx.json(applications);
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

        ApplicationCreateCommand applicationCreateCommand = new ApplicationCreateCommand(appName, showName);
        Boolean created = domainCommandInvoker.invoke((q) -> applicationCreateCommandHandler.handle(q, applicationCreateCommand));
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
