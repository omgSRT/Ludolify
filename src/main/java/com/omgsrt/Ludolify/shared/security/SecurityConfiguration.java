package com.omgsrt.Ludolify.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

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
                                                         AuthManager authManager,
                                                         CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authManager);
        authenticationWebFilter.setServerAuthenticationConverter(authConverter);

        return httpSecurity
                .authorizeExchange(auth -> {
                    auth.pathMatchers(HttpMethod.GET, PUBLIC_ENDPOINT_GET).permitAll()
                            .pathMatchers(HttpMethod.POST, PUBLIC_ENDPOINT_POST).permitAll()
                            .pathMatchers(HttpMethod.PUT, PUBLIC_ENDPOINT_PUT).permitAll()
                            .pathMatchers(HttpMethod.DELETE, PUBLIC_ENDPOINT_DELETE).permitAll()
                            .pathMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                    "/swagger-resources/**", "/telegram/**").permitAll()
                            .anyExchange().authenticated();
                })
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(customAuthenticationEntryPoint)
                            .accessDeniedHandler((exchange, denied) -> {
                                ErrorCode errorCode = ErrorCode.FORBIDDEN;
                                ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                                        .code(errorCode.getCode())
                                        .message(errorCode.getMessage())
                                        .build();

                                exchange.getResponse().setStatusCode(errorCode.getStatusCode());
                                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                exchange.getResponse().getHeaders().remove(HttpHeaders.WWW_AUTHENTICATE);

                                try {
                                    byte[] jsonBytes = new ObjectMapper().writeValueAsBytes(apiResponse);
                                    return exchange.getResponse().writeWith(
                                            Mono.just(exchange.getResponse().bufferFactory().wrap(jsonBytes))
                                    );
                                } catch (Exception e) {
                                    return Mono.error(new RuntimeException("Failed to serialize access denied response", e));
                                }
                            });
                })
                .build();
    }
}
