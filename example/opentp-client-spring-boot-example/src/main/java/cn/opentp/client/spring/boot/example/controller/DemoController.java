package cn.opentp.client.spring.boot.example.controller;

import cn.opentp.client.core.context.OpentpContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
