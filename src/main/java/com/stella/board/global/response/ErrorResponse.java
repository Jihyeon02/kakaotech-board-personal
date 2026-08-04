package com.stella.board.global.response;

import com.stella.board.global.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private ErrorCode errorCode;
    private LocalDateTime timestamp;
    private String localMessage;


    public ErrorResponse(ErrorCode errorCode, LocalDateTime timestamp, String localMessage) {
        this.errorCode = errorCode;
        this.timestamp = timestamp;
        this.localMessage = localMessage;
    }




}
