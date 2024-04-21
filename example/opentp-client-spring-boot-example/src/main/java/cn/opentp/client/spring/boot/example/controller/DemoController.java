package cn.opentp.client.spring.boot.example.controller;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.thread.pool.ThreadPoolContext;
import cn.opentp.core.util.JSONUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("test")
public class DemoController {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("threadPool")
    public String tps() {
        Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, ThreadPoolContext> e : threadPoolContextCache.entrySet()) {
            res.append(e.getValue().getState().toString());
        }
        return JSONUtils.toJson(res.toString());
    }

    @GetMapping("threadPool/{tpName}")
    public String tpCoreSize(@PathVariable String tpName) {
        Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
        ThreadPoolContext threadPoolContext = threadPoolContextCache.get(tpName);
        return JSONUtils.toJson(threadPoolContext);
    }

    @GetMapping("threadPool/report")
    public String report() {
        Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
        for (Map.Entry<String, ThreadPoolContext> threadPoolContextEntry : threadPoolContextCache.entrySet()) {
            threadPoolContextEntry.getValue().flushStateAndSetThreadPoolName(threadPoolContextEntry.getKey());
            Configuration.configuration().threadPoolStateReportChannel().writeAndFlush(threadPoolContextEntry.getValue().getState());
        }
        return "ok";
    }

    @GetMapping("threadPool/execute")
    public String newThread() {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return "ok";
    }
}
