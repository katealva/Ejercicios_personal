package com.example.oreo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Oreo2Application {
    public static void main(String[] args) {
        SpringApplication.run(Oreo2Application.class, args);
    }

}
