package com.omgsrt.Ludolify.shared.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@PropertySource("classpath:local.env")
public class DotEnvConfiguration {
    @EventListener(ApplicationReadyEvent.class)
    public void loadLocalDotEnv(){
        Dotenv dotenv = Dotenv.configure()
                .filename("local.env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String jwtSecret = getEnvVar(dotenv, "JWT_SECRET");
        String mongodbUser = getEnvVar(dotenv, "MONGODB_USER");
        String mongodbPassword = getEnvVar(dotenv, "MONGODB_PASSWORD");

        //set data from local.env to application.yml
        setSystemProperty("JWT_SECRET", jwtSecret);
        setSystemProperty("MONGODB_USER", mongodbUser);
        setSystemProperty("MONGODB_PASSWORD", mongodbPassword);
    }

    private String getEnvVar(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(key + " not found in local.env");
        }
        return value;
    }

    private void setSystemProperty(String key, String value) {
        System.clearProperty(key);
        System.setProperty(key, value);
    }
}
