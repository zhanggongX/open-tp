package cn.opentp.client.spring.boot.example.controller;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.thread.pool.ThreadPoolWrapper;
import cn.opentp.core.util.JacksonUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("test")
public class DemoController {

    private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("threadPool")
    public String tps() {
        Map<String, ThreadPoolWrapper> threadPoolContextCache = Configuration._cfg().threadPoolContextCache();
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, ThreadPoolWrapper> e : threadPoolContextCache.entrySet()) {
            res.append(e.getValue().getState().toString());
        }
        return JacksonUtil.toJSONString(res.toString());
    }

    @GetMapping("threadPool/{tpName}")
    public String tpCoreSize(@PathVariable String tpName) {
        Map<String, ThreadPoolWrapper> threadPoolContextCache = Configuration._cfg().threadPoolContextCache();
        ThreadPoolWrapper threadPoolContext = threadPoolContextCache.get(tpName);
        return JacksonUtil.toJSONString(threadPoolContext);
    }

    @GetMapping("threadPool/report")
    public String report() {
        Map<String, ThreadPoolWrapper> threadPoolContextCache = Configuration._cfg().threadPoolContextCache();
        for (Map.Entry<String, ThreadPoolWrapper> threadPoolContextEntry : threadPoolContextCache.entrySet()) {
            threadPoolContextEntry.getValue().flushStateAndSetThreadPoolName(threadPoolContextEntry.getKey());
//            Configuration._cfg().reportService()..writeAndFlush(threadPoolContextEntry.getValue().getState());
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
                    log.error("中断", e);
                }
            }
        });
        return "ok";
    }
}
