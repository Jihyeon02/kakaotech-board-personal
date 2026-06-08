package com.stella.board.global.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

//controller에 나와서 만들게 됨. 선언된 속성, 생성자의 파라미터도 컨트롤러 기준으로 작성.
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
