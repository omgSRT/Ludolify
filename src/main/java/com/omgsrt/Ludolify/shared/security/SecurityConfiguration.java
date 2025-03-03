package com.omgsrt.Ludolify.shared.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import reactor.core.publisher.Flux;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfiguration {
    String[] PUBLIC_ENDPOINT_GET = {};
    String[] PUBLIC_ENDPOINT_POST = {"/authentication/**", ""};
    String[] PUBLIC_ENDPOINT_PUT = {};
    String[] PUBLIC_ENDPOINT_DELETE = {};

    CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange
                        //.pathMatchers(HttpMethod.GET, PUBLIC_ENDPOINT_GET).permitAll()
                        .pathMatchers(HttpMethod.POST, PUBLIC_ENDPOINT_POST).permitAll()
                        //.pathMatchers(HttpMethod.PUT, PUBLIC_ENDPOINT_PUT).permitAll()
                        //.pathMatchers(HttpMethod.DELETE, PUBLIC_ENDPOINT_DELETE).permitAll()
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/telegram/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec.authenticationEntryPoint((exchange, ex) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        })
                )
                .securityContextRepository(serverSecurityContextRepository())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    public ServerSecurityContextRepository serverSecurityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }

    @Bean
    static SecurityContextHolderStrategy securityContextHolderStrategy() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        return SecurityContextHolder.getContextHolderStrategy();
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        return new DefaultMethodSecurityExpressionHandler();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
            return Flux.fromIterable(authorities);
        });

        return converter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(16);
    }
}
