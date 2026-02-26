package com.library.spaceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaceServiceApplication.class, args);
    }
}
