package com.stella.board.user.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

// 교재에 있던 코드
@Getter
@AllArgsConstructor
public class TokenInfo {

    private String accessToken;
    private long expiresIn;
}
