package com.example.ejadwebapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EjadWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(EjadWebApplication.class, args);
    }

}
