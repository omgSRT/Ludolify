package com.omgsrt.Ludolify.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        exchange.getResponse().setStatusCode(errorCode.getStatusCode());
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().remove(HttpHeaders.WWW_AUTHENTICATE);

        return Mono.just(apiResponse)
                .flatMap(response -> {
                    try {
                        byte[] jsonBytes = objectMapper.writeValueAsBytes(response);
                        log.info("Writing response: {}", new String(jsonBytes));
                        return exchange.getResponse().writeWith(
                                Mono.just(exchange.getResponse().bufferFactory().wrap(jsonBytes))
                        );
                    } catch (Exception e) {
                        log.error("Failed to serialize response", e);
                        return Mono.error(new RuntimeException("Failed to serialize response", e));
                    }
                });
    }
}
