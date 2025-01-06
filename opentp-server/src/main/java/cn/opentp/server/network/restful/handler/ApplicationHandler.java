package cn.opentp.server.network.restful.handler;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.DomainException;
import cn.opentp.server.domain.application.*;
import cn.opentp.server.infrastructure.util.PageUtil;
import cn.opentp.server.network.restful.PageResult;
import cn.opentp.server.network.restful.Result;
import cn.opentp.server.service.ApplicationService;
import cn.opentp.server.service.domain.DomainCommandInvoker;
import com.google.inject.Injector;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 应用接口处理器 handler
 *
 * @author zg
 */
public class ApplicationHandler {

    private final Router router;
    public static final String BASE_URL = "/api/applications/*";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final OpentpApp opentpApp = OpentpApp.instance();
    private final Injector injector = opentpApp.injector();
    private final DomainCommandInvoker domainCommandInvoker = injector.getInstance(DomainCommandInvoker.class);
    private final ApplicationCreateCommandHandler applicationCreateCommandHandler = injector.getInstance(ApplicationCreateCommandHandler.class);
    private final ApplicationDeleteCommandHandler applicationDeleteCommandHandler = injector.getInstance(ApplicationDeleteCommandHandler.class);
    private final ApplicationUpdateCommandHandler applicationUpdateCommandHandler = injector.getInstance(ApplicationUpdateCommandHandler.class);
    private final ApplicationService applicationService = injector.getInstance(ApplicationService.class);

    public ApplicationHandler(Vertx vertx) {
        this.router = Router.router(vertx);

        router.post("/").handler(this::create);
        router.get("/").handler(this::applications);
        router.put("/:appKey").handler(this::update);
        router.delete("/:appKey").handler(this::delete);

        router.errorHandler(500, ErrorHandler::handleError);
    }

    private void update(RoutingContext ctx) {
        String appKey = ctx.pathParam("appKey");
        JsonObject body = ctx.body().asJsonObject();
        String showName = body.getString("showName");
        String appName = body.getString("appName");

        ApplicationUpdateCommand applicationUpdateCommand = new ApplicationUpdateCommand(showName, appName, appKey);
        domainCommandInvoker.invoke((q) -> applicationUpdateCommandHandler.handle(q, applicationUpdateCommand));
        ctx.json(Result.success());
    }

    private void delete(RoutingContext ctx) {
        String appKey = ctx.pathParam("appKey");
        if (appKey == null || appKey.isEmpty()) {
            throw new UnsupportedOperationException("the appKey is empty");
        }
        ApplicationDeleteCommand applicationDeleteCommand = new ApplicationDeleteCommand(appKey);
        domainCommandInvoker.invoke((q) -> applicationDeleteCommandHandler.handle(q, applicationDeleteCommand));
        ctx.json(Result.success());
    }

    /**
     * query all applications
     *
     * @param ctx routing context
     */
    private void applications(RoutingContext ctx) {
        String appName = ctx.request().getParam("appName");
        String appKey = ctx.request().getParam("appKey");
        String current = ctx.request().getParam("current");
        String pageSize = ctx.request().getParam("pageSize");
        int currentVal = Integer.parseInt(current == null ? "1" : current);
        int pageSizeVal = Integer.parseInt(pageSize == null ? "20" : pageSize);

        String username = opentpApp.getManagerUsername();
        List<ApplicationImpl> applications = applicationService.applications(username);
        if (appName != null && !appName.isEmpty()) {
            applications = applications.stream().filter(app -> appName.equals(app.getAppName())).toList();
        }
        if (appKey != null && !appKey.isEmpty()) {
            applications = applications.stream().filter(app -> appKey.equals(app.getAppKey())).toList();
        }
        // 分页
        applications = PageUtil.page(applications, currentVal, pageSizeVal);
        ctx.json(Result.success(new PageResult<>(applications)));
    }

    /**
     * create application
     *
     * @param ctx routing context
     */
    private void create(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String appName = body.getString("appName");
        String showName = body.getString("showName");
        if (appName == null || showName == null || appName.isEmpty() || showName.isEmpty()) {
            throw new DomainException("the params are empty");
        }

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
