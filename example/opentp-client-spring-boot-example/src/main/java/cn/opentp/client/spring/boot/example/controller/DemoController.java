package cn.opentp.client.spring.boot.example.controller;

import cn.opentp.client.core.context.OpentpContext;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("demo")
public class DemoController {

    @GetMapping("test")
    public String test() {
        return "test";
    }

    @GetMapping("tps")
    public String tps() {
        return OpentpContext.tps();
    }

    @GetMapping("tp/{tpName}/{coreSize}")
    public String tpCoreSize(@PathVariable String tpName, @PathVariable Integer coreSize) {
        ThreadPoolExecutor tp = OpentpContext.getTp(tpName);
        tp.setCorePoolSize(coreSize);
        return "success";
    }
}
