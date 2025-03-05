package com.omgsrt.Ludolify.shared.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfiguration {
    String[] PUBLIC_ENDPOINT_GET = {""};
    String[] PUBLIC_ENDPOINT_POST = {"/authentication/**", ""};
    String[] PUBLIC_ENDPOINT_PUT = {""};
    String[] PUBLIC_ENDPOINT_DELETE = {""};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(16);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity,
                                                         AuthConverter authConverter,
                                                         AuthManager authManager) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authManager);
        authenticationWebFilter.setServerAuthenticationConverter(authConverter);

        return httpSecurity
                .authorizeExchange(auth -> {
                    auth.pathMatchers(HttpMethod.GET, PUBLIC_ENDPOINT_GET).permitAll()
                            .pathMatchers(HttpMethod.POST, PUBLIC_ENDPOINT_POST).permitAll()
                            .pathMatchers(HttpMethod.PUT, PUBLIC_ENDPOINT_PUT).permitAll()
                            .pathMatchers(HttpMethod.DELETE, PUBLIC_ENDPOINT_DELETE).permitAll()
                            .pathMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                    "/swagger-resources/**", "/webjars/**", "/telegram/**").permitAll()
                            .anyExchange().authenticated();
                })
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
