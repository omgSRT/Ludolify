package com.omgsrt.Ludolify.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SecurityConfiguration {
    String[] PUBLIC_ENDPOINT_GET = {"/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/favicon.io"};
    String[] PUBLIC_ENDPOINT_POST = {"/authentication/**"};
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
                                                         CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                                                         RoleBasedAuthorizationManager roleBasedAuthorizationManager) {
        var publicMatchers = new OrServerWebExchangeMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, PUBLIC_ENDPOINT_GET),
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, PUBLIC_ENDPOINT_POST),
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.PUT, PUBLIC_ENDPOINT_PUT),
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.DELETE, PUBLIC_ENDPOINT_DELETE)
        );

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authManager);
        authenticationWebFilter.setServerAuthenticationConverter(authConverter);
        authenticationWebFilter.setRequiresAuthenticationMatcher(new NegatedServerWebExchangeMatcher(publicMatchers));

        return httpSecurity
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(auth -> {
                    auth.pathMatchers(HttpMethod.GET, PUBLIC_ENDPOINT_GET).permitAll();
                    auth.pathMatchers(HttpMethod.POST, PUBLIC_ENDPOINT_POST).permitAll();
                    auth.pathMatchers(HttpMethod.PUT, PUBLIC_ENDPOINT_PUT).permitAll();
                    auth.pathMatchers(HttpMethod.DELETE, PUBLIC_ENDPOINT_DELETE).permitAll();
                    auth.pathMatchers("/telegram/**").permitAll();
                    auth.pathMatchers("/role/**").access(roleBasedAuthorizationManager);
                    auth.anyExchange().authenticated();
                })
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
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
