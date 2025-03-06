package com.omgsrt.Ludolify.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import com.omgsrt.Ludolify.shared.exception.AppException;
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
        log.info("CustomAuthenticationEntryPoint: Path: {}, Exception: {}, Type: {}",
                exchange.getRequest().getPath(), ex.getMessage(), ex.getClass().getSimpleName());

        AppException appException = (ex.getCause() instanceof AppException)
                ? (AppException) ex.getCause()
                : new AppException(ErrorCode.UNAUTHORIZED);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(appException.getErrorCode().getCode())
                .message(appException.getMessage())
                .build();

        exchange.getResponse().setStatusCode(ErrorCode.UNAUTHORIZED.getStatusCode());
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(apiResponse))
                .flatMap(bytes -> exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))));
    }
}
