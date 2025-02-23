package com.omgsrt.Ludolify.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNDEFINED_EXCEPTION(1000, "Undefined Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(1002, "You Are Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "You Are Unauthorized", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1004, "Invalid Token", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
