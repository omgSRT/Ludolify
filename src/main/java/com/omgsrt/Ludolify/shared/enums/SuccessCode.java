package com.omgsrt.Ludolify.shared.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum SuccessCode {
    SUCCESSFULLY_CREATED(20000, "Data Successfully Created", HttpStatus.CREATED),
    SUCCESSFULLY_GET_WITH_CONTENT(20001, "Data Successfully Retrieved", HttpStatus.OK),
    SUCCESSFULLY_GET_WITH_NO_CONTENT(20002, "Data Successfully Retrieved", HttpStatus.NO_CONTENT),
    SUCCESSFULLY_UPDATE(20003, "Data Successfully Updated", HttpStatus.OK),
    SUCCESSFULLY_DELETE(20004, "Data Successfully Deleted", HttpStatus.OK),
    SUCCESSFULLY_UPLOAD_NEW_DATA(20005, "Data Successfully Uploaded", HttpStatus.CREATED),
    SUCCESSFULLY_UPLOAD_BY_REPLACE_DATA(20006, "Data Successfully Uploaded", HttpStatus.OK),
    SUCCESSFULLY_DOWNLOAD(20007, "Data Successfully Downloaded", HttpStatus.OK),
    SUCCESSFULLY_LOGIN(20008, "Login Successfully", HttpStatus.OK),
    SUCCESSFULLY_SEND_MESSAGE(20009, "Send Message Successfully", HttpStatus.OK)
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    SuccessCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
