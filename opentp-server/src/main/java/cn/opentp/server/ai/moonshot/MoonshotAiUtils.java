package cn.opentp.server.ai.moonshot;

import cn.opentp.core.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class MoonshotAiUtils {

    private static final Logger log = LoggerFactory.getLogger(MoonshotAiUtils.class);

    private static final String API_KEY = "sk-qUydd6ZuCgeVrC5W3RM3S3pS9rcvkPFVcz5B51WtEn15ATrV";
    private static final String MODELS_URL = "https://api.moonshot.cn/v1/models";
    // private static final String FILES_URL = "https://api.moonshot.cn/v1/files";
    // private static final String ESTIMATE_TOKEN_COUNT_URL = "https://api.moonshot.cn/v1/tokenizers/estimate-token-count";
    private static final String CHAT_COMPLETION_URL = "https://api.moonshot.cn/v1/chat/completions";

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static String getModelList() {
        HttpRequest httpRequest = getCommonRequest(MODELS_URL);
        String result = "";
        try {
            HttpResponse<String> send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            result = send.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void main(String[] args) {
//        System.out.println(getModelList());
        System.out.println(chat("moonshot-v1-8k", List.of(new Message(RoleEnum.assistant.name(), "你好"))));
    }

    public static String chat(String model, List<Message> messages) {

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("stream", true);
        String requestBody = JacksonUtil.toJSONString(body);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(CHAT_COMPLETION_URL))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .build();

        // 发送请求并逐行处理响应
        CompletableFuture<String> completableFuture = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenApply(info -> {
                    StringBuilder contents = new StringBuilder();
                    info.body().forEach(line -> {
                        contents.append(line);
                    });
                    return contents.toString();
                }).exceptionally(err -> {
                    log.error("异常： ", err);
                    return err.getMessage();
                });

        completableFuture.thenAccept(result -> log.info("result: {}", result));

        completableFuture.join();
        return "result";
    }

    private static HttpRequest getCommonRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + API_KEY)
                .build();
    }
}