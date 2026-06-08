package com.stella.board.global.exception;


import com.stella.board.global.ErrorCode;
import org.springframework.http.HttpStatus;

// AuthService에서 필요한 클래스라 만들었는데 super을 보니 이 클래스의 부모클래스를 또 만들게 됨.
public class AuthorizedException extends tempException{
    public AuthorizedException(String code) {
        super(code, HttpStatus.UNAUTHORIZED, ErrorCode.INTERNAL_SERVER_ERROR);
    }
}