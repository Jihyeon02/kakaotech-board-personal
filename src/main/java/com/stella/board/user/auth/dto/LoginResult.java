package com.stella.board.user.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

//교재에 있던 코드

@Getter
@AllArgsConstructor
public class LoginResult {

    private LoginResponse response; // 응답 바디용
    private String refreshToken;    // 쿠키용
}