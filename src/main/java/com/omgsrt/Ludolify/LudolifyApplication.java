package com.omgsrt.Ludolify;

import com.omgsrt.Ludolify.shared.dataInitializer.DataInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootApplication(exclude = {ReactiveSecurityAutoConfiguration.class})
@EnableCaching
@EnableAsync
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class LudolifyApplication {
    private final List<DataInitializer> initializerList;

    public static void main(String[] args) {
		SpringApplication.run(LudolifyApplication.class, args);
    }

    @Bean
    ApplicationRunner runInitializers() {
        return args -> {
            log.info("Running application initializers...");
            Mono.when(initializerList.stream().map(DataInitializer::initialize).toList())
                    .subscribe();
        };
    }
}
