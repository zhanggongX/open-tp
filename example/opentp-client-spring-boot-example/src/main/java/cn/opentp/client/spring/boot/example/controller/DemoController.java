package cn.opentp.client.spring.boot.example.controller;

import jakarta.annotation.Resource;
import opentp.client.spring.boot.starter.annotation.EnableOpentp;
import opentp.client.spring.boot.starter.configuration.OpentpAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableOpentp
@RestController
@RequestMapping("demo")
public class DemoController {

    @Resource
    private OpentpAutoConfiguration opentpAutoConfiguration;

    @GetMapping("test")
    public String test(){
        return "test";
    }
}
