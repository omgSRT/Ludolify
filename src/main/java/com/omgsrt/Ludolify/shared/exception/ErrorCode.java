package com.omgsrt.Ludolify.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.Arrays;

@Getter
public enum ErrorCode {
    UNDEFINED_EXCEPTION(10000, "Undefined Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(10002, "You Are Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(10003, "You Are Unauthorized", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(10004, "Invalid Token", HttpStatus.BAD_REQUEST),
    INVALID_PAGE_NUMBER(10005, "Page Number Must Be Greater Than 0", HttpStatus.BAD_REQUEST),
    INVALID_PER_PAGE_NUMBER(10006, "Per Page Number Must Be Greater Than 0", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_LENGTH(10007, "Password Must Be At Least 8 Characters", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME_LENGTH(10008, "Username Must Be At Least 1 Character", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME_OR_PASSWORD(10009, "Wrong Username Or Password", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public static ErrorCode findByMessage(String message) {
        return Arrays.stream(ErrorCode.values())
                .filter(errorCode -> errorCode.getMessage().equalsIgnoreCase(message))
                .findFirst()
                .orElse(ErrorCode.UNDEFINED_EXCEPTION);
    }
}
