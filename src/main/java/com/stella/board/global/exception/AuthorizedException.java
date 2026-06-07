package com.stella.board.global.exception;


import com.stella.board.global.ErrorCode;
import org.springframework.http.HttpStatus;

public class AuthorizedException extends tempException{
    public AuthorizedException(String code) {
        super(code, HttpStatus.UNAUTHORIZED, ErrorCode.INTERNAL_SERVER_ERROR);
    }
}