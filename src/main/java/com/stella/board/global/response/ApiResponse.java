package com.stella.board.global.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse<T> {

    private String message;
    private T data;

    // of() - 컨트롤러에서 ApiResponse.of("LOGIN_SUCCESS", result) 로 사용
    public static <T> ApiResponse<T> of(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.message = message;
        response.data = data;
        return response;
    }
}
