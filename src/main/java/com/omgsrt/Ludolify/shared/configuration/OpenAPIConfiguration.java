package com.omgsrt.Ludolify.shared.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenAPIConfiguration {
    @Bean
    public OpenAPI ludolify() {
        return new OpenAPI()
                .info(info())
                .externalDocs(new ExternalDocumentation()
                        .description("You Can Refer to The Product Server Wiki Documentation")
                        .url("null"))
                .servers(servers())
                .components(component());
    }

    private Info info() {
        Info info = new Info();
        info.setTitle("Ludolify API");
        info.setVersion("1.0.0");
        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0.html");
        info.setLicense(license);
        return info;
    }

    private List<Server> servers() {
        return Arrays.asList(
                new Server().url("http://docker:8080").description("Docker Server (placeholder)"),
                new Server().url("http://localhost:8080").description("Local server")
        );
    }

    private Components component() {
        final String securitySchemeName = "Bearer Authentication";
        Components components = new Components();
        components.addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
        );

        return components;
    }
}
