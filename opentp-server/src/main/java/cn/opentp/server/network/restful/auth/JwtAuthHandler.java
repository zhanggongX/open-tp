package cn.opentp.server.network.restful.auth;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.manager.ManagerRegCommand;
import cn.opentp.server.domain.manager.ManagerRegCommandHandler;
import cn.opentp.server.service.domain.DomainCommandInvoker;
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

    public JwtAuthHandler(Vertx vertx) {
        // todo `my-secret-key 配置化
        JWTAuthOptions config = new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions().setAlgorithm("HS256").setBuffer("my-secret-key"))
                .setJWTOptions(new JWTOptions().setExpiresInMinutes(120));
        this.jwtAuth = JWTAuth.create(vertx, config);

        Router router = Router.router(vertx);
        router.post("/login").handler(this::login);
        router.post("/register").handler(this::register);
        this.router = router;
    }

    private void login(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String userName = body.getString("userName");
        String password = body.getString("password");

        // login check
        if ("admin".equals(userName) && "password123".equals(password)) {
            String token = jwtAuth.generateToken(new JsonObject().put("sub", userName), new JWTOptions());
            ctx.json(new JsonObject().put("token", token));
        } else {
            ctx.response().setStatusCode(401).end("Invalid credentials");
        }
    }

    public void register(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String userName = body.getString("userName");
        String password = body.getString("password");

        DomainCommandInvoker domainCommandInvoker = OpentpApp.instance().injector().getInstance(DomainCommandInvoker.class);
        ManagerRegCommandHandler managerRegCommandHandler = OpentpApp.instance().injector().getInstance(ManagerRegCommandHandler.class);
        ManagerRegCommand managerRegCommand = new ManagerRegCommand(userName, password);
        boolean invoke = domainCommandInvoker.invoke(managerRegCommand, (q, c) -> managerRegCommandHandler.handle(q, managerRegCommand));

        ctx.json(new JsonObject().put("res", invoke));
    }


    public JWTAuth getJwtAuth() {
        return jwtAuth;
    }

    public Router getRouter() {
        return router;
    }
}
