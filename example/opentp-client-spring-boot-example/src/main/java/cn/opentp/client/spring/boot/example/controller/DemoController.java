package cn.opentp.client.spring.boot.example.controller;

import cn.opentp.client.configuration.OpentpContext;
import cn.opentp.client.net.NettyClient;
import cn.opentp.core.tp.ThreadPoolWrapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("demo")
public class DemoController {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("test")
    public String test() {
        return "test";
    }

    @GetMapping("tps")
    public String tps() {
        return OpentpContext.all();
    }

    @GetMapping("tp/{tpName}/{coreSize}")
    public String tpCoreSize(@PathVariable String tpName, @PathVariable Integer coreSize) {
        ThreadPoolWrapper threadPoolWrapper = OpentpContext.get(tpName);
//        threadPoolWrapper.getTarget().setCorePoolSize(coreSize);
        return "success";
    }

    @GetMapping("report")
    public String report() {
        Map<String, ThreadPoolWrapper> allTps = OpentpContext.allTps();
        for (ThreadPoolWrapper tpw : allTps.values()) {
            tpw.flush();
            NettyClient.send(tpw);
        }
        return "ok";
    }

    @GetMapping("new")
    public String newThread() {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(1);
            }
        });
        return "ok";
    }
}
