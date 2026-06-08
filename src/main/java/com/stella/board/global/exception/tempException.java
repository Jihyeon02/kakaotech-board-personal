package com.stella.board.global.exception;

import com.stella.board.global.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
// 일단 AuthorizedException의 부모 클래스가 필요하여 만들게 된 임시 예외 처리 클래스
@Getter
public class tempException extends RuntimeException{
    // AuthorizedException에 필요한 속성들 다 정의
    private final String code;
    private final HttpStatus status;
    // 이걸 만들어 쓰다보니 Errorcode에 대한 클래스도 필요했음-> global/Errcode.java
    private final ErrorCode errorCode;

    // 생성자 <- AuthorizedException에서 상속해서 사용할 수 있도록 만듦
    public tempException(String code, HttpStatus status, ErrorCode errorCode) {
        this.code = code;
        this.status = status;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return errorCode.getMessage();
    }
}
