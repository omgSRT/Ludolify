package com.omgsrt.Ludolify.shared.exception;

import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import com.omgsrt.Ludolify.shared.security.CustomAuthenticationEntryPoint;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    CustomAuthenticationEntryPoint entryPoint;

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException exception) {
        ErrorCode error = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(error.getCode());
        apiResponse.setMessage(error.getMessage());

        return ResponseEntity.status(error.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> details = exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        ErrorCode errorCode = details.values().stream()
                .map(ErrorCode::findByMessage)
                .findFirst()
                .orElse(ErrorCode.UNDEFINED_EXCEPTION);

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.<Map<String, String>>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .entity(details)
                        .build());
    }

    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public Mono<Void> handleSecurityException(ServerWebExchange exchange, Exception ex) {
        log.info("GlobalExceptionHandler: Handling exception for path: {}, exception: {}",
                exchange.getRequest().getPath(), ex.getMessage());
        return entryPoint.commence(exchange, (AuthenticationException) ex);
    }
}
