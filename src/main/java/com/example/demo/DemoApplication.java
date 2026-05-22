package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@SpringBootApplication
@RestController
public class DemoApplication {


    @Value("${app.title}")
    private String title;

    private static final Logger logger = Logger.getLogger(DemoApplication.class.getName());

    @GetMapping("/greet")
    public String greet() {
       logger.info("logging msg");
       logger.warning("warning msg...");
        return title;
    }


    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);
    }

}
