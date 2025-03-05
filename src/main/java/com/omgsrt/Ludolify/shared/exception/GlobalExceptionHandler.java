package com.omgsrt.Ludolify.shared.exception;

import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
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
}
