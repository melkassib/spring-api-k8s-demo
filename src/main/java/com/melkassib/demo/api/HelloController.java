package com.melkassib.demo.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HelloController {

    @Value("${app.color}")
    private String color;

    @Value("${POD_NAME:local-machine}")
    private String podName;

    @GetMapping("/hello")
    public HelloResponse hello() {
        return new HelloResponse(
                podName,
                color.toUpperCase(),
                LocalDateTime.now().toString()
        );
    }

    public record HelloResponse(
            String podName,
            String color,
            String currentDate
    ) {}
}