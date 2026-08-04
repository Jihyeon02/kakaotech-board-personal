package com.stella.board.postImage;

import com.stella.board.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static com.stella.board.global.ErrorCode.*;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(
        assignableTypes = PostImageController.class
)
public class PostImageExceptionHandler {

    @ExceptionHandler(InvalidPostImageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImage(
            InvalidPostImageException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                BAD_REQUEST,
                LocalDateTime.now(),
                exception.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(PostImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleImageNotFound(
            PostImageNotFoundException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                NOT_FOUND_END_POINT,
                LocalDateTime.now(),
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(PostImageUploadException.class)
    public ResponseEntity<ErrorResponse> handleUploadFailure(
            PostImageUploadException exception
    ) {
        log.error("S3 이미지 업로드 실패", exception);

        ErrorResponse response = new ErrorResponse(
                INTERNAL_SERVER_ERROR,
                LocalDateTime.now(),
                "이미지 업로드 중 오류가 발생했습니다."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}