package com.mtran.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.mtran.common", "com.mtran.mvc"})
public class StoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreServiceApplication.class, args);
    }

}
