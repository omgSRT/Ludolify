package com.omgsrt.Ludolify.shared.exception;

import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
}
