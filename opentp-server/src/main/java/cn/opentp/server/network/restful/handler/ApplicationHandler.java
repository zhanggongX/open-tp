package cn.opentp.server.network.restful.handler;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.DomainException;
import cn.opentp.server.domain.application.ApplicationCreateCommand;
import cn.opentp.server.domain.application.ApplicationCreateCommandHandler;
import cn.opentp.server.domain.application.ApplicationImpl;
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
    private final ApplicationService applicationService = injector.getInstance(ApplicationService.class);

    public ApplicationHandler(Vertx vertx) {
        this.router = Router.router(vertx);

        router.post("/").handler(this::create);
        router.get("/").handler(this::applications);

        router.errorHandler(500, ErrorHandler::handleError);
    }

    /**
     * 查询所有的应用
     *
     * @param ctx routing context
     */
    private void applications(RoutingContext ctx) {
        String appName = ctx.request().getParam("appName");
        String appKey = ctx.request().getParam("appKey");
        int current = Integer.parseInt(ctx.request().getParam("current"));
        int pageSize = Integer.parseInt(ctx.request().getParam("pageSize"));

        String username = opentpApp.getManagerUsername();
        List<ApplicationImpl> applications = applicationService.applications(username);
        if (appName != null && !appName.isEmpty()) {
            applications = applications.stream().filter(app -> appName.equals(app.getAppName())).toList();
        }
        if (appKey != null && !appKey.isEmpty()) {
            applications = applications.stream().filter(app -> appKey.equals(app.getAppKey())).toList();
        }
        // 分页
        applications = PageUtil.page(applications, current, pageSize);
        ctx.json(Result.success(new PageResult<>(applications)));
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
        if (appName == null || showName == null) {
            throw new DomainException("参数不能为空");
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
