package com.example.event_driven_practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EventDrivenPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventDrivenPracticeApplication.class, args);
    }

}
