package com.mtran.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.mtran.common", "com.mtran.mvc"})
public class KeyCloakApplication {
    public static void main(String[] args) {
        SpringApplication.run(KeyCloakApplication.class, args);
    }
}
