package com.omgsrt.Ludolify.shared.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
//@PropertySource("classpath:local.env")
public class DotEnvConfiguration {
    @EventListener(ApplicationEnvironmentPreparedEvent.class)
    public void loadLocalDotEnv(ApplicationEnvironmentPreparedEvent event){
        Dotenv dotenv = Dotenv.configure()
                .directory("./src/main/resources/")
                .filename("local.env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        ConfigurableEnvironment environment = event.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        Properties properties = new Properties();

        setPropertyIfMissing(dotenv, properties, "JWT_SECRET");
        setPropertyIfMissing(dotenv, properties, "MONGODB_USER");
        setPropertyIfMissing(dotenv, properties, "MONGODB_PASSWORD");

        propertySources.addFirst(new PropertiesPropertySource("dotenvProperties", properties));
    }

    private void setPropertyIfMissing(Dotenv dotenv, Properties properties, String key) {
        String value = dotenv.get(key, System.getenv(key)); // Use env var if .env is missing
        if (value != null && !value.isEmpty()) {
            properties.setProperty(key, value);
        }
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
