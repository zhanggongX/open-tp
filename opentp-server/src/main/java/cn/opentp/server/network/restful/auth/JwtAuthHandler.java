package cn.opentp.server.network.restful.auth;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;

public class JwtAuthHandler {

    /**
     * login or logout base url;
     */
    public static final String AUTH_URL = "/auth/*";
    /**
     * All APIs under this path require permission verification；
     */
    public static final String PERMISSION_BASE_URL = "/api/*";

    private final Router router;
    private final JWTAuth jwtAuth;

    public JwtAuthHandler(Vertx vertx) {
        // todo the `my-secret-key should be config`
        JWTAuthOptions config = new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions().setAlgorithm("HS256").setBuffer("my-secret-key"))
                .setJWTOptions(new JWTOptions().setExpiresInMinutes(120));

        this.jwtAuth = JWTAuth.create(vertx, config);

        Router router = Router.router(vertx);
        // login route
        router.post("/login").handler(ctx -> {
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
        });

        this.router = router;
    }

    public JWTAuth getJwtAuth() {
        return jwtAuth;
    }

    public Router getRouter() {
        return router;
    }
}
