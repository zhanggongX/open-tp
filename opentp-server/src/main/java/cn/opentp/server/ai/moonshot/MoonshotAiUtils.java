package cn.opentp.server.ai.moonshot;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.ai.moonshot.bean.AnsMessage;
import cn.opentp.server.ai.moonshot.bean.Choice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;


public class MoonshotAiUtils {

    private static final Logger log = LoggerFactory.getLogger(MoonshotAiUtils.class);

    private static final String API_KEY = "";
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
        Message system = new Message(RoleEnum.system.name(), "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你是一个JAVA线程专家。你会为用户提供专业的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。");
        Message user = new Message(RoleEnum.user.name(), "我是一个线程监控程序，我目前检测到一个线程池核心线程数是10，最大线程数是20，队列长度是1024，目前核心线程已满，队列内有100个线程在等待，50个汉字内，简短的回答我，可以怎么优化吗？请直接说方法即可。");
        System.out.println(chat("moonshot-v1-32k", List.of(system, user)));
    }

    public static String chat(String model, List<Message> messages) {

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("stream", true);
        String requestBody = JacksonUtil.toJSONString(body);

        String appKey = getApiKey(API_KEY);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(CHAT_COMPLETION_URL))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + appKey)
                .build();

        // 创建 AtomicReference 用于汇总所有行
        AtomicReference<String> allContent = new AtomicReference<>("");

        // 发送请求并逐行处理响应
        CompletableFuture<Object> httpResponseCompletableFuture = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines()).thenApply(response -> {
            // 逐行处理并汇总
            response.body().forEach(line -> {
                String info = line.replace("data:", "");
                info = info.trim();
                if (info.startsWith("[DONE]")) {
                    log.info("Received [DONE]");
                    return;
                }
                if (info.startsWith("{")) {
                    AnsMessage ansMessage = JacksonUtil.parseJson(info, AnsMessage.class);
                    log.info("Received message: {}", ansMessage);
                    if (ansMessage.getChoices() == null || ansMessage.getChoices().isEmpty()) return;
                    for (Choice choice : ansMessage.getChoices()) {
                        if (choice.getFinish_reason() != null && choice.getFinish_reason().equals("stop")) {
                            return;
                        }
                        allContent.updateAndGet(v -> v + choice.getDelta().getContent() + "\n");
                    }
                }
            });
            return null;
        }).exceptionally(err -> {
            log.error("Error occurred: ", err);
            return null;
        });

        // 等待结果并处理
        httpResponseCompletableFuture.thenAccept(response -> {
            if (allContent.get() != null && !allContent.get().isEmpty()) {
                allContent.set(allContent.get().replace("\n", ""));
                log.info("All content received:\n{}", allContent.get());
            } else {
                log.info("No content received.");
            }
        });

        httpResponseCompletableFuture.join();
        return "result";
    }

    private static HttpRequest getCommonRequest(String url) {
        String appKey = getApiKey(API_KEY);
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + appKey)
                .build();
    }

    private static String getApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            try {
                return Files.readString(Paths.get("/opt/moonshot.key")).trim();
            } catch (IOException e) {
                log.error("读取文件 moonshot.key 内容失败：", e);
                return "";
            }
        }
        return apiKey;
    }
}