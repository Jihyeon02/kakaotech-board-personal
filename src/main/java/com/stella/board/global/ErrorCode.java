package com.stella.board.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
// 예외처리를 위해 ErrorCode 정의하기 위해 만든 클래스
public enum ErrorCode{
    // Test Error
    //TEST_ERROR(HttpStatus.BAD_REQUEST, "테스트 에러입니다."),
    // 400 BAD_REQUEST : 잘못된 요청
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    // 404 Not Found
    NOT_FOUND_END_POINT(HttpStatus.NOT_FOUND, "존재하지 않는 API입니다."),
    // 405 METHOD_NOT_ALLOWED : 허용되지 않은 Request Method 호출
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메소드입니다."),
    // 유효하지 않은 토큰
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    // 유효하지 않은 회원 정보
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "일치하는 회원정보가 존재하지 않습니다."),
    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");


    private final HttpStatus httpStatus;
    private final String message;
}