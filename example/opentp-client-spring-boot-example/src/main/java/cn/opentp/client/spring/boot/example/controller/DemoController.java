package cn.opentp.client.spring.boot.example.controller;

import cn.opentp.client.context.OpentpContext;
import cn.opentp.core.tp.ThreadPoolWrapper;
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
        return OpentpContext.all();
    }

    @GetMapping("tp/{tpName}/{coreSize}")
    public String tpCoreSize(@PathVariable String tpName, @PathVariable Integer coreSize) {
        ThreadPoolWrapper threadPoolWrapper = OpentpContext.get(tpName);
        threadPoolWrapper.getTarget().setCorePoolSize(coreSize);
        return "success";
    }
}
