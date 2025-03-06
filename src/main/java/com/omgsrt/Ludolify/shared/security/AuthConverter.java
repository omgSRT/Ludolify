package com.omgsrt.Ludolify.shared.security;

import com.omgsrt.Ludolify.shared.exception.AppException;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.info("AuthConverter: Path: {}, Authorization header: {}", exchange.getRequest().getPath(), authHeader);

        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            return Mono.empty();
        }

        return Mono.justOrEmpty(authHeader)
                .filter(header -> header != null && header.toLowerCase().startsWith("bearer "))
                .switchIfEmpty(Mono.just("No Bearer token found"))
                .map(header -> {
                    String token = header.substring(7).trim();
                    log.info("AuthConverter: Extracted token: {}", token);
                    return new BearerToken(token);
                });
    }
}
