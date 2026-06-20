package com.stella.board.global.exception;

import com.stella.board.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import javax.security.sasl.AuthenticationException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static com.stella.board.global.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 클라이언트의 잘못된 입력 형식
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse response = new ErrorResponse(BAD_REQUEST, LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 찾을 수 없는 api -> 조회되지 않는 데이터
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoSuchElementException ex) {
        ErrorResponse response = new ErrorResponse(NOT_FOUND_END_POINT, LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 틀린 HTTP 메소드 요청
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse response = new ErrorResponse(METHOD_NOT_ALLOWED, LocalDateTime.now(),  ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 보안관련, 이미지 처리, 글자수 제한 등등 여기서 책임을 지는게 맞는지, 어떻게 해야할지 고민을 해보고 추후에 넣을 예정

    // 로그인 오류
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchMemberException(AuthenticationException ex) {
        ErrorResponse response = new ErrorResponse(INVALID_CREDENTIALS, LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 공통 예외처리 - 서버측 오류 -> 서버측 오류면 어디서 어떻게 오류가 낫는지 로그에 기록이 남아야 추적할 수 있으므로 log처리도 넣어둠.
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse response = new ErrorResponse(INTERNAL_SERVER_ERROR, LocalDateTime.now(), ex.getMessage());
        log.error(ex.getMessage(), ex.getStackTrace());
        return new ResponseEntity<>(response,  HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
