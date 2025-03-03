package com.fintracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinTrackerApplication.class, args);
    }
}