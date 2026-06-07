package com.stella.board.global.exception;

import com.stella.board.global.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class tempException extends RuntimeException{
    private final String code;
    private final HttpStatus status;
    private final ErrorCode errorCode;

    public tempException(String code, HttpStatus status, ErrorCode errorCode) {
        this.code = code;
        this.status = status;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return errorCode.getMessage();
    }
}
