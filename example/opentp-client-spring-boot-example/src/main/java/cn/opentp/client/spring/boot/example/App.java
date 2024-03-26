package cn.opentp.client.spring.boot.example;

import opentp.client.spring.boot.starter.annotation.EnableOpentp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableOpentp
@SpringBootApplication
public class App {
    public static void main(String[] args) {

        SpringApplication.run(App.class, args);
    }
}
