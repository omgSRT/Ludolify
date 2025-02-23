package com.omgsrt.Ludolify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {ReactiveSecurityAutoConfiguration.class})
@EnableCaching
@EnableAsync
@EnableScheduling
public class LudolifyApplication {

    public static void main(String[] args) {
		SpringApplication.run(LudolifyApplication.class, args);
    }

}
