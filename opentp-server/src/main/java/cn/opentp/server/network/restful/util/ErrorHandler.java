package cn.opentp.server.network.restful.util;

import io.vertx.ext.web.RoutingContext;

public class ErrorHandler {
    public static void handleError(RoutingContext ctx) {

        Throwable failure = ctx.failure();
        int statusCode = 500;

        if (failure instanceof IllegalArgumentException) {
            statusCode = 400; // Bad Request
        }

        ctx.response()
                .setStatusCode(statusCode)
                .putHeader("Content-Type", "application/json")
                .end("{\"error\": \"" + failure.getMessage() + "\"}");
    }
}