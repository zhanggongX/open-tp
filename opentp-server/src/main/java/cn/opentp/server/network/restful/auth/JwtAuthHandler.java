package cn.opentp.server.network.restful.auth;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.manager.*;
import cn.opentp.server.infrastructure.secret.MD5Util;
import cn.opentp.server.network.restful.Result;
import cn.opentp.server.service.domain.DomainCommandInvoker;
import com.google.inject.Injector;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class JwtAuthHandler {

    /**
     * 登录，注册，登出 base url
     */
    public static final String AUTH_URL = "/auth/*";
    /**
     * 所有需要权限认证的 api 接口
     */
    public static final String PERMISSION_BASE_URL = "/api/*";

    private final Router router;
    private final JWTAuth jwtAuth;
    private final Injector injector = OpentpApp.instance().injector();

    public JwtAuthHandler(Vertx vertx) {
        // todo `my-secret-key 配置化
        JWTAuthOptions config = new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions().setAlgorithm("HS256").setBuffer("my-secret-key"))
                .setJWTOptions(new JWTOptions().setExpiresInMinutes(120));
        this.jwtAuth = JWTAuth.create(vertx, config);

        Router router = Router.router(vertx);
        router.post("/login").handler(this::login);
        router.post("/register").handler(this::register);
        router.post("/change").handler(this::changePassword);
        this.router = router;
    }

    /**
     * 登录
     *
     * @param ctx routing context
     */
    private void login(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String userName = body.getString("userName");
        String password = body.getString("password");


        DomainCommandInvoker domainCommandInvoker = injector.getInstance(DomainCommandInvoker.class);
        ManagerLoginCommandHandler managerLoginCommandHandler = injector.getInstance(ManagerLoginCommandHandler.class);
        ManagerLoginCommand managerLoginCommand = new ManagerLoginCommand(userName, MD5Util.md5(password));
        boolean checkPassed = domainCommandInvoker.invoke(managerLoginCommand, (q, c) -> managerLoginCommandHandler.handle(q, managerLoginCommand));
        if (checkPassed) {
            String token = jwtAuth.generateToken(new JsonObject().put("sub", userName), new JWTOptions());
            ctx.json(new JsonObject().put("token", token));
        } else {
            ctx.response().setStatusCode(401).end("Invalid credentials");
        }
    }

    /**
     * 注册
     *
     * @param ctx routing context
     */
    public void register(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String userName = body.getString("userName");
        String password = body.getString("password");

        DomainCommandInvoker domainCommandInvoker = injector.getInstance(DomainCommandInvoker.class);
        ManagerRegCommandHandler managerRegCommandHandler = injector.getInstance(ManagerRegCommandHandler.class);
        ManagerRegCommand managerRegCommand = new ManagerRegCommand(userName, MD5Util.md5(password));
        boolean invoke = domainCommandInvoker.invoke(managerRegCommand, (q, c) -> managerRegCommandHandler.handle(q, managerRegCommand));
        ctx.json(Result.success(invoke));
    }

    public void changePassword(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String userName = body.getString("userName");
        String password = body.getString("password");
        String newPassword = body.getString("newPassword");

        DomainCommandInvoker domainCommandInvoker = injector.getInstance(DomainCommandInvoker.class);
        ManagerChangeCommandHandler managerChangeCommandHandler = injector.getInstance(ManagerChangeCommandHandler.class);
        ManagerChangeCommand managerChangeCommand = new ManagerChangeCommand(userName, MD5Util.md5(password), MD5Util.md5(newPassword));
        boolean invoke = domainCommandInvoker.invoke(managerChangeCommand, (q, c) -> managerChangeCommandHandler.handle(q, managerChangeCommand));
        ctx.json(Result.success(invoke));
    }


    public JWTAuth getJwtAuth() {
        return jwtAuth;
    }

    public Router getRouter() {
        return router;
    }
}
