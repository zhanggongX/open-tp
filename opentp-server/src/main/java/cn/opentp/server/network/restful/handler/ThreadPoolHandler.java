package cn.opentp.server.network.restful.handler;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.connection.ConnectionImpl;
import cn.opentp.server.domain.connection.ConnectionRepository;
import cn.opentp.server.domain.threadpool.ThreadPoolRepository;
import cn.opentp.server.network.restful.Result;
import com.google.inject.Injector;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class ThreadPoolHandler {

    public static final String BASE_URL = "/api/thread-pools/*";
    private final Router router;
    private final OpentpApp opentpApp = OpentpApp.instance();
    private final Injector injector = opentpApp.injector();

    public ThreadPoolHandler(Vertx vertx) {
        this.router = Router.router(vertx);

        router.get("/").handler(this::threadPools);

        router.errorHandler(500, ErrorHandler::handleError);
    }

    private void threadPools(RoutingContext ctx) {
        String ipAndPid = ctx.request().params().get("ipAndPid");

        ThreadPoolRepository threadPoolRepository = injector.getInstance(ThreadPoolRepository.class);
        List<String> tpNames = threadPoolRepository.findByIpAndPid(ipAndPid);
        ctx.json(Result.success(tpNames));
    }

    public Router getRouter() {
        return router;
    }
}
