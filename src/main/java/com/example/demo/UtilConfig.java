package com.example.demo;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class UtilConfig {
    @Value("${spring.key}")
    private String value;

    @PostConstruct
    public void init() {
        // This runs after dependency injection is complete
        readValue();
    }

    public void readValue() {
        System.out.println("Property value: " + value);
    }

}
